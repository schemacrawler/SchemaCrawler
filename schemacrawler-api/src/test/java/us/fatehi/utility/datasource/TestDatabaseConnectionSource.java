/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;

final class TestDatabaseConnectionSource extends AbstractDatabaseConnectionSource {

  private final Connection connection;

  public TestDatabaseConnectionSource(final Connection connection) {
    super(conn -> {});
    this.connection = requireNonNull(connection, "No connection provided");
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  @Override
  public Connection get() {
    return PooledConnectionUtility.newPooledConnection(connection, this);
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    // No-op
    return true;
  }
}
