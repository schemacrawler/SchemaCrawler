/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import schemacrawler.tools.commandline.command.AvailableCatalogLoaders;
import schemacrawler.tools.commandline.command.AvailableCommands;
import schemacrawler.tools.commandline.command.AvailableJDBCDrivers;
import schemacrawler.tools.commandline.command.AvailableScriptEngines;
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
  // No script engines ship with Java versions later than 8
  @EnabledOnJre(JRE.JAVA_8)
  public void availableScriptEngines() throws UnsupportedEncodingException {
    final AvailableScriptEngines availableScriptEngines = new AvailableScriptEngines();
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final String utf8 = StandardCharsets.UTF_8.name();
    try (final PrintStream out = new PrintStream(baos, true, utf8)) {
      availableScriptEngines.printHelp(out);
    }
    final String data = baos.toString(utf8);

    assertThat(data.replace("\r", ""), containsString("Available Script Engines:"));
    assertThat(data.replace("\r", ""), containsString("Nashorn"));
  }

  @Test
  public void availableJDBCDrivers() throws UnsupportedEncodingException {
    final AvailableJDBCDrivers availableJDBCDrivers = new AvailableJDBCDrivers();
    final int size = availableJDBCDrivers.size();
    assertThat(size, is(15));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final String utf8 = StandardCharsets.UTF_8.name();
    try (final PrintStream out = new PrintStream(baos, true, utf8)) {
      availableJDBCDrivers.printHelp(out);
    }
    final String data = baos.toString(utf8);

    assertThat(data.replace("\r", ""), containsString("Available JDBC Drivers:"));
    assertThat(data.replace("\r", ""), containsString("org.hsqldb.jdbc.JDBCDriver"));
  }

  @Test
  public void availableServers() {
    assertThat(new AvailableServers(), contains("test-db"));
  }
}
