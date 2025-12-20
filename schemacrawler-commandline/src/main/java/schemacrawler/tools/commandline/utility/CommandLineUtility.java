/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.utility;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.readResourceFully;
import static us.fatehi.utility.Utility.isBlank;

import java.io.PrintWriter;
import java.io.StringWriter;
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
import schemacrawler.Version;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.commandline.command.AvailableCatalogLoaders;
import schemacrawler.tools.commandline.command.AvailableCommands;
import schemacrawler.tools.commandline.command.AvailableJDBCDrivers;
import schemacrawler.tools.commandline.command.AvailableServers;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;
import us.fatehi.utility.database.ConnectionInfoBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.BaseProductVersion;
import us.fatehi.utility.property.JvmArchitectureInfo;
import us.fatehi.utility.property.JvmSystemInfo;
import us.fatehi.utility.property.OperatingSystemInfo;
import us.fatehi.utility.property.ProductVersion;

public class CommandLineUtility {

  public static final Supplier<Collection<PluginCommand>> catalogLoaderPluginCommands =
      () -> CatalogLoaderRegistry.getCatalogLoaderRegistry().getCommandLineCommands();

  public static final Supplier<Collection<PluginCommand>> catalogLoaderPluginHelpCommands =
      () -> CatalogLoaderRegistry.getCatalogLoaderRegistry().getHelpCommands();

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

  public static String getConnectionInfo(final ShellState state) {

    final StringWriter stringWriter = new StringWriter();
    try (final PrintWriter writer = new PrintWriter(stringWriter)) {

      writer.print("Connection:");

      final boolean isConnectedState = state.isConnected();
      if (isConnectedState) {
        final DatabaseConnectionSource dbConnectionSource = state.getDataSource();
        try (final Connection connection = dbConnectionSource.get(); ) {
          final ConnectionInfoBuilder builder = ConnectionInfoBuilder.builder(connection);
          final ProductVersion databaseInfo =
              new BaseProductVersion(builder.buildDatabaseInformation());
          final ProductVersion jdbcDriverInfo =
              new BaseProductVersion(builder.buildJdbcDriverInformation());
          writer.printf("%n  %s%n  %s", databaseInfo, jdbcDriverInfo);
        } catch (final Exception e) {
          writer.printf("%n  %s", e.getMessage());
        }
      } else {
        writer.printf("%n  Not connected to a database");
      }
      writer.flush();
    }

    return stringWriter.toString();
  }

  public static String getEnvironment(final ShellState state) {
    if (state == null) {
      return "";
    }
    return "Environment:%n  %s%n  %s%n  %s%n  %s"
        .formatted(
            Version.version(),
            OperatingSystemInfo.operatingSystemInfo(),
            JvmSystemInfo.jvmSystemInfo(),
            JvmArchitectureInfo.jvmArchitectureInfo());
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
    System.err.println(CommandLineUtility.getConnectionInfo(state));
  }

  public static void printEnvironment(final ShellState state) {
    System.out.println(CommandLineUtility.getEnvironment(state));
    System.out.println();

    new AvailableJDBCDrivers().printHelp(System.out);
    new AvailableServers().printHelp(System.out);
    new AvailableCatalogLoaders().printHelp(System.out);
    new AvailableCommands().printHelp(System.out);
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
    final String paramName = "<%s>".formatted(optionName);
    final String[] helpText;
    if (option.getValueClass().isEnum()) {
      helpText = new String[1];
      helpText[0] = "%s%nUse one of ${COMPLETION-CANDIDATES}".formatted(option.getHelpText()[0]);
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
