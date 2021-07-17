package schemacrawler.test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.commandline.command.AvailableCatalogLoaders;
import schemacrawler.tools.commandline.command.AvailableCommands;
import schemacrawler.tools.commandline.command.AvailableJDBCDrivers;
import schemacrawler.tools.commandline.command.AvailableServers;

public class TestAvailablePlugins {

  @Test
  public void availableCatalogLoaders() {
    assertThat(
        new AvailableCatalogLoaders(),
        contains(
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
            "brief", "count", "details", "dump", "list", "quickdump", "schema", "test-command"));
  }

  @Test
  public void availableJDBCDrivers() throws UnsupportedEncodingException {
    final AvailableJDBCDrivers availableJDBCDrivers = new AvailableJDBCDrivers();

    final int size = availableJDBCDrivers.size();
    assertThat(size == 3 || size == 4, is(true));

    final List<Driver> availableJDBCDriversList = new ArrayList<>();
    availableJDBCDrivers.forEach(availableJDBCDriversList::add);

    assertThat(
        availableJDBCDriversList
            .stream()
            .map(driver -> driver.getClass().getTypeName())
            .collect(toList()),
        hasItem("org.hsqldb.jdbc.JDBCDriver"));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final String utf8 = StandardCharsets.UTF_8.name();
    try (final PrintStream out = new PrintStream(baos, true, utf8)) {
      availableJDBCDrivers.print(out);
    }
    final String data = baos.toString(utf8);

    assertThat(data.replace("\r", ""), containsString("Available JDBC drivers:"));
    assertThat(data.replace("\r", ""), containsString("org.hsqldb.jdbc.JDBCDriver"));
  }

  @Test
  public void availableServers() {
    assertThat(new AvailableServers(), contains("test-db"));
  }
}
