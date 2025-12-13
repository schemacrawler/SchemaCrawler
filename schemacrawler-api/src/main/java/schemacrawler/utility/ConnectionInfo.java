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
   * @param connection JDBC connection
   * @return DatabaseInfo object
   * @throws SQLException if there is a database access error
   */
  public static DatabaseInfo getDatabaseInfo(final Connection connection) throws SQLException {
    final ConnectionInfoBuilder builder = ConnectionInfoBuilder.builder(connection);
    return builder.buildDatabaseInfo();
  }

  /**
   * Gets JDBC driver information from a connection.
   *
   * @param connection JDBC connection
   * @return JdbcDriverInfo object
   * @throws SQLException if there is a database access error
   */
  public static JdbcDriverInfo getJdbcDriverInfo(final Connection connection) throws SQLException {
    final ConnectionInfoBuilder builder = ConnectionInfoBuilder.builder(connection);
    return builder.buildJdbcDriverInfo();
  }

  private ConnectionInfo() {
    // Prevent instantiation
  }
}
