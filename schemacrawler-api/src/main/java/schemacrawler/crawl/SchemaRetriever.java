/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.SCHEMATA;
import static us.fatehi.utility.database.DatabaseUtility.readResultsVector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import us.fatehi.utility.string.StringFormat;

final class SchemaRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(SchemaRetriever.class.getName());

  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;

  SchemaRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);

    supportsCatalogs = retrieverConnection.isSupportsCatalogs();
    supportsSchemas = retrieverConnection.isSupportsSchemas();
  }

  /**
   * Retrieves a list of schemas from the database.
   *
   * @param schemaInclusionRule Schema inclusion rule
   * @throws SQLException On an exception
   */
  void retrieveSchemas(final InclusionRule schemaInclusionRule) throws SQLException {
    final InclusionRuleFilter<Schema> schemaFilter =
        new InclusionRuleFilter<>(schemaInclusionRule, true);

    if (schemaFilter.isExcludeAll()) {
      return;
    }

    final Set<SchemaReference> schemaRefs;

    // Prefer to retrieve schemas from the INFORMATION_SCHEMA views
    schemaRefs = retrieveAllSchemasFromInformationSchemaViews();
    if (schemaRefs.isEmpty()) {
      schemaRefs.addAll(retrieveAllSchemas());
    }

    // Filter out schemas
    for (final Iterator<SchemaReference> iterator = schemaRefs.iterator(); iterator.hasNext(); ) {
      final SchemaReference schemaRef = iterator.next();
      if (!schemaFilter.test(schemaRef)) {
        LOGGER.log(Level.FINER, new StringFormat("Excluding schema <%s>", schemaRef));
        iterator.remove();
        // continue
      }
    }

    // Create schemas for the catalogs, as well as create the schema
    // reference cache
    for (final SchemaReference schemaRef : schemaRefs) {
      catalog.addSchema(schemaRef);
    }

    // Add an empty schema reference for databases that do not support
    // neither catalogs nor schemas
    if (!supportsCatalogs && !supportsSchemas) {
      catalog.addSchema(new SchemaReference(null, null));
    }
  }

  /**
   * Retrieves all catalog names.
   *
   * @return All catalog names in the database
   */
  private Set<String> retrieveAllCatalogs() {
    LOGGER.log(Level.INFO, "Retrieving all catalogs");

    final Set<String> catalogNames = new HashSet<>();

    if (supportsCatalogs) {
      try (final ResultSet catalogsResults = getMetaData().getCatalogs()) {
        int numCatalogs = 0;
        final List<String> metaDataCatalogNames = readResultsVector(catalogsResults);
        for (final String catalogName : metaDataCatalogNames) {
          numCatalogs = numCatalogs + 1;
          catalogNames.add(catalogName);
        }
        LOGGER.log(Level.INFO, new StringFormat("Processed %d catalogs", numCatalogs));
      } catch (final SQLException e) {
        LOGGER.log(Level.WARNING, e.getMessage(), e);
      }
      LOGGER.log(Level.FINER, new StringFormat("Retrieved catalogs <%s>", catalogNames));
    }

    return catalogNames;
  }

  private Set<SchemaReference> retrieveAllSchemas() throws SQLException {
    LOGGER.log(Level.INFO, "Retrieving all schemas");

    final Set<SchemaReference> schemaRefs = new HashSet<>();
    final Set<String> allCatalogNames = retrieveAllCatalogs();
    if (supportsSchemas) {
      int numSchemas = 0;
      try (final MetadataResultSet results =
          new MetadataResultSet(getMetaData().getSchemas(), "DatabaseMetaData::getSchemas")) {
        while (results.next()) {
          numSchemas = numSchemas + 1;
          final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
          final String schemaName = results.getString("TABLE_SCHEM");
          LOGGER.log(
              Level.FINER,
              new StringFormat("Retrieving schema: %s --> %s", catalogName, schemaName));
          if (catalogName == null) {
            if (allCatalogNames.isEmpty()) {
              schemaRefs.add(new SchemaReference(null, schemaName));
            } else {
              for (final String expectedCatalogName : allCatalogNames) {
                schemaRefs.add(new SchemaReference(expectedCatalogName, schemaName));
              }
            }
          } else {
            schemaRefs.add(new SchemaReference(catalogName, schemaName));
          }
        }
      }
      LOGGER.log(Level.INFO, new StringFormat("Processed %d schemas", numSchemas));
    } else {
      for (final String catalogName : allCatalogNames) {
        LOGGER.log(
            Level.FINER, new StringFormat("Retrieving schema: %s --> %s", catalogName, null));
        schemaRefs.add(new SchemaReference(catalogName, null));
      }
    }
    return schemaRefs;
  }

  private Set<SchemaReference> retrieveAllSchemasFromInformationSchemaViews() throws SQLException {
    final Set<SchemaReference> schemaRefs = new HashSet<>();

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(SCHEMATA)) {
      LOGGER.log(Level.FINE, "Schemata SQL statement was not provided");
      return schemaRefs;
    }
    final Query schemataSql = informationSchemaViews.getQuery(SCHEMATA);

    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(schemataSql, statement, getSchemaInclusionRule()); ) {
      int numSchemas = 0;
      while (results.next()) {
        numSchemas = numSchemas + 1;
        final String catalogName = results.getString("CATALOG_NAME");
        final String schemaName = results.getString("SCHEMA_NAME");
        LOGGER.log(
            Level.FINER, new StringFormat("Retrieving schema: %s --> %s", catalogName, schemaName));
        schemaRefs.add(new SchemaReference(catalogName, schemaName));
      }
      LOGGER.log(Level.INFO, new StringFormat("Processed %d schemas", numSchemas));
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve schemas", e);
    }

    return schemaRefs;
  }
}
