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

package us.fatehi.utility.datasource;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.fatehi.utility.database.DatabaseUtility;

final class SimpleDatabaseConnectionSource extends AbstractDatabaseConnectionSource {

  private static final Logger LOGGER =
      Logger.getLogger(SimpleDatabaseConnectionSource.class.getName());

  private final String connectionUrl;
  private final Properties jdbcConnectionProperties;
  private final LinkedBlockingDeque<Connection> connectionPool;
  private final LinkedBlockingDeque<Connection> usedConnections;

  SimpleDatabaseConnectionSource(
      final String connectionUrl,
      final Map<String, String> connectionProperties,
      final UserCredentials userCredentials,
      final Consumer<Connection> connectionInitializer) {

    this.connectionUrl = requireNotBlank(connectionUrl, "No database connection URL provided");
    requireNonNull(userCredentials, "No user credentials provided");

    final String user = userCredentials.getUser();
    final String password = userCredentials.getPassword();
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
  public Connection get() {
    // Create a connection if needed
    if (connectionPool.isEmpty()) {
      final Connection connection =
          getConnection(connectionUrl, jdbcConnectionProperties, connectionInitializer);
      connectionPool.add(connection);
    }

    // Mark connection as in-use
    final Connection connection = connectionPool.removeFirst();
    usedConnections.add(connection);

    return PooledConnectionUtility.newPooledConnection(connection, this);
  }

  @Override
  public boolean releaseConnection(final Connection connection) {

    final boolean removed = usedConnections.remove(connection);

    try {
      final Connection unwrappedConnection = connection.unwrap(Connection.class);
      DatabaseUtility.checkConnection(unwrappedConnection);
    } catch (final SQLException e) {
      throw new RuntimeException("Cannot release connection from pool", e);
    }

    connectionPool.add(connection);

    return removed;
  }

  @Override
  protected void finalize() throws Throwable {
    // Assert that all connections are closed
    if (!connectionPool.isEmpty() || !usedConnections.isEmpty()) {
      throw new RuntimeException("Connection pool is not closed");
    }
    super.finalize();
  }
}
