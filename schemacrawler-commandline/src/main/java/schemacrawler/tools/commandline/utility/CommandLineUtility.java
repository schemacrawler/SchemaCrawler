/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigValueFactory;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.UsageMessageSpec;
import picocli.CommandLine.ParseResult;
import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;

public class CommandLineUtility {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(CommandLineUtility.class.getName());

  public static void addPluginCommand(
      final CommandLine commandLine, final PluginCommand pluginCommand, final boolean addAsMixins) {
    if (pluginCommand == null || pluginCommand.isEmpty()) {
      return;
    }

    final CommandSpec pluginCommandSpec = toCommandSpec(pluginCommand);
    final String pluginCommandName = pluginCommandSpec.name();
    if (addAsMixins) {
      commandLine.addMixin(pluginCommandName, pluginCommandSpec);
    } else {
      commandLine.addSubcommand(pluginCommandName, pluginCommandSpec);
    }
  }

  public static CommandLine configureCommandLine(final CommandLine commandLine) {
    commandLine.setUnmatchedArgumentsAllowed(true);
    commandLine.setCaseInsensitiveEnumValuesAllowed(true);
    commandLine.setTrimQuotes(true);
    commandLine.setToggleBooleanFlags(false);

    return commandLine;
  }

  public static Map<String, Object> loadConfig() {

    final Config config = loadConfig("schemacrawler.config");
    final Config colormapConfig = loadConfig("schemacrawler.colormap");

    final Config totalConfig =
        config
            .withValue(
                "schemacrawler.format.color_map",
                ConfigValueFactory.fromMap(colormapConfig.root().unwrapped()))
            .withFallback(ConfigFactory.load())
            .resolve();

    final Map<String, Object> configMap =
        totalConfig
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() != null)
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().unwrapped()));

    return configMap;
  }

  public static CommandLine newCommandLine(
      final Object object, final IFactory factory, final boolean addPluginsAsMixins) {
    final CommandLine commandLine = newCommandLine(object, factory);
    try {
      addPluginCommands(commandLine, addPluginsAsMixins);
      addDatabasePluginHelpCommands(commandLine, addPluginsAsMixins);
      configureCommandLine(commandLine);
    } catch (final SchemaCrawlerException e) {
      throw new SchemaCrawlerRuntimeException("Could not initialize command-line", e);
    }
    return commandLine;
  }

  /**
   * SchemaCrawler plugins are registered on-the-fly, by adding them to the classpath. Inspect the
   * command-line to see if there are any additional plugin-specific options passed in from the
   * command-line, and put them in the configuration.
   *
   * @param parseResult Result of parsing the command-line
   * @return Config with additional plugin-specific command-line options
   * @throws SchemaCrawlerException On an exception
   */
  public static Map<String, Object> retrievePluginOptions(final ParseResult parseResult)
      throws SchemaCrawlerException {
    requireNonNull(parseResult, "No parse result provided");

    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    final Map<String, Object> commandConfig = new HashMap<>();
    for (final PluginCommand pluginCommand : commandRegistry.getCommandLineCommands()) {
      if (pluginCommand == null || pluginCommand.isEmpty()) {
        continue;
      }
      for (final PluginCommandOption option : pluginCommand) {
        final String optionName = option.getName();
        if (parseResult.hasMatchedOption(optionName)) {
          final Object value = parseResult.matchedOptionValue(optionName, null);
          if (value != null) {
            commandConfig.put(optionName, value);
          }
        }
      }
    }

    return commandConfig;
  }

  public static CommandSpec toCommandSpec(final PluginCommand pluginCommand) {
    requireNonNull(pluginCommand, "No plugin command provided");
    if (pluginCommand.isEmpty()) {
      throw new NullPointerException("Empty plugin command provided");
    }

    final String pluginCommandName = pluginCommand.getName();

    final UsageMessageSpec usageMessageSpec = new UsageMessageSpec();
    usageMessageSpec.header(pluginCommand.getHelpHeader());
    if (pluginCommand.hasHelpDescription()) {
      usageMessageSpec.description(pluginCommand.getHelpDescription().get());
    } else {
      usageMessageSpec.description("");
    }
    usageMessageSpec.synopsisHeading("Command:%n");
    usageMessageSpec.customSynopsis(pluginCommandName);
    usageMessageSpec.optionListHeading("Options:%n");
    if (pluginCommand.hasHelpFooter()) {
      usageMessageSpec.footer(pluginCommand.getHelpFooter().get());
    }

    final CommandSpec pluginCommandSpec =
        CommandSpec.create().name(pluginCommandName).usageMessage(usageMessageSpec);
    for (final PluginCommandOption option : pluginCommand) {
      final String optionName = option.getName();
      final String paramName = String.format("<%s>", optionName);
      final String[] helpText;
      if (option.getValueClass().isEnum()) {
        helpText = new String[1];
        helpText[0] =
            String.format("%s%nUse one of ${COMPLETION-CANDIDATES}", option.getHelpText()[0]);
      } else {
        helpText = option.getHelpText();
      }
      pluginCommandSpec.addOption(
          OptionSpec.builder("--" + optionName)
              .description(helpText)
              .paramLabel(paramName)
              .type(option.getValueClass())
              .build());
    }
    return pluginCommandSpec;
  }

  private static void addDatabasePluginHelpCommands(
      final CommandLine commandLine, final boolean addAsMixins) {
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    for (final DatabaseServerType databaseServerType : databaseConnectorRegistry) {
      final String pluginCommandName = databaseServerType.getDatabaseSystemIdentifier();
      final CommandSpec pluginCommandSpec = CommandSpec.create().name(pluginCommandName);
      if (addAsMixins) {
        commandLine.addMixin(pluginCommandName, pluginCommandSpec);
      } else {
        commandLine.addSubcommand(pluginCommandName, pluginCommandSpec);
      }
    }
  }

  private static void addPluginCommands(final CommandLine commandLine, final boolean addAsMixins)
      throws SchemaCrawlerException {
    // Add commands for plugins
    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    for (final PluginCommand pluginCommand : commandRegistry.getCommandLineCommands()) {
      addPluginCommand(commandLine, pluginCommand, addAsMixins);
    }
  }

  private static Config loadConfig(final String baseName) {
    final ConfigParseOptions configParseOptions =
        ConfigParseOptions.defaults().setAllowMissing(true);
    final Config config =
        ConfigFactory.parseFileAnySyntax(new File(baseName), configParseOptions)
            .withFallback(ConfigFactory.parseResources(baseName, configParseOptions));
    LOGGER.log(Level.CONFIG, () -> config.root().render());
    return config;
  }

  private static CommandLine newCommandLine(final Object object, final IFactory factory) {
    final CommandLine commandLine;
    if (factory == null) {
      commandLine = new CommandLine(object);
    } else {
      commandLine = new CommandLine(object, factory);
    }

    return commandLine;
  }

  private CommandLineUtility() {
    // Prevent instantiation
  }
}
