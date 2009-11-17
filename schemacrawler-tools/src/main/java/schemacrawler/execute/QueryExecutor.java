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

import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Executes SQL.
 * 
 * @author Sualeh Fatehi
 */
public final class QueryExecutor
{

  private static final Logger LOGGER = Logger.getLogger(QueryExecutor.class
    .getName());

  private final Connection connection;
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
  public QueryExecutor(final Connection connection, final DataHandler handler)
    throws SchemaCrawlerException
  {

    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection provided");
    }
    this.connection = connection;

    if (handler == null)
    {
      throw new SchemaCrawlerException("No handler provided");
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
    throws SchemaCrawlerException
  {

    final Query query = new Query("Ad hoc query", queryString);
    LOGGER.fine("Executing: " + query);

    Statement statement = null;
    ResultSet resultSet = null;
    try
    {
      statement = connection.createStatement();
      resultSet = statement.executeQuery(query.getQuery());

      handler.begin();
      handler.handleData(query.getQuery(), resultSet);
      handler.end();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage() + " - when executing - "
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
