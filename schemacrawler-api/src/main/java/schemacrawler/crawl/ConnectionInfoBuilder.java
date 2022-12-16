/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ConnectionInfo;
import us.fatehi.utility.string.StringFormat;

public final class ConnectionInfoBuilder {

  private static final Logger LOGGER = Logger.getLogger(ConnectionInfoBuilder.class.getName());

  public static ConnectionInfoBuilder builder(final Connection connection) {
    return new ConnectionInfoBuilder(connection);
  }

  private static <T> T getConnectionInfoProperty(
      final Callable<T> propertyFunction, final T defaultValue) {
    if (propertyFunction == null) {
      return defaultValue;
    }
    try {
      final T value = propertyFunction.call();
      return value;
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
      final String connectionUrl = dbMetaData.getURL();
      return connectionUrl;
    } catch (final SQLException e) {
      LOGGER.log(
          Level.WARNING, new StringFormat("Could not obtain the database connection URL", e));
      return "";
    }
  }

  private static String getJdbcDriverClassName(final String connectionUrl) {
    if (isBlank(connectionUrl)) {
      return "";
    }
    try {
      final Driver jdbcDriver = DriverManager.getDriver(connectionUrl);
      return jdbcDriver.getClass().getName();
    } catch (final SQLException e) {
      LOGGER.log(
          Level.WARNING,
          new StringFormat(
              "Could not find a suitable JDBC driver for database connection URL <%s>",
              connectionUrl, e));
      return "";
    }
  }

  private final Connection connection;

  private ConnectionInfoBuilder(final Connection connection) {
    this.connection = requireNonNull(connection, "No connection provided");
  }

  public ConnectionInfo build() throws SQLException {

    final DatabaseMetaData dbMetaData = connection.getMetaData();
    final String connectionUrl = getConnectionUrl(dbMetaData);
    final String jdbcDriverClassName = getJdbcDriverClassName(connectionUrl);

    final ConnectionInfo connectionInfo =
        new ImmutableConnectionInfo(
            getConnectionInfoProperty(() -> dbMetaData.getDatabaseProductName(), ""),
            getConnectionInfoProperty(() -> dbMetaData.getDatabaseProductVersion(), ""),
            connectionUrl,
            getConnectionInfoProperty(() -> dbMetaData.getUserName(), ""),
            jdbcDriverClassName,
            getConnectionInfoProperty(() -> dbMetaData.getDriverName(), ""),
            getConnectionInfoProperty(() -> dbMetaData.getDriverVersion(), ""),
            getConnectionInfoProperty(() -> dbMetaData.getDriverMajorVersion(), 0),
            getConnectionInfoProperty(() -> dbMetaData.getDriverMinorVersion(), 0),
            getConnectionInfoProperty(() -> dbMetaData.getJDBCMajorVersion(), 0),
            getConnectionInfoProperty(() -> dbMetaData.getJDBCMinorVersion(), 0));

    return connectionInfo;
  }
}
