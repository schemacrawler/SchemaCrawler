/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ConnectionInfo;
import us.fatehi.utility.string.StringFormat;

public final class ConnectionInfoBuilder {

  private static final Logger LOGGER = Logger.getLogger(ConnectionInfoBuilder.class.getName());

  public static ConnectionInfoBuilder builder(final Connection connection) {
    return new ConnectionInfoBuilder(connection);
  }

  private static String getJdbcDriverClassName(final String connectionUrl) throws SQLException {
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
    final String connectionUrl = dbMetaData.getURL();
    final String jdbcDriverClassName = getJdbcDriverClassName(connectionUrl);

    final ConnectionInfo connectionInfo =
        new ImmutableConnectionInfo(
            dbMetaData.getDatabaseProductName(),
            dbMetaData.getDatabaseProductVersion(),
            connectionUrl,
            dbMetaData.getUserName(),
            jdbcDriverClassName,
            dbMetaData.getDriverName(),
            dbMetaData.getDriverVersion(),
            dbMetaData.getDriverMajorVersion(),
            dbMetaData.getDriverMinorVersion(),
            dbMetaData.getJDBCMajorVersion(),
            dbMetaData.getJDBCMinorVersion());

    return connectionInfo;
  }
}
