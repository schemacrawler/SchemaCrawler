/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static us.fatehi.utility.Utility.isBlank;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.CommandLine;
import schemacrawler.CommandLineExecutor;
import schemacrawler.schemacrawler.Version;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.commandline.utility.CommandLineUtility;
import us.fatehi.utility.jdbc.driver.JdbcConnectionUtility;

public final class ConnectionTest implements CommandLineExecutor {

  private static final Logger LOGGER = Logger.getLogger(ConnectionTest.class.getName());

  /**
   * Returns a zero exit code on successful termination. Non-zero otherwise.
   *
   * @param args Command-line arguments
   * @return System exit code
   */
  @Override
  public int execute(final String... args) {

    final ShellState state = new ShellState();
    try {
      requireNonNull(args, "No arguments provided");

      System.out.printf("%s%n%n", Version.about());
      System.out.printf(
          """
          >  The following may contain credentials or sensitive information.
          >  Review before sharing.
          %n
          """);

      state.setDeferCatalogLoad(true);
      final StateFactory stateFactory = new StateFactory(state);

      final ConnectionTestCommands commands = new ConnectionTestCommands();
      final CommandLine commandLine = newCommandLine(commands, stateFactory);
      commandLine.parseArgs(args);

      CommandLineUtility.printEnvironment(state);

      Throwable throwable = null;
      try {
        executeCommandLine(commandLine);
      } catch (final Exception | ServiceConfigurationError | LinkageError e) {
        // Catch errors for missing third-party jars;
        // other errors (e.g. OutOfMemoryError) are intentionally not caught here
        throwable = e;
      }
      final boolean connectionSucceeded = throwable == null;
      if (connectionSucceeded) {
        System.out.printf("%n");
        System.out.println(CommandLineUtility.getConnectionInfo(state));
        System.out.println(getConnectionUrl(state));
      } else {
        System.out.println("  " + throwable.getMessage());
        LOGGER.log(Level.CONFIG, throwable.getMessage(), throwable);
      }
      System.out.flush();

      return connectionSucceeded ? 0 : 1;
    } finally {
      state.close();
    }
  }

  private void executeCommandLine(final CommandLine commandLine) {
    final Map<String, Object> subcommands = commandLine.getMixins();
    final Runnable command = (Runnable) subcommands.get("connect");
    LOGGER.log(Level.INFO, "Running command " + command.getClass().getSimpleName());
    command.run();
  }

  private String getConnectionUrl(final ShellState state) {
    requireNonNull(state, "No shell state provided");
    if (!state.isConnected()) {
      return "";
    }
    final StringWriter stringWriter = new StringWriter();
    try (final PrintWriter writer = new PrintWriter(stringWriter);
        final Connection connection = state.getConnectionSource().get()) {
      final String url = JdbcConnectionUtility.getConnectionUrl(connection);
      if (isBlank(url)) {
        throw new InternalRuntimeException("Could not obtain database connection URL");
      }
      writer.printf("  JDBC URL: %s", url);

      final DatabaseMetaData metaData = connection.getMetaData();
      final String userName = metaData.getUserName();
      if (!isBlank(userName)) {
        writer.printf("%n  Database user name:  %s", userName);
      }
      writer.flush();
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
    return stringWriter.toString();
  }
}
