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
package schemacrawler.tools.commandline.utility;


import static picocli.CommandLine.Model.UsageMessageSpec.*;

import java.util.Arrays;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.UsageMessageSpec;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;

public class CommandLineUtility
{

  private static void addPluginCommands(final CommandLine commandLine,
                                        final boolean addAsMixins)
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
      final UsageMessageSpec usageMessageSpec = new UsageMessageSpec();
      usageMessageSpec.header(pluginCommand.getHelpText());
      usageMessageSpec.description("");
      final CommandSpec pluginCommandSpec = CommandSpec.create()
                                                       .name(pluginCommandName)
                                                       .usageMessage(
                                                         usageMessageSpec);
      for (final PluginCommandOption option : pluginCommand)
      {
        final String optionName = option.getName();
        final String paramName = String.format("<%s>", optionName);
        final String helpText;
        if (option.getValueClass().isEnum())
        {
          helpText = String.format("%s%nUse one of ${COMPLETION-CANDIDATES}",
                                   option.getHelpText());
        }
        else
        {
          helpText = option.getHelpText();
        }
        pluginCommandSpec.addOption(OptionSpec.builder("--" + optionName)
                                              .description(helpText)
                                              .paramLabel(paramName)
                                              .type(option.getValueClass())
                                              .build());
      }
      if (addAsMixins)
      {
        commandLine.addMixin(pluginCommandName, pluginCommandSpec);
      }
      else
      {
        commandLine.addSubcommand(pluginCommandName, pluginCommandSpec);
      }
    }
  }

  private static CommandLine configureCommandLine(final CommandLine commandLine)
  {
    commandLine.setUnmatchedArgumentsAllowed(true);
    commandLine.setCaseInsensitiveEnumValuesAllowed(true);
    commandLine.setTrimQuotes(true);
    commandLine.setToggleBooleanFlags(false);

    commandLine.setHelpSectionKeys(Arrays.asList(SECTION_KEY_HEADER_HEADING,
                                                 SECTION_KEY_HEADER,
                                                 // SECTION_KEY_SYNOPSIS_HEADING,
                                                 // SECTION_KEY_SYNOPSIS,
                                                 SECTION_KEY_DESCRIPTION_HEADING,
                                                 SECTION_KEY_DESCRIPTION,
                                                 SECTION_KEY_PARAMETER_LIST_HEADING,
                                                 SECTION_KEY_PARAMETER_LIST,
                                                 SECTION_KEY_OPTION_LIST_HEADING,
                                                 SECTION_KEY_OPTION_LIST,
                                                 SECTION_KEY_COMMAND_LIST_HEADING,
                                                 SECTION_KEY_COMMAND_LIST,
                                                 SECTION_KEY_FOOTER_HEADING,
                                                 SECTION_KEY_FOOTER));

    return commandLine;
  }

  public static CommandLine newCommandLine(final Object object,
                                           final CommandLine.IFactory factory,
                                           final boolean addPluginsAsMixins)
  {
    final CommandLine commandLine = newCommandLine(object, factory);
    try
    {
      addPluginCommands(commandLine, addPluginsAsMixins);
      configureCommandLine(commandLine);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SchemaCrawlerRuntimeException(
        "Could not initialize command-line",
        e);
    }
    return commandLine;
  }

  private static CommandLine newCommandLine(final Object object,
                                            final CommandLine.IFactory factory)
  {
    final CommandLine commandLine;
    if (factory == null)
    {
      commandLine = new CommandLine(object);
    }
    else
    {
      commandLine = new CommandLine(object, factory);
    }

    return commandLine;
  }

  public static Config retrievePluginOptions(final CommandLine.ParseResult parseResult)
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

  private CommandLineUtility()
  {
    // Prevent instantiation
  }

}
