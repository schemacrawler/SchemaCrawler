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

package schemacrawler.execute;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.crawl.Query;

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
      handler.handleMetadata(dataSource.toString());
      handler.handleTitle(query.getQuery());
      handler.handleData(resultSet);
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
          LOGGER.log(Level.FINE, "Database connection closed - " + connection);
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

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return "QueryExecutor{" + "dataSource=" + dataSource + ", handler="
           + handler + "}";
  }

}
