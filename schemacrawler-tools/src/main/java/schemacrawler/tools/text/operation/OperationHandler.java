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

  private Database database;

  public Connection getConnection()
  {
    return connection;
  }

  public Database getDatabase()
  {
    return database;
  }

  public DataTraversalHandler getFormatter()
  {
    return handler;
  }

  public Query getQuery()
  {
    return query;
  }

  public void setConnection(final Connection connection)
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }
    this.connection = connection;
  }

  public void setDatabase(final Database database)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;
  }

  public void setFormatter(final DataTraversalHandler formatter)
  {
    if (formatter == null)
    {
      throw new IllegalArgumentException("No formatter provided");
    }
    handler = formatter;
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
    if (handler == null || query == null)
    {
      throw new SchemaCrawlerException("Cannot perform operation");
    }

    final Statement statement = createStatement();

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
        final String sql = query.getQueryForTable(table);

        LOGGER.log(Level.FINE,
                   String.format("Executing query for table %s: %s",
                                 table.getFullName(),
                                 sql));
        final ResultSet results = executeSql(statement, sql);
        handler.handleData(table, results);
        closeResults(results);
      }
    }
    else
    {
      final String sql = query.getQuery();
      final ResultSet results = executeSql(statement, sql);
      handler.handleData(query, results);
      closeResults(results);
    }

    handler.end();

    closeStatement(statement);
  }

  private void closeResults(final ResultSet results)
  {
    try
    {
      if (results != null)
      {
        results.close();
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Error releasing resources", e);
    }
  }

  private void closeStatement(final Statement statement)
  {
    try
    {
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

  private Statement createStatement()
    throws SchemaCrawlerException
  {
    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection provided");
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

    try
    {
      final Statement statement = connection.createStatement();
      return statement;
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Could not create a statement", e);
    }
  }

  private ResultSet executeSql(final Statement statement, final String sql)
    throws SchemaCrawlerException
  {
    ResultSet results = null;
    if (statement == null)
    {
      return results;
    }

    try
    {
      final boolean hasResults = statement.execute(sql);
      if (hasResults)
      {
        results = statement.getResultSet();
        return results;
      }
      else
      {
        LOGGER.log(Level.WARNING, "No results for: " + sql);
        return null;
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Error executing: " + sql, e);
      return null;
    }
  }

}
