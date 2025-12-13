/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.model.implementation.MutableDatabaseInfo;
import schemacrawler.model.implementation.MutableJdbcDriverInfo;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import us.fatehi.utility.string.StringFormat;

public final class ConnectionInfoBuilder {

  private static final Logger LOGGER = Logger.getLogger(ConnectionInfoBuilder.class.getName());

  public static ConnectionInfoBuilder builder(final Connection connection) throws SQLException {
    return new ConnectionInfoBuilder(connection);
  }

  private static <T> T getConnectionInfoProperty(
      final Callable<T> propertyFunction, final T defaultValue) {
    if (propertyFunction == null) {
      return defaultValue;
    }
    try {
      return propertyFunction.call();
    } catch (final Exception e) {
      LOGGER.log(Level.FINE, "Could not get connection info property", e);
      return defaultValue;
    }
  }

  /**
   * Get database connection URL.
   *
   * <p>NOTE: Some databases such as Hive may throw an exception. See issue #910.
   *
   * @param dbMetaData Database metadata.
   * @return Database connection URL
   */
  private static String getConnectionUrl(final DatabaseMetaData dbMetaData) {
    if (dbMetaData == null) {
      return "";
    }
    try {
      return dbMetaData.getURL();
    } catch (final SQLException e) {
      LOGGER.log(
          Level.WARNING, new StringFormat("Could not obtain the database connection URL", e));
      return "";
    }
  }

  private static Driver getJdbcDriver(final String connectionUrl) {
    if (isBlank(connectionUrl)) {
      return null;
    }
    try {
      return DriverManager.getDriver(connectionUrl);
    } catch (final SQLException e) {
      LOGGER.log(
          Level.WARNING,
          new StringFormat(
              "Could not find a suitable JDBC driver for database connection URL <%s>",
              connectionUrl, e));
      return null;
    }
  }

  private final DatabaseMetaData dbMetaData;

  private ConnectionInfoBuilder(final Connection connection) throws SQLException {
    requireNonNull(connection, "No connection provided");
    dbMetaData = connection.getMetaData();
    requireNonNull(dbMetaData, "No database metadata available");
  }

  public DatabaseInfo buildDatabaseInfo() throws SQLException {
    return new MutableDatabaseInfo(
        getConnectionInfoProperty(() -> dbMetaData.getDatabaseProductName(), ""),
        getConnectionInfoProperty(() -> dbMetaData.getDatabaseProductVersion(), ""),
        getConnectionInfoProperty(() -> dbMetaData.getUserName(), ""));
  }

  public JdbcDriverInfo buildJdbcDriverInfo() throws SQLException {
    final String connectionUrl = getConnectionUrl(dbMetaData);
    final Driver jdbcDriver = getJdbcDriver(connectionUrl);
    final String jdbcDriverClassName;
    final boolean isJdbcCompliant;
    if (jdbcDriver != null) {
      jdbcDriverClassName = jdbcDriver.getClass().getName();
      isJdbcCompliant = jdbcDriver.jdbcCompliant();
    } else {
      jdbcDriverClassName = "";
      isJdbcCompliant = false;
    }

    return new MutableJdbcDriverInfo(
        getConnectionInfoProperty(() -> dbMetaData.getDriverName(), ""),
        jdbcDriverClassName,
        getConnectionInfoProperty(() -> dbMetaData.getDriverVersion(), ""),
        getConnectionInfoProperty(() -> dbMetaData.getDriverMajorVersion(), 0),
        getConnectionInfoProperty(() -> dbMetaData.getDriverMinorVersion(), 0),
        getConnectionInfoProperty(() -> dbMetaData.getJDBCMajorVersion(), 0),
        getConnectionInfoProperty(() -> dbMetaData.getJDBCMinorVersion(), 0),
        isJdbcCompliant,
        connectionUrl);
  }
}
