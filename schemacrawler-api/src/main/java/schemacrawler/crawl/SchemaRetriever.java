/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.MetadataResultSet;
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
    LOGGER.log(Level.FINER, new StringFormat("Retrieved schemas <%s>", schemaRefs));

    // Filter out schemas
    final Identifiers identifiers = getRetrieverConnection().getIdentifiers();
    for (final Iterator<SchemaReference> iterator = schemaRefs.iterator(); iterator.hasNext(); ) {
      final SchemaReference schemaRef = iterator.next();
      schemaRef.withQuoting(identifiers);
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
      final String name = "catalogs";
      final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final ResultSet catalogsResults = connection.getMetaData().getCatalogs(); ) {
        final List<String> metaDataCatalogNames = readResultsVector(catalogsResults);
        for (final String catalogName : metaDataCatalogNames) {
          retrievalCounts.count();
          catalogNames.add(catalogName);
          retrievalCounts.countIncluded();
        }
      } catch (final SQLException e) {
        LOGGER.log(Level.WARNING, e.getMessage(), e);
      }
      retrievalCounts.log();
      LOGGER.log(Level.FINER, new StringFormat("Retrieved catalogs <%s>", catalogNames));
    }

    return catalogNames;
  }

  private Set<SchemaReference> retrieveAllSchemas() throws SQLException {
    LOGGER.log(Level.INFO, "Retrieving all schemas");

    final Set<SchemaReference> schemaRefs = new HashSet<>();
    final Set<String> allCatalogNames = retrieveAllCatalogs();
    if (supportsSchemas) {
      final String name = "schemas";
      final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection.getMetaData().getSchemas(), "DatabaseMetaData::getSchemas"); ) {
        while (results.next()) {
          retrievalCounts.count();
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
          retrievalCounts.countIncluded();
        }
      }
      retrievalCounts.log();
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
    final String name = "schemas from data dictionary";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query schemataSql = informationSchemaViews.getQuery(SCHEMATA);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(schemataSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = results.getString("CATALOG_NAME");
        final String schemaName = results.getString("SCHEMA_NAME");
        LOGGER.log(
            Level.FINER, new StringFormat("Retrieving schema: %s --> %s", catalogName, schemaName));
        schemaRefs.add(new SchemaReference(catalogName, schemaName));
        retrievalCounts.countIncluded();
      }
      retrievalCounts.log();
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve schemas", e);
    }

    return schemaRefs;
  }
}
