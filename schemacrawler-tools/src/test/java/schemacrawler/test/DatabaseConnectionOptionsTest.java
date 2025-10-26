/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseUrlConnectionOptions;

public class DatabaseConnectionOptionsTest {

  @Test
  public void url() {
    final DatabaseUrlConnectionOptions connectionOptions =
        new DatabaseUrlConnectionOptions("jdbc:test-db:test");

    assertThat(connectionOptions.connectionUrl(), is("jdbc:test-db:test"));
  }

  @Test
  public void serverHost() {

    final Map<String, String> map = new HashMap<>();
    map.put("key", "value");

    final DatabaseServerHostConnectionOptions connectionOptions =
        new DatabaseServerHostConnectionOptions("test-db", "host", 2222, "database", map);

    assertThat(connectionOptions.host(), is("host"));
    assertThat(connectionOptions.port(), is(2222));
    assertThat(connectionOptions.database(), is("database"));
    assertThat(connectionOptions.urlx(), is(map));
  }
}
