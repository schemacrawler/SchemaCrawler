/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static us.fatehi.test.utility.DataSourceTestUtility.JDBC_DRIVER_COUNT;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.command.AvailableCatalogLoaders;
import schemacrawler.tools.commandline.command.AvailableCommands;
import schemacrawler.tools.commandline.command.AvailableJDBCDrivers;
import schemacrawler.tools.commandline.command.AvailableServers;

public class AvailablePluginsTest {

  @Test
  public void availableCatalogLoaders() {
    assertThat(
        new AvailableCatalogLoaders(),
        containsInAnyOrder(
            "weakassociationsloader",
            "testloader",
            "attributesloader",
            "countsloader",
            "entitymodelsloader",
            "schemacrawlerloader"));
  }

  @Test
  public void availableCommands() {
    assertThat(
        new AvailableCommands(),
        contains(
            "brief", "count", "details", "dump", "list", "schema", "tablesample", "test-command"));
  }

  @Test
  public void availableJDBCDrivers() throws UnsupportedEncodingException {
    final AvailableJDBCDrivers availableJDBCDrivers = new AvailableJDBCDrivers();
    final int size = availableJDBCDrivers.size();
    assertThat(size, is(JDBC_DRIVER_COUNT));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final String utf8 = StandardCharsets.UTF_8.name();
    try (final PrintStream out = new PrintStream(baos, true, utf8)) {
      availableJDBCDrivers.printHelp(out);
    }
    final String data = baos.toString(utf8);

    assertThat(data.replace("\r", ""), containsString("Available JDBC Drivers:"));
    assertThat(data.replace("\r", ""), containsString("org.hsqldb.jdbc.JDBCDriver"));
    assertThat(data.replace("\r", ""), containsString("us.fatehi.test.utility.TestDatabaseDriver"));
  }

  @Test
  public void availableServers() {
    assertThat(new AvailableServers(), contains("test-db"));
  }
}
