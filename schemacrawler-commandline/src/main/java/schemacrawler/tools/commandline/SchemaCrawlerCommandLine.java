/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.catalogLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.commandPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.printCommandLineErrorMessage;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.CommandLine;
import picocli.CommandLine.PicocliException;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.commandline.utility.CommandLineLogger;
import us.fatehi.utility.UtilityLogger;

public final class SchemaCrawlerCommandLine {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerCommandLine.class.getName());

  /**
   * Returns a zero exit code on successful termination. Non-zero otherwise.
   *
   * @param args Command-line arguments
   * @return System exit code
   */
  public static int execute(final String[] args) {

    final ShellState state = new ShellState();
    try {
      requireNonNull(args, "No arguments provided");

      state.setDeferCatalogLoad(true);
      final StateFactory stateFactory = new StateFactory(state);

      final SchemaCrawlerCommandLineCommands commands = new SchemaCrawlerCommandLineCommands();
      final CommandLine commandLine = newCommandLine(commands, stateFactory);
      addPluginCommands(commandLine, catalogLoaderPluginCommands);
      addPluginCommands(commandLine, commandPluginCommands);
      commandLine.parseArgs(args);

      executeCommandLine(commandLine);

      return 0;
    } catch (final Throwable throwable) {
      final UtilityLogger logger = new UtilityLogger(LOGGER);
      logger.logSafeArguments(args);
      logger.logFatalStackTrace(throwable);
      final CommandLineLogger commandLineLogger = new CommandLineLogger(LOGGER);
      commandLineLogger.logState(state);

      final String errorMessage = extractErrorMessage(throwable);
      printCommandLineErrorMessage(errorMessage, state);

      return 1;
    } finally {
      state.close();
    }
  }

  private static void executeCommandLine(final CommandLine commandLine) {
    final Map<String, Object> subcommands = commandLine.getMixins();

    for (final String commandName :
        new String[] {
          "log", "configfile", "connect", "limit", "grep", "filter", "showstate", "load", "execute"
        }) {
      final Runnable command = (Runnable) subcommands.get(commandName);
      LOGGER.log(Level.INFO, "Running command " + command.getClass().getSimpleName());
      command.run();
    }
  }

  private static String extractErrorMessage(final Throwable throwable) {
    final String errorMessage;
    if (throwable instanceof PicocliException) {
      final Throwable cause = throwable.getCause();
      if (cause != null && !isBlank(cause.getMessage())) {
        errorMessage = cause.getMessage();
      } else {
        errorMessage = throwable.getMessage();
      }
    } else {
      errorMessage = throwable.getMessage();
    }
    return errorMessage;
  }

  private SchemaCrawlerCommandLine() {
    // Prevent instantiation
  }
}
