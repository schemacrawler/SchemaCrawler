package schemacrawler.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.test.utility.TestCatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.catalogloader.ChainedCatalogLoader;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;

public class CatalogLoaderRegistryTest {

  @Test
  public void commandLineCommands() throws Exception {
    final Collection<PluginCommand> commandLineCommands1 =
        new CatalogLoaderRegistry().getCommandLineCommands();
    assertThat(commandLineCommands1, hasSize(2));

    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestCatalogLoader.class.getName() + ".force-instantiation-failure", "throw");
          assertThrows(
              SchemaCrawlerRuntimeException.class,
              () -> new CatalogLoaderRegistry().getCommandLineCommands());
        });
  }

  @Test
  public void helpCommands() throws Exception {
    final Collection<PluginCommand> helpCommands = new CatalogLoaderRegistry().getHelpCommands();
    assertThat(helpCommands, hasSize(2));

    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestCatalogLoader.class.getName() + ".force-instantiation-failure", "throw");
          assertThrows(
              SchemaCrawlerRuntimeException.class,
              () -> new CatalogLoaderRegistry().getHelpCommands());
        });
  }

  @Test
  public void loadCatalogLoaders() throws SchemaCrawlerException {
    final ChainedCatalogLoader chainedCatalogLoaders =
        new CatalogLoaderRegistry().loadCatalogLoaders();

    final List<CatalogLoader> catalogLoaders = new ArrayList<>();
    chainedCatalogLoaders.forEach(catalogLoaders::add);

    assertThat(catalogLoaders, hasSize(2));
  }

  @Test
  public void supportedCatalogLoaders() throws Exception {
    final Collection<CommandDescription> supportedCatalogLoaders =
        new CatalogLoaderRegistry().getSupportedCatalogLoaders();
    assertThat(supportedCatalogLoaders, hasSize(2));

    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestCatalogLoader.class.getName() + ".force-instantiation-failure", "throw");
          assertThrows(
              SchemaCrawlerRuntimeException.class,
              () -> new CatalogLoaderRegistry().getSupportedCatalogLoaders());
        });
  }
}
