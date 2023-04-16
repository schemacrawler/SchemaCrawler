/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.UsageMessageSpec;
import picocli.CommandLine.ParseResult;
import schemacrawler.BaseProductVersion;
import schemacrawler.JvmSystemInfo;
import schemacrawler.OperatingSystemInfo;
import schemacrawler.ProductVersion;
import schemacrawler.Version;
import schemacrawler.crawl.ConnectionInfoBuilder;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class CommandLineUtility {

  public static final Supplier<Collection<PluginCommand>> catalogLoaderPluginCommands =
      () -> new CatalogLoaderRegistry().getCommandLineCommands();

  public static final Supplier<Collection<PluginCommand>> catalogLoaderPluginHelpCommands =
      () -> new CatalogLoaderRegistry().getHelpCommands();

  public static final Supplier<Collection<PluginCommand>> commandPluginCommands =
      () -> CommandRegistry.getCommandRegistry().getCommandLineCommands();

  public static final Supplier<Collection<PluginCommand>> commandPluginHelpCommands =
      () -> CommandRegistry.getCommandRegistry().getHelpCommands();

  public static final Supplier<Collection<PluginCommand>> serverPluginHelpCommands =
      () -> DatabaseConnectorRegistry.getDatabaseConnectorRegistry().getHelpCommands();

  public static void addPluginCommands(
      final CommandLine commandLine, final Supplier<Collection<PluginCommand>> pluginCommands) {
    addPluginCommands(commandLine, pluginCommands, true);
  }

  public static void addPluginHelpCommands(
      final CommandLine commandLine, final Supplier<Collection<PluginCommand>> pluginCommands) {
    addPluginCommands(commandLine, pluginCommands, false);
  }

  public static CommandLine configureCommandLine(final CommandLine commandLine) {
    commandLine.setUnmatchedArgumentsAllowed(true);
    commandLine.setCaseInsensitiveEnumValuesAllowed(true);
    commandLine.setTrimQuotes(true);
    commandLine.setToggleBooleanFlags(false);

    return commandLine;
  }

  public static String getConnectionInfo(final DatabaseConnectionSource dbConnectionSource) {
    final String connectionInfoString = "";
    if (dbConnectionSource == null) {
      return connectionInfoString;
    }
    try (final Connection connection = dbConnectionSource.get(); ) {
      final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfoBuilder.builder(connection);

      final ProductVersion databaseInfo =
          new BaseProductVersion(connectionInfoBuilder.buildDatabaseInfo());
      final ProductVersion jdbcDriverInfo =
          new BaseProductVersion(connectionInfoBuilder.buildJdbcDriverInfo());
      return String.format("  %s%n  %s%n", databaseInfo, jdbcDriverInfo);
    } catch (final Exception e) {
      // Ignore - do not log
    }
    return connectionInfoString;
  }

  public static String getEnvironment(final ShellState state) {
    if (state == null) {
      return "";
    }
    return String.format(
        "Environment:%n  %s%n  %s%n  %s%n%s",
        Version.version(),
        OperatingSystemInfo.operatingSystemInfo(),
        JvmSystemInfo.jvmSystemInfo(),
        getConnectionInfo(state.getDataSource()));
  }

  /**
   * SchemaCrawler plugins are registered on-the-fly, by adding them to the classpath. Inspect the
   * command-line to see if there are any additional plugin-specific options passed in from the
   * command-line, and put them in the configuration.
   *
   * @param parseResult Result of parsing the command-line
   * @return Config with additional plugin-specific command-line options
   */
  public static Map<String, Object> matchedOptionValues(final ParseResult parseResult) {
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

  public static void printCommandLineErrorMessage(
      final String errorMessage, final ShellState state) {
    System.err.printf("%s%n%n", Version.version());
    if (!isBlank(errorMessage)) {
      System.err.printf("Error: %s%n%n", errorMessage);
    } else {
      System.err.printf("Error: Unknown error%n%n");
    }

    System.err.println(readResourceFully("/command-line-error.footer.txt"));

    System.err.println();
    System.err.println(CommandLineUtility.getEnvironment(state));
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
      pluginCommandSpec.addOption(toOptionSpec(option));
    }
    return pluginCommandSpec;
  }

  private static void addPluginCommand(
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

  private static void addPluginCommands(
      final CommandLine commandLine,
      final Supplier<Collection<PluginCommand>> pluginCommands,
      final boolean addAsMixins) {
    requireNonNull(commandLine, "No command-line provided");
    requireNonNull(pluginCommands, "No plugin commands supplier provided");
    for (final PluginCommand pluginCommand : pluginCommands.get()) {
      addPluginCommand(commandLine, pluginCommand, addAsMixins);
    }
  }

  private static OptionSpec toOptionSpec(final PluginCommandOption option) {
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
    return OptionSpec.builder("--" + optionName)
        .description(helpText)
        .paramLabel(paramName)
        .type(option.getValueClass())
        .build();
  }

  private CommandLineUtility() {
    // Prevent instantiation
  }
}
