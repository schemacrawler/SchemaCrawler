/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.commandline.command;


import static picocli.CommandLine.Model.UsageMessageSpec.*;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_FOOTER;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.*;
import static sf.util.Utility.isBlank;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.*;
import schemacrawler.tools.commandline.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;

@Command(name = "help",
         header = "Displays SchemaCrawler command-line help",
         helpCommand = true,
         headerHeading = "",
         synopsisHeading = "Command:%n",
         customSynopsis = {
           "help"
         },
         optionListHeading = "Options:%n")
public final class CommandLineHelpCommand
  implements Runnable
{

  private Help.Ansi ansi;
  @Parameters
  private String[] commands;
  private PrintStream err;
  @Option(names = { "-h", "--help" },
          usageHelp = true,
          description = "Displays SchemaCrawler command-line help")
  private boolean helpRequested;
  private PrintStream out;
  @Spec
  private Model.CommandSpec spec;

  public boolean isHelpRequested()
  {
    return helpRequested;
  }

  @Override
  public void run()
  {
    ansi = Help.Ansi.AUTO;
    out = System.out;
    err = System.err;

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final CommandLine parent = newCommandLine(new SchemaCrawlerShellCommands(),
                                              new StateFactory(state),
                                              false);

    if (commands != null && commands.length > 0)
    {
      configureHelpForSubcommand(parent);
      showHelpForSubcommand(parent, commands[0]);
    }
    else
    {
      new SystemCommand(state).printVersion();

      out.println();
      out.println();

      final List<String> commandNames = new ArrayList<>(Arrays.asList("log",
                                                                      "config-file",
                                                                      "connect",
                                                                      "limit",
                                                                      "grep",
                                                                      "filter",
                                                                      "load"));
      new AvailableCommands().iterator().forEachRemaining(commandNames::add);
      commandNames.addAll(Arrays.asList("show", "sort", "execute"));

      for (final String commandName : commandNames)
      {
        showHelpForSubcommand(parent, commandName);
      }

    }
  }

  private CommandLine databaseConnectorCommand(final String databaseSystemIdentifier)
  {
    final DatabaseConnectorRegistry databaseConnectorRegistry = DatabaseConnectorRegistry
      .getDatabaseConnectorRegistry();
    final DatabaseConnector databaseConnector = databaseConnectorRegistry.lookupDatabaseConnector(
      databaseSystemIdentifier);

    @Command
    class EmptyCommand
    {
    }

    final CommandLine commandLine = new CommandLine(new EmptyCommand());

    final PluginCommand helpCommand = databaseConnector.getHelpCommand();
    addPluginCommand(commandLine, helpCommand, false);

    final CommandLine subcommandLine = commandLine.getSubcommands()
                                                  .get(databaseSystemIdentifier);
    configureCommandLine(subcommandLine);
    configureHelpForSubcommand(subcommandLine);

    return subcommandLine;
  }

  private void configureHelpForSubcommand(final CommandLine commandLine) {

    if (commandLine == null) {
      return;
    }

    commandLine.setHelpSectionKeys(Arrays.asList(// SECTION_KEY_HEADER_HEADING,
                                                 SECTION_KEY_HEADER,
                                                 // SECTION_KEY_SYNOPSIS_HEADING,
                                                 // SECTION_KEY_SYNOPSIS,
                                                 // SECTION_KEY_DESCRIPTION_HEADING,
                                                 SECTION_KEY_DESCRIPTION,
                                                 // SECTION_KEY_PARAMETER_LIST_HEADING,
                                                 SECTION_KEY_PARAMETER_LIST,
                                                 // SECTION_KEY_OPTION_LIST_HEADING,
                                                 SECTION_KEY_OPTION_LIST,
                                                 // SECTION_KEY_COMMAND_LIST_HEADING,
                                                 SECTION_KEY_COMMAND_LIST,
                                                 // SECTION_KEY_FOOTER_HEADING,
                                                 SECTION_KEY_FOOTER));
  }

  private void showHelpForSubcommand(final CommandLine parent,
                                     final String commandName)
  {
    if (isBlank(commandName))
    {
      return;
    }
    final DatabaseConnectorRegistry databaseConnectorRegistry = DatabaseConnectorRegistry
      .getDatabaseConnectorRegistry();
    final CommandLine subCommand;
    if (databaseConnectorRegistry.hasDatabaseSystemIdentifier(commandName))
    {
      subCommand = databaseConnectorCommand(commandName);
    }
    else
    {
      subCommand = parent.getSubcommands().get(commandName);
    }
    if (subCommand != null)
    {
      subCommand.usage(out, ansi);
      System.out.println();
      System.out.println();
    }
  }

}
