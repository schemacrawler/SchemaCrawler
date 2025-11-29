/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import us.fatehi.utility.SQLRuntimeException;

final class DataSourceConnectionSource extends AbstractDatabaseConnectionSource {

  private static final Logger LOGGER = Logger.getLogger(DataSourceConnectionSource.class.getName());

  private final DataSource dataSource;

  DataSourceConnectionSource(final DataSource dataSource) {
    super(connection -> {});
    this.dataSource = requireNonNull(dataSource, "Data source not provided");
  }

  @Override
  public void close() throws Exception {
    if (dataSource instanceof Closeable closeable) {
      closeable.close();
    }
  }

  @Override
  public Connection get() {
    try {
      final Connection connection = dataSource.getConnection();
      connectionInitializer.accept(connection);
      return connection;
    } catch (final SQLException e) {
      throw new SQLRuntimeException(e);
    }
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    try {
      connection.close();
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not close database connection", e);
      return false;
    }
    return true;
  }
}
