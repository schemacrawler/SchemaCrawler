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
import static schemacrawler.tools.commandline.utility.CommandLineUtility.logFullStackTrace;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.logSafeArguments;

import java.util.Map;
import java.util.logging.Level;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import schemacrawler.Version;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;
import sf.util.SchemaCrawlerLogger;

public final class SchemaCrawlerCommandLine
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger.getLogger(
    SchemaCrawlerCommandLine.class.getName());

  private static void addPluginCommands(final CommandLine cmd)
    throws SchemaCrawlerException
  {
    // Add commands for plugins
    final CommandRegistry commandRegistry = new CommandRegistry();
    for (final PluginCommand pluginCommand : commandRegistry.getCommandLineCommands())
    {
      if (pluginCommand == null || pluginCommand.isEmpty())
      {
        continue;
      }
      final String pluginCommandName = pluginCommand.getName();
      final CommandSpec pluginCommandSpec = CommandSpec.create()
                                                       .name(pluginCommandName);
      for (final PluginCommandOption option : pluginCommand)
      {
        pluginCommandSpec.addOption(OptionSpec.builder("--" + option.getName())
                                              .usageHelp(true)
                                              .description(option.getHelpText())
                                              .type(option.getValueClass())
                                              .build());
      }
      cmd.addMixin(pluginCommandName, pluginCommandSpec);
    }
  }

  public static void execute(final String[] args)
  {
    try
    {
      requireNonNull(args, "No arguments provided");

      final SchemaCrawlerShellState state = new SchemaCrawlerShellState();

      final StateFactory stateFactory = new StateFactory(state);
      final SchemaCrawlerCommandLineCommands commands = new SchemaCrawlerCommandLineCommands();

      final CommandLine cmd = new CommandLine(commands, stateFactory);
      cmd.setUnmatchedArgumentsAllowed(true);
      cmd.setCaseInsensitiveEnumValuesAllowed(true);
      cmd.setTrimQuotes(true);
      cmd.setToggleBooleanFlags(false);

      addPluginCommands(cmd);

      final CommandLine.ParseResult parseResult = cmd.parseArgs(args);

      final Config additionalConfig = retrievePluginOptions(parseResult);
      state.setAdditionalConfiguration(additionalConfig);

      executeCommandLine(cmd);
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

  private static void executeCommandLine(final CommandLine cmd)
  {
    final Map<String, Object> subcommands = cmd.getMixins();

    for (final String commandName : new String[] {
      "logCommand",
      "configFileCommand",
      "connectCommand",
      "filterCommand",
      "limitCommand",
      "grepCommand",
      "showCommand",
      "sortCommand",
      "loadCommand",
      "executeCommand"
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

  private static Config retrievePluginOptions(final CommandLine.ParseResult parseResult)
    throws SchemaCrawlerException
  {
    // Retrieve options, and save them to the state
    final CommandRegistry commandRegistry = new CommandRegistry();
    final Config additionalConfig = new Config();
    for (final PluginCommand pluginCommand : commandRegistry.getCommandLineCommands())
    {
      if (pluginCommand == null || pluginCommand.isEmpty())
      {
        continue;
      }
      for (final PluginCommandOption option : pluginCommand)
      {
        final String optionName = option.getName();
        if (parseResult.hasMatchedOption(optionName))
        {
          final Object value = parseResult.matchedOptionValue(optionName, null);
          additionalConfig.put(optionName,
                               value == null? null: String.valueOf(value));
        }
      }
    }
    return additionalConfig;
  }

  private SchemaCrawlerCommandLine()
  {
    // Prevent instantiation
  }

}
