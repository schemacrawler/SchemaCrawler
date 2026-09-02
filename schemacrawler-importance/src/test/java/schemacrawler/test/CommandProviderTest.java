/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.command.ImportanceCommandProvider;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
public class CommandProviderTest {

  @Test
  public void testCommandProvider() throws Exception {
    final ImportanceCommandProvider commandProvider = new ImportanceCommandProvider();
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final OutputOptions outputOptions = OutputOptionsBuilder.newOutputOptions();
    assertThat(
        commandProvider.supportsSchemaCrawlerCommand(
            "importance", schemaCrawlerOptions, null, outputOptions),
        is(true));
  }

  @Test
  public void testImportanceCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new ImportanceCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testImportanceCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new ImportanceCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }
}
