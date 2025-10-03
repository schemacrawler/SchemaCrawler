/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.database;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

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
    try (final Statement statement = createStatement(connection);
        final ResultSet resultSet = executeSql(statement, sql)) {
      return readResultsForLong(sql, resultSet);
    } catch (final SQLException e) {
      throw new SQLException("%s%n%s".formatted(e.getMessage(), sql), e);
    }
  }

  public static Object executeSqlForScalar(final Connection connection, final String sql)
      throws SQLException {
    try (final Statement statement = createStatement(connection);
        final ResultSet resultSet = executeSql(statement, sql)) {
      return readResultsForScalar(sql, resultSet);
    } catch (final SQLException e) {
      throw new SQLException("%s%n%s".formatted(e.getMessage(), sql), e);
    }
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
      throw new SQLException("Could not load database drivers: %s".formatted(e.getMessage()), e);
    }
    if (drivers.isEmpty()) {
      throw new SQLException("No database drivers are available");
    }
    return drivers;
  }

  public static long readResultsForLong(final String sql, final ResultSet resultSet)
      throws SQLException {
    final Object longValue = readResultsForScalar(sql, resultSet);
    // Error checking
    if (longValue == null || !(longValue instanceof Number)) {
      throw new SQLException("Cannot get a long value result from SQL query");
    }

    return ((Number) longValue).longValue();
  }

  public static Object readResultsForScalar(final String sql, final ResultSet resultSet)
      throws SQLException {
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
  }

  /**
   * Reads a single column result set as a list.
   *
   * @param results Result set
   * @return List
   * @throws SQLException On an exception
   */
  public static List<String> readResultsVector(final ResultSet results) throws SQLException {
    return readResultsVector(results, 1);
  }

  /**
   * Reads a column in a result set as a list. Blank values are ignored.
   *
   * @param results Result set
   * @return List of string values
   * @throws SQLException On an exception
   */
  public static List<String> readResultsVector(final ResultSet results, final int columnNumber)
      throws SQLException {
    final List<String> values = new ArrayList<>();
    if (results == null || columnNumber <= 0) {
      return values;
    }

    try {
      while (results.next()) {
        final String value = results.getString(columnNumber);
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
