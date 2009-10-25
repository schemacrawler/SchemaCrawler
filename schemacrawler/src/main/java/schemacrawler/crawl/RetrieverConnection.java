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


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

/**
 * A connection for the retriever. Wraps a live database connection.
 * 
 * @author Sualeh Fatehi
 */
final class RetrieverConnection
{

  private static final Logger LOGGER = Logger
    .getLogger(RetrieverConnection.class.getName());

  private final Connection connection;
  private final DatabaseMetaData metaData;
  private final List<String> catalogNames;
  private final String schemaPattern;
  private final InformationSchemaViews informationSchemaViews;
  private final boolean supportsCatalogs;

  RetrieverConnection(final Connection connection,
                      final SchemaCrawlerOptions options)
    throws SchemaCrawlerException, SQLException
  {
    SchemaCrawlerOptions schemaCrawlerOptions = options;
    if (schemaCrawlerOptions == null)
    {
      schemaCrawlerOptions = new SchemaCrawlerOptions();
    }
    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection provided");
    }
    if (connection.isClosed())
    {
      throw new SchemaCrawlerException("Connection is closed");
    }
    this.connection = connection;
    metaData = connection.getMetaData();

    supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
    final Set<String> catalogNames = new HashSet<String>();
    if (supportsCatalogs)
    {
      try
      {
        catalogNames.addAll(new HashSet<String>(readResultsVector(metaData
          .getCatalogs())));
        catalogNames.add(connection.getCatalog());
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
        final InclusionRule catalogInclusionRule = options
          .getCatalogInclusionRule();
        if (catalogInclusionRule != null
            && !catalogInclusionRule.include(catalogName))
        {
          catalogNamesIterator.remove();
        }
      }
    }
    if (catalogNames.size() == 0)
    {
      catalogNames.add(connection.getCatalog());
    }
    final ArrayList<String> catalogNamesList = new ArrayList<String>(catalogNames);
    Collections.sort(catalogNamesList);
    this.catalogNames = Collections.unmodifiableList(catalogNamesList);

    schemaPattern = schemaCrawlerOptions.getSchemaPattern();

    informationSchemaViews = schemaCrawlerOptions.getInformationSchemaViews();
  }

  List<String> getCatalogNames()
  {
    return catalogNames;
  }

  Connection getConnection()
  {
    return connection;
  }

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   * 
   * @return INFORMATION_SCHEMA views selects
   */
  InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  String getSchemaPattern()
  {
    return schemaPattern;
  }

  boolean isSupportsCatalogs()
  {
    return supportsCatalogs;
  }

  /**
   * Reads a single column result set as a list.
   * 
   * @param results
   *        Result set
   * @return List
   * @throws SQLException
   */
  List<String> readResultsVector(final ResultSet results)
    throws SQLException
  {
    final List<String> values = new ArrayList<String>();
    try
    {
      while (results.next())
      {
        final String value = results.getString(1);
        values.add(value);
      }
    }
    finally
    {
      results.close();
    }
    return values;
  }

}
