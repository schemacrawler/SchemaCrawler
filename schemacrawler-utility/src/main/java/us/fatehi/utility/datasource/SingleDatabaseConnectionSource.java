/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.SQLRuntimeException;
import us.fatehi.utility.string.StringFormat;

final class SingleDatabaseConnectionSource extends AbstractDatabaseConnectionSource {

  private static final Logger LOGGER =
      Logger.getLogger(SingleDatabaseConnectionSource.class.getName());

  private final Connection connection;

  SingleDatabaseConnectionSource(
      final String connectionUrl,
      final Map<String, String> connectionProperties,
      final UserCredentials userCredentials,
      final Consumer<Connection> connectionInitializer) {
    super(connectionInitializer);
    requireNotBlank(connectionUrl, "No database connection URL provided");
    requireNonNull(userCredentials, "No user credentials provided");

    final String user = userCredentials.user();
    final String password = userCredentials.password();
    final Properties jdbcConnectionProperties =
        createConnectionProperties(connectionUrl, connectionProperties, user, password);
    connection = getConnection(connectionUrl, jdbcConnectionProperties);
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  @Override
  public Connection get() {
    connectionInitializer.accept(connection);
    LOGGER.log(
        Level.FINE,
        new StringFormat(
            "Initialized database connection <%s> with <%s>", connection, connectionInitializer));

    return PooledConnectionUtility.newPooledConnection(connection, this);
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    // No-op
    return true;
  }

  @Override
  protected void finalize() throws Throwable {
    // Assert that all connections are closed
    if (!connection.isClosed()) {
      throw new SQLRuntimeException("Connection pool is not closed");
    }
    super.finalize();
  }
}
