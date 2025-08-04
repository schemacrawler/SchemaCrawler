/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler;

import static java.util.Objects.requireNonNull;
import static picocli.CommandLine.populateCommand;

import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.CommandLine;
import schemacrawler.tools.commandline.ConnectionTest;
import schemacrawler.tools.commandline.ConnectionTestOptions;
import schemacrawler.tools.commandline.InteractiveShellOptions;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerShell;
import schemacrawler.tools.commandline.command.CommandLineHelpCommand;
import schemacrawler.tools.commandline.command.LogCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.registry.JDBCDriverRegistry;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.SystemExitException;
import us.fatehi.utility.UtilityLogger;
import us.fatehi.utility.property.JvmArchitectureInfo;
import us.fatehi.utility.property.JvmSystemInfo;
import us.fatehi.utility.property.OperatingSystemInfo;

/** Main class that takes arguments for a database for crawling a schema. */
public final class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  private static final String SC_EXIT_WITH_EXCEPTION = "SC_EXIT_WITH_EXCEPTION";

  public static void main(final String... args) throws Exception {
    requireNonNull(args, "No command-line arguments provided");

    final CommandLine commandLine = new CommandLine(new LogCommand());
    commandLine.setUnmatchedArgumentsAllowed(true);
    commandLine.execute(args);

    // Log details of runtime environment
    LOGGER.log(Level.INFO, String.valueOf(Version.version()));
    LOGGER.log(Level.INFO, String.valueOf(OperatingSystemInfo.operatingSystemInfo()));
    LOGGER.log(Level.INFO, String.valueOf(JvmSystemInfo.jvmSystemInfo()));
    LOGGER.log(Level.INFO, String.valueOf(JvmArchitectureInfo.jvmArchitectureInfo()));
    final UtilityLogger logger = new UtilityLogger(LOGGER);
    logger.logSafeArguments(args);
    logger.logSystemClasspath();
    logger.logSystemProperties();
    JDBCDriverRegistry.getJDBCDriverRegistry(); // Will log

    final ConnectionTestOptions connectionTestOptions = new ConnectionTestOptions();
    populateCommand(connectionTestOptions, args);
    final boolean isConnectionTest = connectionTestOptions.isConnectionTest();

    final InteractiveShellOptions interactiveShellOptions = new InteractiveShellOptions();
    populateCommand(interactiveShellOptions, args);
    final boolean isInteractive = interactiveShellOptions.isInteractive();

    int exitCode = 0;
    if (isConnectionTest) {
      exitCode = ConnectionTest.execute(args);
    } else if (isInteractive) {
      exitCode = SchemaCrawlerShell.execute(args);
    } else {
      if (showHelpIfRequested(args) || showVersionIfRequested(args)) {
        return;
      }
      exitCode = SchemaCrawlerCommandLine.execute(args);
    }

    if (exitCode == 0) {
      return;
    }
    exit(exitCode);
  }

  /**
   * Use an exit strategy to exit either by throwing a runtime exception or by calling System.exit.
   *
   * @param exitCode Exit code to exit with.
   */
  private static void exit(int exitCode) {
    final String exitWithExceptionValue =
        PropertiesUtility.getSystemConfigurationProperty(SC_EXIT_WITH_EXCEPTION, "false");
    final boolean exitWithException = Boolean.parseBoolean(exitWithExceptionValue);
    if (exitWithException) {
      throw new SystemExitException(exitCode, "SchemaCrawler has exited with an error");
    }
    System.exit(exitCode);
  }

  private static boolean showHelpIfRequested(final String[] args) {
    final CommandLineHelpCommand commandLineHelpCommand = new CommandLineHelpCommand();
    final CommandLine commandLine = new CommandLine(commandLineHelpCommand);
    commandLine.setUnmatchedArgumentsAllowed(true);
    commandLine.parseArgs(args);
    if (commandLineHelpCommand.isHelpRequested()) {
      commandLineHelpCommand.run();
      return true;
    }
    return false;
  }

  private static boolean showVersionIfRequested(final String[] args) {
    final ShellState state = new ShellState();
    final SystemCommand systemCommand = new SystemCommand(state);
    final CommandLine commandLine = new CommandLine(systemCommand);
    commandLine.setUnmatchedArgumentsAllowed(true);
    commandLine.parseArgs(args);
    if (systemCommand.isVersionRequested() || systemCommand.isShowEnvironment()) {
      systemCommand.run();
      return true;
    }
    return false;
  }

  private Main() {
    // Prevent instantiation
  }
}
