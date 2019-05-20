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
         helpCommand = true)
public final class CommandLineHelpCommand
  implements Runnable
{

  private static CommandLine databaseConnectorCommand(final String databaseSystemIdentifier)
  {
    final DatabaseConnectorRegistry databaseConnectorRegistry = new DatabaseConnectorRegistry();
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

    return subcommandLine;
  }

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

    final CommandLine parent = newCommandLine(new SchemaCrawlerShellCommands(),
                                              new StateFactory(new SchemaCrawlerShellState()),
                                              false);

    if (commands != null && commands.length > 0)
    {
      showHelpForSubcommand(parent, commands[0]);
    }
    else
    {
      new SystemCommand().run();
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

  private void showHelpForSubcommand(final CommandLine parent,
                                     final String commandName)
  {
    if (isBlank(commandName))
    {
      return;
    }
    final DatabaseConnectorRegistry databaseConnectorRegistry = new DatabaseConnectorRegistry();
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
