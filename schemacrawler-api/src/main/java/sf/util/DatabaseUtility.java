/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package sf.util;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;
import static sf.util.Utility.readResourceFully;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Utility methods.
 *
 * @author Sualeh Fatehi
 */
public final class DatabaseUtility
{

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseUtility.class.getName());

  public static void checkConnection(final Connection connection)
    throws SchemaCrawlerException
  {
    try
    {
      requireNonNull(connection, "No connection provided");
      if (connection.isClosed())
      {
        throw new SQLException("Connection is closed");
      }
    }
    catch (final NullPointerException | SQLException e)
    {
      throw new SchemaCrawlerException("Bad database connection", e);
    }
  }

  public static Statement createStatement(final Connection connection)
    throws SchemaCrawlerException, SQLException
  {
    checkConnection(connection);
    return connection.createStatement();
  }

  public static void executeScriptFromResource(final Connection connection,
                                               final String scriptResource)
    throws SchemaCrawlerException
  {
    try (final Statement statement = createStatement(connection);)
    {
      final String sqlScript = readResourceFully(scriptResource);
      if (!isBlank(sqlScript))
      {
        for (final String sql: sqlScript.split(";"))
        {
          if (isBlank(sql))
          {
            continue;
          }

          final ResultSet resultSet = executeSql(statement, sql);
          if (resultSet != null)
          {
            LOGGER
              .log(Level.WARNING,
                   new StringFormat("Ignoring results from query, %s", sql));
            resultSet.close();
          }
        }
      }
    }
    catch (final SQLException e)
    {
      System.err.println(e.getMessage());
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
  }

  public static ResultSet executeSql(final Statement statement,
                                     final String sql)
    throws SQLException
  {
    return executeSql(statement, sql, false);
  }

  public static ResultSet executeSql(final Statement statement,
                                     final String sql,
                                     boolean throwSQLException)
    throws SQLException
  {
    ResultSet results = null;
    if (statement == null)
    {
      return results;
    }
    if (isBlank(sql))
    {
      LOGGER.log(Level.FINE,
                 "No SQL provided",
                 new RuntimeException("No SQL provided"));
      return results;
    }

    try
    {
      statement.clearWarnings();

      final boolean hasResults = statement.execute(sql);
      if (hasResults)
      {
        results = statement.getResultSet();
      }
      else
      {
        final int updateCount = statement.getUpdateCount();
        LOGGER.log(Level.FINE,
                   new StringFormat("No results. Update count of %d for query: %s",
                                    updateCount,
                                    sql));
      }

      SQLWarning sqlWarning = statement.getWarnings();
      while (sqlWarning != null)
      {
        LOGGER.log(Level.INFO, sqlWarning.getMessage(), sqlWarning);
        sqlWarning = sqlWarning.getNextWarning();
      }

      return results;
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING,
                 e,
                 new StringFormat("Error executing SQL, %s", sql));
      if (throwSQLException)
      {
        throw e;
      }
      return null;
    }
  }

  public static long executeSqlForLong(final Connection connection,
                                       final String sql)
    throws SchemaCrawlerException
  {
    final Object longValue = executeSqlForScalar(connection, sql);
    // Error checking
    if (longValue == null || !(longValue instanceof Number))
    {
      throw new SchemaCrawlerException("Cannot get an integer value result from SQL");
    }

    return ((Number) longValue).longValue();
  }

  public static Object executeSqlForScalar(final Connection connection,
                                           final String sql)
    throws SchemaCrawlerException
  {
    try (final Statement statement = createStatement(connection);
        final ResultSet resultSet = executeSql(statement, sql);)
    {
      if (resultSet == null)
      {
        return null;
      }

      // Error checking
      if (resultSet.getMetaData().getColumnCount() != 1)
      {
        throw new SchemaCrawlerException("Too many columns of data returned");
      }

      final Object scalar;
      if (resultSet.next())
      {
        scalar = resultSet.getObject(1);
      }
      else
      {
        LOGGER
          .log(Level.WARNING,
               new StringFormat("No rows of data returned for query, %s", sql));
        scalar = null;
      }

      // Error checking
      if (resultSet.next())
      {
        throw new SchemaCrawlerException("Too many rows of data returned");
      }

      return scalar;
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(sql, e);
    }
  }

  /**
   * Reads a single column result set as a list.
   *
   * @param results
   *        Result set
   * @return List
   * @throws SQLException
   *         On an exception
   */
  public static List<String> readResultsVector(final ResultSet results)
    throws SQLException
  {
    final List<String> values = new ArrayList<>();
    if (results == null)
    {
      return values;
    }

    try
    {
      while (results.next())
      {
        final String value = results.getString(1);
        if (!results.wasNull() && !isBlank(value))
        {
          values.add(value.trim());
        }
      }
    }
    finally
    {
      results.close();
    }
    return values;
  }

  private DatabaseUtility()
  { // Prevent instantiation
  }

}
