/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.CommandLine;
import schemacrawler.Version;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.commandline.utility.CommandLineUtility;

public final class ConnectionTest {

  private static final Logger LOGGER = Logger.getLogger(ConnectionTest.class.getName());

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

      System.out.printf("%s%n%n", Version.about());

      state.setDeferCatalogLoad(true);
      final StateFactory stateFactory = new StateFactory(state);

      final ConnectionTestCommands commands = new ConnectionTestCommands();
      final CommandLine commandLine = newCommandLine(commands, stateFactory);
      commandLine.parseArgs(args);

      CommandLineUtility.printEnvironment(state);

      Throwable throwable = null;
      try {
        executeCommandLine(commandLine);
      } catch (final Throwable e) {
        throwable = e;
      }
      System.out.printf("%n%n");
      System.out.println(CommandLineUtility.getConnectionInfo(state));
      if (throwable != null) {
        System.out.println("  " + throwable.getMessage());
        LOGGER.log(Level.CONFIG, throwable.getMessage(), throwable);
      }
      System.out.flush();

      return 0;
    } finally {
      state.close();
    }
  }

  private static void executeCommandLine(final CommandLine commandLine) {
    final Map<String, Object> subcommands = commandLine.getMixins();
    final Runnable command = (Runnable) subcommands.get("connect");
    LOGGER.log(Level.INFO, "Running command " + command.getClass().getSimpleName());
    command.run();
  }

  private ConnectionTest() {
    // Prevent instantiation
  }
}
