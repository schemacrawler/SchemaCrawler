/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import schemacrawler.server.mysql.MySQLDatabaseConnector;

public class MySQLUrlMatcherTest {

  @Test
  public void supportsMySqlAndMariaDbUrls() {
    final MySQLDatabaseConnector mySQLDatabaseConnector = new MySQLDatabaseConnector();

    assertThat(mySQLDatabaseConnector.supportsUrl("jdbc:mysql://localhost:3306/books"), is(true));
    assertThat(mySQLDatabaseConnector.supportsUrl("jdbc:mariadb://localhost:3306/books"), is(true));
    assertThat(
        mySQLDatabaseConnector.supportsUrl("jdbc:postgresql://localhost:5432/books"), is(false));
    assertThat(mySQLDatabaseConnector.supportsUrl("mysql://localhost:3306/books"), is(false));

    assertThat(mySQLDatabaseConnector.supportsUrl(null), is(false));
    assertThat(mySQLDatabaseConnector.supportsUrl("\t"), is(false));
  }
}
