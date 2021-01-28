/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static us.fatehi.utility.IOUtility.readResourceFully;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.UsageMessageSpec;
import picocli.CommandLine.ParseResult;
import schemacrawler.Version;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.catalogloader.ChainedCatalogLoader;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;

public class CommandLineUtility {

  public static void addDatabasePluginHelpCommands(final CommandLine commandLine) {
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    for (final DatabaseServerType databaseServerType : databaseConnectorRegistry) {
      final String pluginCommandName = databaseServerType.getDatabaseSystemIdentifier();
      final CommandSpec pluginCommandSpec = CommandSpec.create().name(pluginCommandName);
      commandLine.addSubcommand(pluginCommandName, pluginCommandSpec);
    }
  }

  public static void addLoadCommandOptions(final CommandLine commandLine)
      throws SchemaCrawlerException {
    requireNonNull(commandLine, "No command-line provided");

    final Collection<PluginCommandOption> loadCommandOptions = new ArrayList<>();
    final CatalogLoaderRegistry catalogLoaderRegistry = new CatalogLoaderRegistry();
    final ChainedCatalogLoader catalogLoaders = catalogLoaderRegistry.loadCatalogLoaders();
    for (final CatalogLoader catalogLoader : catalogLoaders) {
      loadCommandOptions.addAll(catalogLoader.getLoadCommandLineOptions());
    }

    if (loadCommandOptions.isEmpty()) {
      return;
    }

    final CommandSpec commandSpec = commandLine.getCommandSpec();
    final CommandSpec loadCommandSpec;
    if (commandSpec.subcommands().containsKey("load")) {
      loadCommandSpec = commandSpec.subcommands().get("load").getCommandSpec();
    } else {
      loadCommandSpec = commandSpec.mixins().get("load");
    }
    if (loadCommandSpec == null) {
      return;
    }

    for (final PluginCommandOption option : loadCommandOptions) {
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
      loadCommandSpec.addOption(
          OptionSpec.builder("--" + optionName)
              .description(helpText)
              .paramLabel(paramName)
              .type(option.getValueClass())
              .build());
    }
  }

  public static void addPluginCommand(
      final CommandLine commandLine, final PluginCommand pluginCommand, final boolean addAsMixins) {
    requireNonNull(commandLine, "No command-line provided");
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

  public static void addPluginCommands(final CommandLine commandLine)
      throws SchemaCrawlerException {
    addPluginCommands(commandLine, true);
  }

  public static void addPluginHelpCommands(final CommandLine commandLine)
      throws SchemaCrawlerException {
    addPluginCommands(commandLine, false);
  }

  public static CommandLine configureCommandLine(final CommandLine commandLine) {
    commandLine.setUnmatchedArgumentsAllowed(true);
    commandLine.setCaseInsensitiveEnumValuesAllowed(true);
    commandLine.setTrimQuotes(true);
    commandLine.setToggleBooleanFlags(false);

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
  public static Map<String, Object> matchedOptionValues(final ParseResult parseResult)
      throws SchemaCrawlerException {
    requireNonNull(parseResult, "No parse result provided");

    final Map<String, Object> options = new HashMap<>();

    final List<OptionSpec> matchedOptionSpecs = parseResult.matchedOptions();
    for (final OptionSpec matchedOptionSpec : matchedOptionSpecs) {
      if (matchedOptionSpec.userObject() != null) {
        continue;
      }
      final Object optionValue = matchedOptionSpec.getValue();
      if (optionValue == null) {
        continue;
      }
      final String optionName = matchedOptionSpec.longestName().replaceFirst("^\\-{0,2}", "");
      options.put(optionName, optionValue);
    }

    return options;
  }

  public static CommandLine newCommandLine(final Object object, final IFactory factory) {
    final CommandLine commandLine;
    if (factory == null) {
      commandLine = new CommandLine(object);
    } else {
      commandLine = new CommandLine(object, factory);
    }
    configureCommandLine(commandLine);
    return commandLine;
  }

  public static void printCommandLineErrorMessage(final String errorMessage) {
    System.err.printf("%s %s%n%n", Version.getProductName(), Version.getVersion());
    if (!isBlank(errorMessage)) {
      System.err.printf("Error: %s%n%n", errorMessage);
    } else {
      System.err.printf("Error: Unknown error%n%n");
    }

    System.err.println(readResourceFully("/command-line-error.footer.txt"));
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

  private static void addPluginCommands(final CommandLine commandLine, final boolean addAsMixins)
      throws SchemaCrawlerException {
    // Add commands for plugins
    final CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
    for (final PluginCommand pluginCommand : commandRegistry.getCommandLineCommands()) {
      addPluginCommand(commandLine, pluginCommand, addAsMixins);
    }
  }

  private CommandLineUtility() {
    // Prevent instantiation
  }
}
