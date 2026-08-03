/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.postgresql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.crawl.SafeHostLocationExtractor;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.datasource.JdbcUrl;

final class PostgreSQLHostLocationExtractor extends SafeHostLocationExtractor {

  private static final Logger LOGGER =
      Logger.getLogger(PostgreSQLHostLocationExtractor.class.getName());

  @Override
  protected String obtainInstanceName(final Connection connection, final JdbcUrl jdbcUrl) {
    try {
      Object instance;
      instance = DatabaseUtility.executeSqlForScalar(connection, "SELECT current_database()");
      if (instance instanceof final String instanceName) {
        return instanceName;
      }
      instance = DatabaseUtility.executeSqlForScalar(connection, "SELECT inet_server_addr()::text");
      if (instance instanceof final String instanceName) {
        return instanceName;
      }
    } catch (final SQLException e) {
      LOGGER.log(Level.FINE, "Could not get instance name", e);
    }
    return "";
  }
}
