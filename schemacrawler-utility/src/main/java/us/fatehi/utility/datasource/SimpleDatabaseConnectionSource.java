/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.string.StringFormat;

final class SimpleDatabaseConnectionSource extends AbstractDatabaseConnectionSource {

  private static final Logger LOGGER =
      Logger.getLogger(SimpleDatabaseConnectionSource.class.getName());

  private final String connectionUrl;
  private final Properties jdbcConnectionProperties;
  private final Deque<Connection> connectionPool;
  private final Deque<Connection> usedConnections;

  SimpleDatabaseConnectionSource(
      final String connectionUrl,
      final Map<String, String> connectionProperties,
      final UserCredentials userCredentials,
      final Consumer<Connection> connectionInitializer) {

    super(connectionInitializer);
    this.connectionUrl = requireNotBlank(connectionUrl, "No database connection URL provided");
    requireNonNull(userCredentials, "No user credentials provided");

    final String user = userCredentials.user();
    final String password = userCredentials.password();
    if (isBlank(user)) {
      LOGGER.log(Level.WARNING, "Database user is not provided");
    }
    if (isBlank(password)) {
      LOGGER.log(Level.WARNING, "Database password is not provided");
    }

    jdbcConnectionProperties =
        createConnectionProperties(connectionUrl, connectionProperties, user, password);

    connectionPool = new LinkedBlockingDeque<>();
    usedConnections = new LinkedBlockingDeque<>();
  }

  @Override
  public void close() throws Exception {

    final List<Connection> connections = new ArrayList<>();
    connections.addAll(connectionPool);
    connections.addAll(usedConnections);

    for (final Connection connection : connections) {
      try {
        connection.close();
        LOGGER.log(Level.INFO, new StringFormat("Closed database connection <%s>", connection));
      } catch (final Exception e) {
        LOGGER.log(Level.WARNING, "Cannot close connection", e);
      }
    }

    if (!usedConnections.isEmpty()) {
      LOGGER.log(Level.SEVERE, "Abnormal termination - not all database connections are closed");
    }

    connectionPool.clear();
    usedConnections.clear();
  }

  @Override
  public synchronized Connection get() {
    // Create a connection if needed
    if (connectionPool.isEmpty()) {
      final Connection connection = getConnection(connectionUrl, jdbcConnectionProperties);
      connectionPool.add(connection);
    }

    // Mark connection as in-use
    final Connection connection = connectionPool.removeFirst();
    usedConnections.add(connection);

    connectionInitializer.accept(connection);
    LOGGER.log(
        Level.FINE,
        new StringFormat(
            "Initialized database connection <%s> with <%s>", connection, connectionInitializer));

    return PooledConnectionUtility.newPooledConnection(connection, this);
  }

  @Override
  public synchronized boolean releaseConnection(final Connection connection) {

    final boolean removed = usedConnections.remove(connection);

    try {
      final Connection unwrappedConnection = connection.unwrap(Connection.class);
      DatabaseUtility.checkConnection(unwrappedConnection);
    } catch (final SQLException e) {
      LOGGER.log(
          Level.WARNING,
          "Cannot check connection before returning to the pool - " + e.getMessage());
      LOGGER.log(Level.FINE, "Cannot check connection before returning to the pool - ", e);
    }

    connectionPool.add(connection);

    return removed;
  }
}
