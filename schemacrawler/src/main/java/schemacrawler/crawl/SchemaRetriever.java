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


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;

final class SchemaRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(SchemaRetriever.class
    .getName());

  private final boolean supportsCatalogs;

  SchemaRetriever(final RetrieverConnection retrieverConnection,
                  final MutableDatabase database)
    throws SchemaCrawlerException
  {
    super(retrieverConnection, database);
    try
    {
      supportsCatalogs = getMetaData().supportsCatalogsInTableDefinitions();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Could ", e);
    }
  }

  /**
   * Retrieves a list of schemas from the database.
   */
  void retrieveSchemas(final InclusionRule catalogInclusionRule,
                       final InclusionRule schemaInclusionRule)
    throws SQLException
  {

    final Set<String> catalogNames = retrieveCatalogs(catalogInclusionRule);
    final Set<SchemaReference> schemaRefs = new HashSet<SchemaReference>();

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
        final List<SchemaReference> candidateSchemaRefs = new ArrayList<SchemaReference>();
        if (catalogNames.contains(catalogName))
        {
          candidateSchemaRefs.add(new SchemaReference(catalogName, schemaName));
        }
        else
        {
          for (final String candidateCatalogName: catalogNames)
          {
            candidateSchemaRefs.add(new SchemaReference(candidateCatalogName,
                                                        schemaName));
          }
        }
        for (final SchemaReference schemaReference: candidateSchemaRefs)
        {
          if (schemaInclusionRule != null
              && schemaInclusionRule.include(schemaReference.getFullName()))
          {
            schemaRefs.add(schemaReference);
          }
        }
      }
    }
    finally
    {
      results.close();
    }

    final Map<String, MutableCatalog> catalogNamesMap = new HashMap<String, MutableCatalog>();
    for (final String catalogName: catalogNames)
    {
      final MutableCatalog catalog = new MutableCatalog(database, catalogName);
      catalogNamesMap.put(catalogName, catalog);
      database.addCatalog(catalog);
    }
    // Create schemas for the catalogs, as well as create the schema
    // reference cache
    for (final SchemaReference schemaRef: schemaRefs)
    {
      final MutableCatalog catalog = catalogNamesMap.get(schemaRef
        .getCatalogName());
      final MutableSchema schema = new MutableSchema(catalog, schemaRef
        .getSchemaName());
      getRetrieverConnection().cacheSchema(schemaRef, schema);
      catalog.addSchema(schema);
    }
    // Ensure that each catalog has at least one schema
    for (final MutableCatalog catalog: catalogNamesMap.values())
    {
      if (catalog.getSchemas().length == 0)
      {
        final MutableSchema schema = new MutableSchema(catalog, null);
        getRetrieverConnection().cacheSchema(new SchemaReference(catalog
                                               .getName(), null),
                                             schema);
        catalog.addSchema(schema);
      }
    }
  }

  /**
   * Retrieves catalog metadata according to the parameters specified.
   * 
   * @return A list of catalogs in the database that matches the pattern
   */
  private Set<String> retrieveCatalogs(final InclusionRule catalogInclusionRule)
  {
    final Set<String> catalogNames = new HashSet<String>();

    String connectionCatalog;
    try
    {
      connectionCatalog = getDatabaseConnection().getCatalog();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "", e);
      connectionCatalog = null;
    }

    if (supportsCatalogs)
    {
      try
      {
        catalogNames.addAll(readResultsVector(getMetaData().getCatalogs()));
        catalogNames.add(connectionCatalog);
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "", e);
      }
      LOGGER.log(Level.FINE, "All catalogs: " + catalogNames);

      for (final Iterator<String> catalogNamesIterator = catalogNames
        .iterator(); catalogNamesIterator.hasNext();)
      {
        final String catalogName = catalogNamesIterator.next();
        if (catalogInclusionRule != null
            && !catalogInclusionRule.include(catalogName))
        {
          catalogNamesIterator.remove();
        }
      }
    }
    if (catalogNames.isEmpty())
    {
      if (supportsCatalogs)
      {
        catalogNames.add(connectionCatalog);
      }
      else
      {
        catalogNames.add(null);
      }
    }

    return catalogNames;
  }

}
