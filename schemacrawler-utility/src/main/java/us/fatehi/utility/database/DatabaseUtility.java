/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import us.fatehi.utility.UtilityLogger;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public final class DatabaseUtility {

  private static final Logger LOGGER = Logger.getLogger(DatabaseUtility.class.getName());

  public static Connection checkConnection(final Connection connection) throws SQLException {
    try {
      requireNonNull(connection, "No database connection provided");
      if (connection.isClosed()) {
        throw new SQLException("Connection is closed");
      }
    } catch (final NullPointerException e) {
      throw new SQLException(e);
    }

    return connection;
  }

  public static ResultSet checkResultSet(final ResultSet resultSet) throws SQLException {
    try {
      requireNonNull(resultSet, "No result-set provided");
      if (resultSet.isClosed()) {
        throw new SQLException("Result-set is closed");
      }
    } catch (final NullPointerException e) {
      throw new SQLException(e);
    }

    return resultSet;
  }

  public static Statement createStatement(final Connection connection) throws SQLException {
    checkConnection(connection);
    return connection.createStatement();
  }

  /**
   * Load registered database drivers, and throw exception if any driver cannot be loaded. Cycling
   * through the service loader and loading driver classes allows for dependencies to be vetted out.
   *
   * <p>Do not use DriverManager.getDrivers(), since that swallows exceptions.
   *
   * @throws SQLException
   */
  public static Collection<Driver> getAvailableJdbcDrivers() throws SQLException {
    final Collection<Driver> drivers = new ArrayList<>();
    try {
      final ServiceLoader<Driver> serviceLoader = ServiceLoader.load(Driver.class);
      for (final Driver driver : serviceLoader) {
        drivers.add(driver);
      }
    } catch (final Throwable e) {
      throw new SQLException(
          String.format("Could not load database drivers: %s", e.getMessage()), e);
    }
    if (drivers.isEmpty()) {
      throw new SQLException("No database drivers are available");
    }
    return drivers;
  }

  public static ResultSet executeSql(final Statement statement, final String sql)
      throws SQLException {
    if (statement == null) {
      return null;
    }
    if (isBlank(sql)) {
      LOGGER.log(Level.FINE, "No SQL provided", new RuntimeException("No SQL provided"));
      return null;
    }

    try {
      statement.clearWarnings();

      final boolean hasResults = statement.execute(sql);
      new UtilityLogger(LOGGER).logSQLWarnings(statement);
      if (hasResults) {
        return statement.getResultSet();
      }
      final int updateCount = statement.getUpdateCount();
      LOGGER.log(
          Level.FINE,
          new StringFormat("No results. Update count of %d for query: %s", updateCount, sql));
      return null;

    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, e, new StringFormat("Error executing SQL <%s>", sql));
      throw e;
    }
  }

  public static long executeSqlForLong(final Connection connection, final String sql)
      throws SQLException {
    final Object longValue = executeSqlForScalar(connection, sql);
    // Error checking
    if (longValue == null || !(longValue instanceof Number)) {
      throw new SQLException("Cannot get a long value result from SQL query");
    }

    return ((Number) longValue).longValue();
  }

  public static Object executeSqlForScalar(final Connection connection, final String sql)
      throws SQLException {
    try (final Statement statement = createStatement(connection);
        final ResultSet resultSet = executeSql(statement, sql)) {
      if (resultSet == null) {
        return null;
      }

      // Error checking
      if (resultSet.getMetaData().getColumnCount() != 1) {
        throw new SQLException("Too many columns of data returned");
      }

      Object scalar;
      if (resultSet.next()) {
        scalar = resultSet.getObject(1);
        if (resultSet.wasNull()) {
          scalar = null;
        }
      } else {
        LOGGER.log(Level.WARNING, new StringFormat("No rows of data returned for query <%s>", sql));
        scalar = null;
      }

      // Error checking
      if (resultSet.next()) {
        throw new SQLException("Too many rows of data returned");
      }

      return scalar;
    } catch (final SQLException e) {
      throw new SQLException(String.format("%s%n%s", e.getMessage(), sql), e);
    }
  }

  /**
   * Reads a single column result set as a list.
   *
   * @param results Result set
   * @return List
   * @throws SQLException On an exception
   */
  public static List<String> readResultsVector(final ResultSet results) throws SQLException {
    final List<String> values = new ArrayList<>();
    if (results == null) {
      return values;
    }

    try {
      while (results.next()) {
        final String value = results.getString(1);
        if (!results.wasNull() && !isBlank(value)) {
          values.add(value.trim());
        }
      }
    } finally {
      results.close();
    }
    return values;
  }

  private DatabaseUtility() {
    // Prevent instantiation
  }
}
