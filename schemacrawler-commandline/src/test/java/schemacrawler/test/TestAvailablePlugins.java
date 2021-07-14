package schemacrawler.test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

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
  public void availableJDBCDrivers() {
    final AvailableJDBCDrivers availableJDBCDrivers = new AvailableJDBCDrivers();
    final List<Driver> availableJDBCDriversList = new ArrayList<>();
    availableJDBCDrivers.forEach(availableJDBCDriversList::add);

    final int size = availableJDBCDriversList.size();

    assertThat(size == 3 || size == 4, is(true));
    assertThat(
        availableJDBCDriversList
            .stream()
            .map(driver -> driver.getClass().getTypeName())
            .collect(toList()),
        hasItem("org.hsqldb.jdbc.JDBCDriver"));
  }

  @Test
  public void availableServers() {
    assertThat(new AvailableServers(), contains("test-db"));
  }
}
