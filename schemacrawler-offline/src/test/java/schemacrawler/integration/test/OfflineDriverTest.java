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

package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import schemacrawler.Version;
import schemacrawler.tools.offline.jdbc.OfflineDriver;

public class OfflineDriverTest {

  @Test
  public void offlineDriver() throws SQLException {
    final OfflineDriver offlineDriver =
        (OfflineDriver) DriverManager.getDriver("jdbc:offline:test");

    assertThat(
        Version.version().getProductVersion(),
        startsWith(
            String.format(
                "%d.%d", offlineDriver.getMajorVersion(), offlineDriver.getMinorVersion())));
    assertThat(offlineDriver.jdbcCompliant(), is(false));
    assertThat(
        offlineDriver.getPropertyInfo("jdbc:offline:test", new Properties()), is(arrayWithSize(0)));
    assertThrows(SQLFeatureNotSupportedException.class, () -> offlineDriver.getParentLogger());

    assertThat(offlineDriver.acceptsURL(null), is(false));
    assertThat(offlineDriver.acceptsURL("\t\t"), is(false));
    assertThat(offlineDriver.acceptsURL("jdbc:test-db:something"), is(false));
    assertThat(offlineDriver.acceptsURL("jdbc:offline:"), is(true));
    assertThat(offlineDriver.acceptsURL("jdbc:offline:something"), is(true));
  }
}
