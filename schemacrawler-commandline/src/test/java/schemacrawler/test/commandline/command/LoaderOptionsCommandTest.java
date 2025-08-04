/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.createConnectedSchemaCrawlerShellState;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.FileHasContent.text;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.catalogLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ResolveTestContext
@WithTestDatabase
public class LoaderOptionsCommandTest {

  private final String COMMAND_HELP = "command_help/";

  @Test
  public void dynamicOptionValue(final DatabaseConnectionSource dataSource) throws Exception {
    final String[] args = {
      "--test-load-option", "true",
    };

    final ShellState state = createConnectedSchemaCrawlerShellState(dataSource);
    final CommandLine commandLine = createShellCommandLine(dataSource, state);

    commandLine.parseArgs(args);
  }

  @Test
  public void help(final TestContext testContext, final DatabaseConnectionSource dataSource)
      throws Exception {

    final ShellState state = createConnectedSchemaCrawlerShellState(dataSource);
    final CommandLine commandLine = createShellCommandLine(dataSource, state);

    final String helpMessage = commandLine.getSubcommands().get("load").getUsageMessage();

    assertThat(
        outputOf(text(helpMessage)),
        hasSameContentAs(
            classpathResource(COMMAND_HELP + testContext.testMethodFullName() + ".txt")));
  }

  private CommandLine createShellCommandLine(
      final DatabaseConnectionSource dataSource, final ShellState state) {

    final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
    final CommandLine commandLine = newCommandLine(commands, new StateFactory(state));
    final CommandLine loadCommandLine = commandLine.getSubcommands().getOrDefault("load", null);
    if (loadCommandLine != null) {
      addPluginCommands(loadCommandLine, catalogLoaderPluginCommands);
      commandLine.addSubcommand(loadCommandLine);
    }
    return commandLine;
  }
}
