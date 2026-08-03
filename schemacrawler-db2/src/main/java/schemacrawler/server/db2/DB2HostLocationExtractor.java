/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.db2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.crawl.SafeHostLocationExtractor;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.datasource.JdbcUrl;

final class DB2HostLocationExtractor extends SafeHostLocationExtractor {

  private static final Logger LOGGER = Logger.getLogger(DB2HostLocationExtractor.class.getName());

  @Override
  protected String obtainInstanceName(final Connection connection, final JdbcUrl jdbcUrl) {
    try {
      Object instance;
      instance =
          DatabaseUtility.executeSqlForScalar(
              connection,
              "SELECT ENV_INSTNAME FROM SYSIBMADM.ENV_SYS_INFO FETCH FIRST 1 ROWS ONLY");
      if (instance instanceof final String instanceName) {
        return instanceName;
      }
      instance =
          DatabaseUtility.executeSqlForScalar(
              connection, "SELECT INST_NAME FROM SYSIBMADM.ENV_INST_INFO");
      if (instance instanceof final String instanceName) {
        return instanceName;
      }
    } catch (final SQLException e) {
      LOGGER.log(Level.FINE, "Could not get instance name", e);
    }
    return "";
  }
}
