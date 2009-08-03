/*
 * SchemaCrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.execute;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.Query;

/**
 * Executes SQL.
 * 
 * @author Sualeh Fatehi
 */
public final class QueryExecutor
{

  private static final Logger LOGGER = Logger.getLogger(QueryExecutor.class
    .getName());

  private final DataSource dataSource;
  private final DataHandler handler;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param dataSource
   *        A data source.
   * @param handler
   *        Execution handler.
   * @throws QueryExecutorException
   *         On query execution error
   */
  public QueryExecutor(final DataSource dataSource, final DataHandler handler)
    throws QueryExecutorException
  {

    if (dataSource == null)
    {
      throw new QueryExecutorException("No data source provided");
    }
    this.dataSource = dataSource;

    if (handler == null)
    {
      throw new QueryExecutorException("No handler provided");
    }
    this.handler = handler;

  }

  /**
   * Executes a SQL statement, and calls the registered handler on
   * events.
   * 
   * @param queryString
   *        SQL statement.
   * @throws QueryExecutorException
   *         On query execution error
   */
  public void executeSQL(final String queryString)
    throws QueryExecutorException
  {

    final Query query = new Query("Ad hoc query", queryString);
    LOGGER.fine("Executing: " + query);

    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    try
    {
      connection = dataSource.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery(query.getQuery());

      handler.begin();
      handler.handleData(query.getQuery(), resultSet);
      handler.end();
    }
    catch (final SQLException e)
    {
      throw new QueryExecutorException(e.getMessage() + " - when executing - "
                                       + query, e);
    }
    finally
    {
      try
      {
        if (statement != null)
        {
          statement.close();
        }
        if (resultSet != null)
        {
          resultSet.close();
        }
        if (connection != null)
        {
          connection.close();
          LOGGER.log(Level.INFO, "Closed database connection, " + connection);
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   "Connection resources could not be released",
                   e);
      }
    }

  }

}
