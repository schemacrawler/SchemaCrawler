/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.offline.jdbc;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

final class OfflineDatabaseConnectionSource implements DatabaseConnectionSource {

  private final Connection connection;

  public OfflineDatabaseConnectionSource(final OfflineConnection connection) {
    this.connection = requireNonNull(connection, "No offline connection provided");
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  @Override
  public Connection get() {
    return connection;
  }
}
