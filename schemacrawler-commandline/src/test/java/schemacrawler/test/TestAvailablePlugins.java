package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.commandline.command.AvailableCatalogLoaders;
import schemacrawler.tools.commandline.command.AvailableCommands;
import schemacrawler.tools.commandline.command.AvailableServers;

public class TestAvailablePlugins {

  @Test
  public void availableCatalogLoaders() {
    assertThat(
        new AvailableCatalogLoaders(),
        contains("testloader", "countsloader", "schemacrawlerloader"));
  }

  @Test
  public void availableCommands() {
    assertThat(
        new AvailableCommands(),
        contains(
            "brief", "count", "details", "dump", "list", "quickdump", "schema", "test-command"));
  }

  @Test
  public void availableServers() {
    assertThat(new AvailableServers(), contains("test-db"));
  }
}
