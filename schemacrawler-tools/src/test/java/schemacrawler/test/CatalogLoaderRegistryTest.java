package schemacrawler.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.PluginRegistryTestUtility.reload;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.TestCatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.catalogloader.ChainedCatalogLoader;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.property.PropertyName;

public class CatalogLoaderRegistryTest {

  @Test
  public void commandLineCommands() throws Exception {
    final Collection<PluginCommand> commandLineCommands =
        CatalogLoaderRegistry.getCatalogLoaderRegistry().getCommandLineCommands();
    assertThat(commandLineCommands, hasSize(2));
    final List<String> names =
        commandLineCommands.stream().map(PluginCommand::getName).collect(toList());
    assertThat(names, containsInAnyOrder("loader:testloader", null));
  }

  @Test
  public void helpCommands() throws Exception {
    final Collection<PluginCommand> helpCommands =
        CatalogLoaderRegistry.getCatalogLoaderRegistry().getHelpCommands();
    assertThat(String.valueOf(helpCommands), helpCommands, hasSize(2));
    final List<String> names = helpCommands.stream().map(PluginCommand::getName).collect(toList());
    assertThat(names, containsInAnyOrder("loader:testloader", null));
  }

  @Test
  public void chainedCatalogLoaders() {
    final ChainedCatalogLoader chainedCatalogLoaders =
        CatalogLoaderRegistry.getCatalogLoaderRegistry().newChainedCatalogLoader();

    final List<CatalogLoader> catalogLoaders = new ArrayList<>();
    chainedCatalogLoaders.forEach(catalogLoaders::add);

    assertThat(catalogLoaders, hasSize(2));
  }

  @Test
  public void registeredPlugins() throws Exception {
    final Collection<PropertyName> supportedCatalogLoaders =
        CatalogLoaderRegistry.getCatalogLoaderRegistry().getRegisteredPlugins();
    assertThat(supportedCatalogLoaders, hasSize(2));
    final List<String> names =
        supportedCatalogLoaders.stream().map(PropertyName::getName).collect(toList());
    assertThat(names, containsInAnyOrder("testloader", "schemacrawlerloader"));
  }

  @Test
  public void name() throws Exception {
    final CatalogLoaderRegistry catalogLoaderRegistry =
        CatalogLoaderRegistry.getCatalogLoaderRegistry();
    assertThat(catalogLoaderRegistry.getName(), is("SchemaCrawler Catalog Loaders"));
  }

  @Test
  public void loadError() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestCatalogLoader.class.getName() + ".force-instantiation-failure", "throw");

          assertThrows(InternalRuntimeException.class, () -> reload(CatalogLoaderRegistry.class));
        });
    // Reset
    reload(CatalogLoaderRegistry.class);
  }
}
