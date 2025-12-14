/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.utility;

import java.sql.Connection;
import java.sql.SQLException;
import schemacrawler.crawl.ConnectionInfoBuilder;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;

/**
 * Public utility class for obtaining connection information.
 * This is a facade over the internal ConnectionInfoBuilder.
 */
public final class ConnectionInfo {

  /**
   * Gets database information from a connection.
   *
   * @param connection JDBC connection (must not be null)
   * @return DatabaseInfo object
   * @throws SQLException if there is a database access error
   * @throws NullPointerException if connection is null
   */
  public static DatabaseInfo getDatabaseInfo(final Connection connection) throws SQLException {
    if (connection == null) {
      throw new NullPointerException("Connection cannot be null");
    }
    final ConnectionInfoBuilder builder = ConnectionInfoBuilder.builder(connection);
    return builder.buildDatabaseInfo();
  }

  /**
   * Gets JDBC driver information from a connection.
   *
   * @param connection JDBC connection (must not be null)
   * @return JdbcDriverInfo object
   * @throws SQLException if there is a database access error
   * @throws NullPointerException if connection is null
   */
  public static JdbcDriverInfo getJdbcDriverInfo(final Connection connection) throws SQLException {
    if (connection == null) {
      throw new NullPointerException("Connection cannot be null");
    }
    final ConnectionInfoBuilder builder = ConnectionInfoBuilder.builder(connection);
    return builder.buildJdbcDriverInfo();
  }

  private ConnectionInfo() {
    // Prevent instantiation
  }
}
