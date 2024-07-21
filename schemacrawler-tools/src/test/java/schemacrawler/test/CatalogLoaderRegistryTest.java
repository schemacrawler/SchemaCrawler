package schemacrawler.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.TestCatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.catalogloader.ChainedCatalogLoader;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;

public class CatalogLoaderRegistryTest {

  @Test
  public void commandLineCommands() throws Exception {
    final Collection<PluginCommand> commandLineCommands =
        CatalogLoaderRegistry.getCatalogLoaderRegistry().getCommandLineCommands();
    assertThat(commandLineCommands, hasSize(2));
    final List<String> names =
        commandLineCommands.stream().map(PluginCommand::getName).collect(toList());
    assertThat(names, containsInAnyOrder("loader:testloader", null));

    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestCatalogLoader.class.getName() + ".force-instantiation-failure", "throw");
          assertThrows(
              InternalRuntimeException.class,
              () -> CatalogLoaderRegistry.getCatalogLoaderRegistry().getCommandLineCommands());
        });
  }

  @Test
  public void helpCommands() throws Exception {
    final Collection<PluginCommand> helpCommands =
        CatalogLoaderRegistry.getCatalogLoaderRegistry().getHelpCommands();
    assertThat(String.valueOf(helpCommands), helpCommands, hasSize(2));
    final List<String> names = helpCommands.stream().map(PluginCommand::getName).collect(toList());
    assertThat(names, containsInAnyOrder("loader:testloader", null));

    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestCatalogLoader.class.getName() + ".force-instantiation-failure", "throw");
          assertThrows(
              InternalRuntimeException.class,
              () -> CatalogLoaderRegistry.getCatalogLoaderRegistry().getHelpCommands());
        });
  }

  @Test
  public void loadCatalogLoaders() {
    final ChainedCatalogLoader chainedCatalogLoaders =
        CatalogLoaderRegistry.getCatalogLoaderRegistry().newChainedCatalogLoader();

    final List<CatalogLoader> catalogLoaders = new ArrayList<>();
    chainedCatalogLoaders.forEach(catalogLoaders::add);

    assertThat(catalogLoaders, hasSize(2));
  }

  @Test
  public void supportedCatalogLoaders() throws Exception {
    final Collection<CommandDescription> supportedCatalogLoaders =
        CatalogLoaderRegistry.getCatalogLoaderRegistry().getCommandDescriptions();
    assertThat(supportedCatalogLoaders, hasSize(2));
    final List<String> names =
        supportedCatalogLoaders.stream().map(CommandDescription::getName).collect(toList());
    assertThat(names, containsInAnyOrder("testloader", "schemacrawlerloader"));

    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestCatalogLoader.class.getName() + ".force-instantiation-failure", "throw");
          assertThrows(
              InternalRuntimeException.class,
              () -> CatalogLoaderRegistry.getCatalogLoaderRegistry().getCommandDescriptions());
        });
  }
}
