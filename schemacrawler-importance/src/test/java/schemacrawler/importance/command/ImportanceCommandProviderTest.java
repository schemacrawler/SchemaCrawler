/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.tools.command.SchemaCrawlerCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
class ImportanceCommandProviderTest {

  @Test
  void importanceCommandIsDiscoveredViaServiceLoader() {
    boolean found = false;
    for (final SchemaCrawlerCommandProvider provider :
        ServiceLoader.load(SchemaCrawlerCommandProvider.class)) {
      if (provider instanceof ImportanceCommandProvider) {
        found = true;
      }
    }
    assertThat(found, is(true));
  }

  @Test
  void supportsOnlyKnownOutputFormats() {
    final ImportanceCommandProvider provider = new ImportanceCommandProvider();
    final OutputOptions json =
        OutputOptionsBuilder.builder().withOutputFormatValue("json").toOptions();
    final OutputOptions unsupported =
        OutputOptionsBuilder.builder().withOutputFormatValue("xml").toOptions();

    assertThat(provider.supportsOutputFormat("importance", json), is(true));
    assertThat(provider.supportsOutputFormat("importance", unsupported), is(false));
  }

  @Test
  void supportsImportanceCommand() {
    final ImportanceCommandProvider provider = new ImportanceCommandProvider();

    assertThat(
        provider.supportsSchemaCrawlerCommand(
            "importance",
            SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions(),
            null,
            OutputOptionsBuilder.newOutputOptions()),
        is(true));
  }

  @Test
  void providesValidCommandLineCommand(final TestContext testContext) {
    final ImportanceCommandProvider provider = new ImportanceCommandProvider();
    final PluginCommand commandLineCommand = provider.getCommandLineCommand();

    PluginCommandTestUtility.testPluginCommand(testContext, commandLineCommand);
  }

  @Test
  void providesValidHelpCommand(final TestContext testContext) {
    final ImportanceCommandProvider provider = new ImportanceCommandProvider();
    final PluginCommand helpCommand = provider.getHelpCommand();

    PluginCommandTestUtility.testPluginCommand(testContext, helpCommand);
  }
}
