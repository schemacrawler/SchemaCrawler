/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.offline.jdbc;

import static schemacrawler.tools.offline.jdbc.OfflineConnectionUtility.newOfflineDatabaseConnectionSource;
import static us.fatehi.utility.Utility.isBlank;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.Version;

public class OfflineDriver implements Driver {

  private static final Logger LOGGER = Logger.getLogger(OfflineDriver.class.getName());

  private static final String JDBC_URL_PREFIX = "jdbc:offline:";

  static {
    try {
      DriverManager.registerDriver(new OfflineDriver());
    } catch (final SQLException e) {
      LOGGER.log(Level.SEVERE, "Cannot register SchemaCrawler Offline Catalog Snapshot driver", e);
    }
  }

  @Override
  public boolean acceptsURL(final String url) {
    return !isBlank(url) && url.startsWith(JDBC_URL_PREFIX);
  }

  @Override
  public Connection connect(final String url, final Properties info) throws SQLException {
    if (acceptsURL(url)) {
      final String path = url.substring(JDBC_URL_PREFIX.length());
      return newOfflineDatabaseConnectionSource(Path.of(path)).get();
    } else {
      return null;
    }
  }

  @Override
  public int getMajorVersion() {
    return Integer.parseInt(Version.version().getProductVersion().split("\\.")[0]);
  }

  @Override
  public int getMinorVersion() {
    return Integer.parseInt(Version.version().getProductVersion().split("\\.")[1]);
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info)
      throws SQLException {
    return new DriverPropertyInfo[0];
  }

  @Override
  public boolean jdbcCompliant() {
    return false;
  }
}
