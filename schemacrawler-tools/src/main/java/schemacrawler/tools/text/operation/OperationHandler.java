/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.tools.text.operation;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.traversal.DataTraversalHandler;

/**
 * Text formatting of operations output.
 * 
 * @author Sualeh Fatehi
 */
final class OperationHandler
{

  private static final Logger LOGGER = Logger.getLogger(OperationHandler.class
    .getName());

  private Connection connection;
  private DataTraversalHandler handler;
  private Query query;

  public Database getDatabase()
  {
    return database;
  }

  public void setDatabase(final Database database)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;
  }

  private Database database;

  public Connection getConnection()
  {
    return connection;
  }

  public DataTraversalHandler getFormatter()
  {
    return handler;
  }

  public Query getQuery()
  {
    return query;
  }

  /**
   * {@inheritDoc}
   */
  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo,
                     final DatabaseInfo databaseInfo,
                     final JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException
  {
    handler.handleInfoStart();
    handler.handle(schemaCrawlerInfo);
    handler.handle(databaseInfo);
    handler.handle(jdbcDriverInfo);
    handler.handleInfoEnd();
  }

  public void setConnection(final Connection connection)
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }
    this.connection = connection;
  }

  public void setFormatter(final DataTraversalHandler formatter)
  {
    if (formatter == null)
    {
      throw new IllegalArgumentException("No formatter provided");
    }
    this.handler = formatter;
  }

  public void setQuery(final Query query)
  {
    if (query == null)
    {
      throw new IllegalArgumentException("No query provided");
    }
    this.query = query;
  }

  public final void traverse()
    throws SchemaCrawlerException
  {
    if (connection == null || handler == null || query == null)
    {
      throw new SchemaCrawlerException("Cannot perform operation");
    }

    try
    {
      if (connection.isClosed())
      {
        throw new SchemaCrawlerException("Connection is closed");
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Connection is closed", e);
    }

    handler.begin();

    handler.handleInfoStart();
    handler.handle(database.getSchemaCrawlerInfo());
    handler.handle(database.getDatabaseInfo());
    handler.handle(database.getJdbcDriverInfo());
    handler.handleInfoEnd();

    if (query.isQueryOver())
    {
      final Collection<Table> tables = database.getTables();

      for (final Table table: tables)
      {
        final String title = table.getFullName();
        final String sql = query.getQueryForTable(table);
        executeSqlAndHandleData(title, sql);
      }
    }
    else
    {
      final String title = query.getName();
      final String sql = query.getQuery();
      executeSqlAndHandleData(title, sql);
    }

    handler.end();
  }

  private void executeSqlAndHandleData(final String title, final String sql)
    throws SchemaCrawlerException
  {
    LOGGER.log(Level.FINE,
               String.format("Executing query for %s: %s", title, sql));
    Statement statement = null;
    ResultSet results = null;
    try
    {
      statement = connection.createStatement();
      final boolean hasResults = statement.execute(sql);
      // Pass into data handler for output
      if (hasResults)
      {
        results = statement.getResultSet();
        handler.handleData(title, results);
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Error executing: " + sql, e);
    }
    finally
    {
      try
      {
        if (results != null)
        {
          results.close();
        }
        if (statement != null)
        {
          statement.close();
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Error releasing resources", e);
      }
    }
  }

}
