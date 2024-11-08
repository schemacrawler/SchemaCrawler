/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.offline.jdbc;

import static schemacrawler.tools.offline.jdbc.OfflineConnectionUtility.newOfflineDatabaseConnectionSource;
import static us.fatehi.utility.Utility.isBlank;
import java.nio.file.Paths;
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
      return newOfflineDatabaseConnectionSource(Paths.get(path)).get();
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
