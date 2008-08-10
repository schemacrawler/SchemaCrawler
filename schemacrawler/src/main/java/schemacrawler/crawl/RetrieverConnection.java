/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  private final String catalog;
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
    catalog = catalogFromConnection;
    schemaPattern = schemaCrawlerOptions.getSchemaPattern();

    informationSchemaViews = schemaCrawlerOptions.getInformationSchemaViews();
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

  String getCatalog()
  {
    return catalog;
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

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   * 
   * @return INFORMATION_SCHEMA views selects
   */
  public InformationSchemaViews getInformationSchemaViews()
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

}
