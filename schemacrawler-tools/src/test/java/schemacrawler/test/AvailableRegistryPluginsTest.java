package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.condition.JRE.JAVA_8;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.JRE;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.registry.JDBCDriverRegistry;
import schemacrawler.tools.registry.PluginRegistry;
import schemacrawler.tools.registry.ScriptEngineRegistry;
import us.fatehi.test.utility.TestDatabaseDriver;
import us.fatehi.utility.property.PropertyName;

public class AvailableRegistryPluginsTest {

  @Test
  public void availableCatalogLoaders() {
    assertThat(
        getCommands(CatalogLoaderRegistry.getCatalogLoaderRegistry()),
        contains("testloader", "schemacrawlerloader"));
  }

  @Test
  public void availableCommands() {
    assertThat(
        getCommands(CommandRegistry.getCommandRegistry()), containsInAnyOrder("test-command"));
  }

  @Test
  public void availableScriptEngines() throws UnsupportedEncodingException {
    int size = ScriptEngineRegistry.getScriptEngineRegistry().getCommandDescriptions().size();
    if (JRE.currentVersion() != JAVA_8 && size == 0) {
      // No script engines ship with Java versions later than 8
      return;
    }

    final String scriptEngineName =
        ScriptEngineRegistry.getScriptEngineRegistry().getCommandDescriptions().stream()
            .findAny()
            .get()
            .getName();

    assertThat(scriptEngineName, containsStringIgnoringCase("Nashorn"));
  }

  @Test
  public void availableJDBCDrivers() throws UnsupportedEncodingException {
    assertThat(
        getCommands(JDBCDriverRegistry.getJDBCDriverRegistry()),
        containsInAnyOrder(JDBCDriver.class.getName(), TestDatabaseDriver.class.getName()));
  }

  @Test
  public void availableServers() {
    assertThat(
        getCommands(DatabaseConnectorRegistry.getDatabaseConnectorRegistry()),
        containsInAnyOrder("test-db"));
  }

  private List<String> getCommands(final PluginRegistry registry) {
    final List<String> commands = new ArrayList<>();
    final Collection<PropertyName> commandDescriptions = registry.getCommandDescriptions();
    for (final PropertyName commandDescription : commandDescriptions) {
      commands.add(commandDescription.getName());
    }
    return commands;
  }
}
