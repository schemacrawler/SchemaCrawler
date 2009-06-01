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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.InclusionRule;

/**
 * A retriever uses database metadata to get the details about the
 * database catalogs and schemas.
 * 
 * @author Sualeh Fatehi
 */
final class DatabaseRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(DatabaseRetriever.class
    .getName());

  DatabaseRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Retrieves a list of schemas from the database, for the table
   * specified.
   * 
   * @param table
   *        Catalog for which data is required.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveSchemas(final NamedObjectList<MutableCatalog> catalogs,
                       final InclusionRule schemaInclusionRule,
                       final NamedObjectList<MutableSchema> schemas)
    throws SQLException
  {
    final MetadataResultSet results = new MetadataResultSet(getRetrieverConnection()
      .getMetaData().getSchemas());
    try
    {
      while (results.next())
      {
        final String catalogName = results.getString("TABLE_CATALOG");
        final String schemaName = results.getString("TABLE_SCHEM");

        final Catalog catalog = catalogs.lookup(catalogName);
        final MutableSchema schema = new MutableSchema(catalog, schemaName);
        final String schemaFullName = schema.getFullName();
        if (schemaInclusionRule.include(schemaFullName))
        {
          LOGGER.log(Level.FINEST, "Retrieving schema: " + schemaName);
          schemas.add(schema);
        }
      }
    }
    finally
    {
      results.close();
    }

  }

  /**
   * Retrieves catalog metadata according to the parameters specified.
   * 
   * @return A list of catalogs in the database that matech the pattern
   */
  void retrieveCatalogs(final NamedObjectList<MutableCatalog> catalogs)
  {
    final List<String> catalogNames = getRetrieverConnection()
      .getCatalogNames();
    for (final String catalogName: catalogNames)
    {
      LOGGER.log(Level.FINEST, "Retrieving catalog: " + catalogName);
      final MutableCatalog catalog = new MutableCatalog(catalogName);
      catalogs.add(catalog);
    }
  }

}
