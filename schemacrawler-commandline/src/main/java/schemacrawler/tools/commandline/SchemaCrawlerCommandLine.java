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
package schemacrawler.tools.commandline;


import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.CommandLineLoggingUtility.logFullStackTrace;
import static schemacrawler.tools.commandline.utility.CommandLineLoggingUtility.logSafeArguments;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.retrievePluginOptions;

import java.util.Map;
import java.util.logging.Level;

import picocli.CommandLine;
import schemacrawler.Version;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import sf.util.SchemaCrawlerLogger;

public final class SchemaCrawlerCommandLine
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger.getLogger(
    SchemaCrawlerCommandLine.class.getName());

  public static void execute(final String[] args)
  {
    try
    {
      requireNonNull(args, "No arguments provided");

      final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
      final StateFactory stateFactory = new StateFactory(state);

      final SchemaCrawlerCommandLineCommands commands = new SchemaCrawlerCommandLineCommands();
      final CommandLine commandLine = new CommandLine(commands, stateFactory);
      addPluginCommands(commandLine, true);
      final CommandLine.ParseResult parseResult = commandLine.parseArgs(args);
      final Config additionalConfig = retrievePluginOptions(parseResult);
      state.addAdditionalConfiguration(additionalConfig);

      executeCommandLine(commandLine);
    }
    catch (final Throwable e)
    {
      System.err.printf("%s %s%n%n",
                        Version.getProductName(),
                        Version.getVersion());
      final String errorMessage = e.getMessage();
      if (errorMessage != null)
      {
        System.err.printf("Error: %s%n%n", errorMessage);
      }
      System.err.println(
        "Re-run SchemaCrawler with just the\n-?\noption for help");
      System.err.println();
      System.err.println(
        "Or, re-run SchemaCrawler with an additional\n--log-level=CONFIG\noption for details on the error");
      logSafeArguments(args);
      logFullStackTrace(Level.SEVERE, e);
    }

  }

  private static void executeCommandLine(final CommandLine commandLine)
  {
    final Map<String, Object> subcommands = commandLine.getMixins();

    for (final String commandName : new String[] {
      "log",
      "configfile",
      "connect",
      "filter",
      "limit",
      "grep",
      "show",
      "sort",
      "load",
      "execute"
    })
    {
      final Runnable command = (Runnable) subcommands.get(commandName);
      if (command != null)
      {
        LOGGER.log(Level.INFO,
                   "Running command " + command.getClass().getSimpleName());
        command.run();
      }
    }
  }

  private SchemaCrawlerCommandLine()
  {
    // Prevent instantiation
  }

}
