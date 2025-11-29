/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
            "%d.%d".formatted(offlineDriver.getMajorVersion(), offlineDriver.getMinorVersion())));
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
