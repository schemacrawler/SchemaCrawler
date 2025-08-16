/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.utility.datasource;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.function.Consumer;

public final class ConnectionDatabaseConnectionSource implements DatabaseConnectionSource {

  private final Connection connection;

  public ConnectionDatabaseConnectionSource(final Connection connection) {
    this.connection = requireNonNull(connection, "No connection provided");
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  @Override
  public Connection get() {
    // Do not close this connection
    return PooledConnectionUtility.newPooledConnection(connection, this);
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    // No-op
    return true;
  }

  @Override
  public void setFirstConnectionInitializer(final Consumer<Connection> connectionInitializer) {
    // No-op
  }
}
