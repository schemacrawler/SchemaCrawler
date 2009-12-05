/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.InclusionRule;

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

    final DatabaseMetaData dbMetaData = getMetaData();

    supportsCatalogs = dbMetaData.supportsCatalogsInTableDefinitions();
    LOGGER.log(Level.FINE, String
      .format("Database %s catalogs", (supportsCatalogs? "supports"
                                                       : "does not support")));

    supportsSchemas = dbMetaData.supportsSchemasInTableDefinitions();
    LOGGER
      .log(Level.FINE, String.format("Database %s schemas",
                                     (supportsSchemas? "supports"
                                                     : "does not support")));
  }

  /**
   * Retrieves a list of schemas from the database.
   */
  void retrieveSchemas(final InclusionRule catalogInclusionRule,
                       final InclusionRule schemaInclusionRule)
    throws SQLException
  {

    final Set<SchemaReference> schemaRefs = new HashSet<SchemaReference>();

    final Set<String> allCatalogNames = retrieveAllCatalogNames();

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
            catalogName = results.getString("TABLE_CATALOG");
          }
          else
          {
            catalogName = null;
          }
          final String schemaName = results.getString("TABLE_SCHEM");
          LOGGER.log(Level.FINER, String.format("Retrieving schema: %s --> %s",
                                                catalogName,
                                                schemaName));
          if (catalogName == null)
          {
            if (allCatalogNames.isEmpty())
            {
              schemaRefs.add(new SchemaReference(catalogName, schemaName));
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

    // Filter out schemas
    for (final Iterator<SchemaReference> iterator = schemaRefs.iterator(); iterator
      .hasNext();)
    {
      final SchemaReference schemaRef = iterator.next();

      final String catalogName = schemaRef.getCatalogName();
      if (catalogInclusionRule != null && catalogName != null
          && !catalogInclusionRule.include(catalogName))
      {
        LOGGER.log(Level.FINER, "Dropping schema, since catalog is excluded: "
                                + schemaRef.getFullName());
        iterator.remove();
        continue;
      }

      final String schemaFullName = schemaRef.getFullName();
      if (schemaInclusionRule != null && schemaFullName != null
          && !schemaInclusionRule.include(schemaFullName))
      {
        LOGGER.log(Level.FINER, "Dropping schema, since schema is excluded: "
                                + schemaRef.getFullName());
        iterator.remove();
        continue;
      }
    }

    // Create catalogs in the database, along with a lookup map
    final Map<String, MutableCatalog> catalogNamesMap = new HashMap<String, MutableCatalog>();
    for (final SchemaReference schemaRef: schemaRefs)
    {
      final String catalogName = schemaRef.getCatalogName();
      if (!catalogNamesMap.containsKey(catalogName))
      {
        final MutableCatalog catalog = new MutableCatalog(database, catalogName);
        database.addCatalog(catalog);
        catalogNamesMap.put(catalogName, catalog);
      }
    }
    // Create schemas for the catalogs, as well as create the schema
    // reference cache
    for (final SchemaReference schemaRef: schemaRefs)
    {
      final MutableCatalog catalog = catalogNamesMap.get(schemaRef
        .getCatalogName());
      final MutableSchema schema = new MutableSchema(catalog, schemaRef
        .getSchemaName());
      catalog.addSchema(schema);
      getRetrieverConnection().cacheSchema(schemaRef, schema);
    }

    // Ensure that each catalog has at least one schema
    for (final MutableCatalog catalog: catalogNamesMap.values())
    {
      if (!catalog.hasSchemas())
      {
        final MutableSchema schema = new MutableSchema(catalog, null);
        catalog.addSchema(schema);
        getRetrieverConnection().cacheSchema(new SchemaReference(catalog
                                               .getName(), null),
                                             schema);
      }
    }
  }

  /**
   * Retrieves all catalog names.
   * 
   * @return All catalog names in the database
   */
  private Set<String> retrieveAllCatalogNames()
  {
    final Set<String> catalogNames = new HashSet<String>();

    String connectionCatalog;
    try
    {
      connectionCatalog = getDatabaseConnection().getCatalog();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      connectionCatalog = null;
    }
    catalogNames.add(connectionCatalog);

    if (supportsCatalogs)
    {
      try
      {
        catalogNames.addAll(readResultsVector(getMetaData().getCatalogs()));
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, e.getMessage(), e);
      }
      LOGGER.log(Level.FINE, "All catalogs: " + catalogNames);
    }

    return catalogNames;
  }

}
