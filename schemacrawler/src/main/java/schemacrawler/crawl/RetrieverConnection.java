/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  private final DatabaseMetaData metaData;
  private final String catalogName;
  private final String schemaPattern;
  private final InformationSchemaViews informationSchemaViews;

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
    metaData = connection.getMetaData();

    String catalogFromConnection;
    try
    {
      catalogFromConnection = connection.getCatalog();
    }
    catch (final SQLException e)
    {
      catalogFromConnection = null;
      LOGGER.log(Level.WARNING, "", e);
    }
    catalogName = catalogFromConnection;
    schemaPattern = schemaCrawlerOptions.getSchemaPattern();

    informationSchemaViews = schemaCrawlerOptions.getInformationSchemaViews();
  }

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   * 
   * @return INFORMATION_SCHEMA views selects
   */
  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  /**
   * @see java.lang.Object#finalize()
   */
  @Override
  protected void finalize()
    throws Throwable
  {
    super.finalize();
    close();
  }

  /**
   * Releases the <code>Connection</code> object's database and JDBC
   * resources immediately instead of waiting for them to be
   * automatically released.
   * 
   * @throws SQLException
   *         On a database access error
   */
  void close()
  {
    try
    {
      final Connection connection = getConnection();
      if (connection != null && !connection.isClosed())
      {
        connection.close();
      }
      LOGGER.log(Level.FINE, "Database connection closed - " + connection);
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not close database connection", e);
    }
  }

  String getCatalogName()
  {
    return catalogName;
  }

  Connection getConnection()
  {
    Connection connection = null;
    if (metaData != null)
    {
      try
      {
        connection = metaData.getConnection();
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   "Could not obtain database connection from metadata",
                   e);
        connection = null;
      }
    }
    return connection;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  String getSchemaPattern()
  {
    return schemaPattern;
  }

}
