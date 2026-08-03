/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.crawl.SafeHostLocationExtractor;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.datasource.JdbcUrl;

final class OracleHostLocationExtractor extends SafeHostLocationExtractor {

  private static final Logger LOGGER =
      Logger.getLogger(OracleHostLocationExtractor.class.getName());

  @Override
  protected String obtainInstanceName(final Connection connection, final JdbcUrl jdbcUrl) {
    try {
      Object instance;
      instance =
          DatabaseUtility.executeSqlForScalar(
              connection, "SELECT SYS_CONTEXT('USERENV', 'DB_UNIQUE_NAME') FROM DUAL");
      if (instance instanceof final String instanceName) {
        return instanceName;
      }
      instance =
          DatabaseUtility.executeSqlForScalar(connection, "SELECT INSTANCE_NAME FROM V$INSTANCE");
      if (instance instanceof final String instanceName) {
        return instanceName;
      }
    } catch (final SQLException e) {
      LOGGER.log(Level.FINE, "Could not get instance name", e);
    }
    return "";
  }
}
