/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package sf.util;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;
import static sf.util.Utility.readResourceFully;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
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
    throws SQLException
  {
    requireNonNull(connection, "No connection provided");
    if (connection.isClosed())
    {
      throw new SQLException("Connection is closed");
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
          final ResultSet resultSet = executeSql(statement, sql);
          if (resultSet != null)
          {
            LOGGER.log(Level.WARNING, "Ignoring results from query: " + sql);
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
                                       throws SchemaCrawlerException
  {
    ResultSet results = null;
    if (statement == null)
    {
      return results;
    }
    if (isBlank(sql))
    {
      LOGGER.log(Level.FINE, "No SQL provided", new RuntimeException());
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
                   String.format("No results. Update count of %d for query: %s",
                                 updateCount, sql));
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
      LOGGER.log(Level.WARNING, "Error executing: " + sql, e);
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
    try (
      final Statement statement = createStatement(connection);
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
        LOGGER.log(Level.WARNING, "No rows of data returned: " + sql);
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

  private DatabaseUtility()
  { // Prevent instantiation
  }

}
