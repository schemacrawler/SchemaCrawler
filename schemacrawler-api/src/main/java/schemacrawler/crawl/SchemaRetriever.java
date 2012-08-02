/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
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
import java.util.logging.Logger;

import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;

final class SchemaRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(SchemaRetriever.class
    .getName());

  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;

  SchemaRetriever(final RetrieverConnection retrieverConnection,
                  final MutableDatabase database)
    throws SQLException
  {
    super(retrieverConnection, database);

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
    final Set<SchemaReference> schemaRefs;

    // Prefer to retrieve schemas from the INFORMATION_SCHEMA views
    schemaRefs = retrieveAllSchemasFromInformationSchemaViews();
    if (schemaRefs.isEmpty())
    {
      schemaRefs.addAll(retrieveAllSchemas());
    }

    // Filter out schemas
    for (final Iterator<SchemaReference> iterator = schemaRefs.iterator(); iterator
      .hasNext();)
    {
      final SchemaReference schemaRef = iterator.next();
      final String schemaFullName = schemaRef.getFullName();
      if (schemaInclusionRule != null && schemaFullName != null
          && !schemaInclusionRule.include(schemaFullName))
      {
        LOGGER.log(Level.FINER, "Dropping schema, since schema is excluded: "
                                + schemaRef.getFullName());
        iterator.remove();
        // continue
      }
    }

    // Create schemas for the catalogs, as well as create the schema
    // reference cache
    for (final SchemaReference schemaRef: schemaRefs)
    {
      database.addSchema(schemaRef);
    }

    // Add an empty schema reference for databases that do not support
    // neither catalogs nor schemas
    if (!supportsCatalogs && !supportsSchemas)
    {
      database.addSchema(new SchemaReference(null, null));
    }

  }

  /**
   * Retrieves all catalog names.
   * 
   * @return All catalog names in the database
   */
  private Set<String> retrieveAllCatalogs()
  {
    final Set<String> catalogNames = new HashSet<String>();

    if (supportsCatalogs)
    {
      try
      {
        final List<String> metaDataCatalogNames = readResultsVector(getMetaData()
          .getCatalogs());
        for (final String catalogName: metaDataCatalogNames)
        {
          catalogNames.add(quotedName(catalogName));
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, e.getMessage(), e);
      }
      LOGGER.log(Level.FINER, "Retrieved catalogs: " + catalogNames);
    }

    return catalogNames;
  }

  private Set<SchemaReference> retrieveAllSchemas()
    throws SQLException
  {
    final Set<SchemaReference> schemaRefs = new HashSet<SchemaReference>();
    final Set<String> allCatalogNames = retrieveAllCatalogs();
    if (supportsSchemas)
    {
      final MetadataResultSet results = new MetadataResultSet(getMetaData()
        .getSchemas());
      try
      {
        while (results.next())
        {
          final String catalogName;
          if (supportsCatalogs)
          {
            catalogName = quotedName(results.getString("TABLE_CATALOG"));
          }
          else
          {
            catalogName = null;
          }
          final String schemaName = quotedName(results.getString("TABLE_SCHEM"));
          LOGGER.log(Level.FINER, String.format("Retrieving schema: %s --> %s",
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
                schemaRefs.add(new SchemaReference(expectedCatalogName,
                                                   schemaName));
              }
            }
          }
          else
          {
            schemaRefs.add(new SchemaReference(catalogName, schemaName));
          }
        }
      }
      finally
      {
        results.close();
      }
    }
    else
    {
      for (final String catalogName: allCatalogNames)
      {
        LOGGER.log(Level.FINER, String.format("Retrieving schema: %s --> %s",
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
    final Set<SchemaReference> schemaRefs = new HashSet<SchemaReference>();

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasSchemataSql())
    {
      LOGGER.log(Level.FINE, "Schemata SQL statement was not provided");
      return schemaRefs;
    }
    final String schemataSql = informationSchemaViews.getSchemataSql();

    final Connection connection = getDatabaseConnection();
    final Statement statement = connection.createStatement();
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement.executeQuery(schemataSql));
      while (results.next())
      {
        final String catalogName = quotedName(results.getString("CATALOG_NAME"));
        final String schemaName = quotedName(results.getString("SCHEMA_NAME"));
        LOGGER.log(Level.FINER, String.format("Retrieving schema: %s --> %s",
                                              catalogName,
                                              schemaName));
        schemaRefs.add(new SchemaReference(catalogName, schemaName));
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve schemas", e);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
      statement.close();
    }

    return schemaRefs;
  }

}
