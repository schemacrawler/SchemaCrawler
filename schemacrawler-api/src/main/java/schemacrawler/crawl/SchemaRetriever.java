/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.Query;
import sf.util.DatabaseUtility;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

final class SchemaRetriever
  extends AbstractRetriever
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaRetriever.class.getName());

  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;

  SchemaRetriever(final RetrieverConnection retrieverConnection,
                  final MutableCatalog catalog,
                  final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);

    supportsCatalogs = retrieverConnection.isSupportsCatalogs();
    supportsSchemas = retrieverConnection.isSupportsSchemas();
  }

  /**
   * Retrieves a list of schemas from the database.
   *
   * @param schemaInclusionRule
   *        Schema inclusion rule
   * @throws SQLException
   *         On an exception
   */
  void retrieveSchemas(final InclusionRule schemaInclusionRule)
    throws SQLException
  {
    final InclusionRuleFilter<Schema> schemaFilter = new InclusionRuleFilter<>(schemaInclusionRule,
                                                                               true);

    if (schemaFilter.isExcludeAll())
    {
      return;
    }

    final Set<SchemaReference> schemaRefs;

    // Prefer to retrieve schemas from the INFORMATION_SCHEMA views
    schemaRefs = retrieveAllSchemasFromInformationSchemaViews();
    if (schemaRefs.isEmpty())
    {
      schemaRefs.addAll(retrieveAllSchemas());
    }

    // Filter out schemas
    for (final Iterator<SchemaReference> iterator = schemaRefs
      .iterator(); iterator.hasNext();)
    {
      final SchemaReference schemaRef = iterator.next();
      if (!schemaFilter.test(schemaRef))
      {
        LOGGER.log(Level.FINER,
                   new StringFormat("Excluding schema <%s>",
                                    schemaRef.getFullName()));
        iterator.remove();
        // continue
      }
    }

    // Create schemas for the catalogs, as well as create the schema
    // reference cache
    for (final SchemaReference schemaRef: schemaRefs)
    {
      catalog.addSchema(schemaRef);
    }

    // Add an empty schema reference for databases that do not support
    // neither catalogs nor schemas
    if (!supportsCatalogs && !supportsSchemas)
    {
      catalog.addSchema(new SchemaReference(null, null));
    }

    LOGGER.log(Level.INFO,
               new StringFormat("Retrieved %d schemas",
                                catalog.getSchemas().size()));
  }

  /**
   * Retrieves all catalog names.
   *
   * @return All catalog names in the database
   */
  private Set<String> retrieveAllCatalogs()
  {
    LOGGER.log(Level.INFO, "Retrieving all catalogs");

    final Set<String> catalogNames = new HashSet<>();

    if (supportsCatalogs)
    {
      try
      {
        final List<String> metaDataCatalogNames = DatabaseUtility
          .readResultsVector(getMetaData().getCatalogs());
        for (final String catalogName: metaDataCatalogNames)
        {
          catalogNames.add(nameQuotedName(catalogName));
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, e.getMessage(), e);
      }
      LOGGER.log(Level.FINER,
                 new StringFormat("Retrieved catalogs <%s>", catalogNames));
    }

    return catalogNames;
  }

  private Set<SchemaReference> retrieveAllSchemas()
    throws SQLException
  {
    LOGGER.log(Level.INFO, "Retrieving all schemas");

    final Set<SchemaReference> schemaRefs = new HashSet<>();
    final Set<String> allCatalogNames = retrieveAllCatalogs();
    if (supportsSchemas)
    {
      try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
        .getSchemas());)
      {
        results.setDescription("retrieveAllSchemas");
        while (results.next())
        {
          final String catalogName;
          if (supportsCatalogs)
          {
            catalogName = nameQuotedName(results.getString("TABLE_CATALOG"));
          }
          else
          {
            catalogName = null;
          }
          final String schemaName = nameQuotedName(results
            .getString("TABLE_SCHEM"));
          LOGGER.log(Level.FINER,
                     new StringFormat("Retrieving schema: %s --> %s",
                                      catalogName,
                                      schemaName));
          if (catalogName == null)
          {
            if (allCatalogNames.isEmpty())
            {
              schemaRefs.add(new SchemaReference(null, schemaName));
            }
            else
            {
              for (final String expectedCatalogName: allCatalogNames)
              {
                schemaRefs
                  .add(new SchemaReference(expectedCatalogName, schemaName));
              }
            }
          }
          else
          {
            schemaRefs.add(new SchemaReference(catalogName, schemaName));
          }
        }
      }
    }
    else
    {
      for (final String catalogName: allCatalogNames)
      {
        LOGGER.log(Level.FINER,
                   new StringFormat("Retrieving schema: %s --> %s",
                                    catalogName,
                                    null));
        schemaRefs.add(new SchemaReference(catalogName, null));
      }
    }
    return schemaRefs;
  }

  private Set<SchemaReference> retrieveAllSchemasFromInformationSchemaViews()
    throws SQLException
  {
    final Set<SchemaReference> schemaRefs = new HashSet<>();

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasSchemataSql())
    {
      LOGGER.log(Level.FINE, "Schemata SQL statement was not provided");
      return schemaRefs;
    }
    final Query schemataSql = informationSchemaViews.getSchemataSql();

    final Connection connection = getDatabaseConnection();

    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet(schemataSql,
                                                                statement,
                                                                getSchemaInclusionRule());)
    {
      results.setDescription("retrieveAllSchemasFromInformationSchemaViews");
      while (results.next())
      {
        final String catalogName = nameQuotedName(results
          .getString("CATALOG_NAME"));
        final String schemaName = nameQuotedName(results.getString("SCHEMA_NAME"));
        LOGGER.log(Level.FINER,
                   new StringFormat("Retrieving schema: %s --> %s",
                                    catalogName,
                                    schemaName));
        schemaRefs.add(new SchemaReference(catalogName, schemaName));
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve schemas", e);
    }

    return schemaRefs;
  }

}
