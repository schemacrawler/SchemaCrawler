/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.catalogLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.commandPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.erModelLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.printCommandLineErrorMessage;
import static us.fatehi.utility.Utility.isBlank;

import java.io.IOException;
import java.util.logging.Logger;
import org.jline.builtins.PosixCommandGroup;
import org.jline.picocli.PicocliCommandRegistry;
import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.jline.reader.impl.DefaultParser;
import org.jline.shell.Shell;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import picocli.CommandLine.PicocliException;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.commandline.utility.CommandLineLogger;
import us.fatehi.utility.logging.UtilityLogger;

/** Runs the SchemaCrawler interactive shell. */
public final class SchemaCrawlerShell {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerShell.class.getName());

  /**
   * Executes an interactive shell session.
   *
   * @param args command-line arguments, currently ignored by the interactive shell
   * @return 0 when the shell exits normally, or 1 when a fatal error occurs
   */
  public static int execute(final String[] args) {
    // NOTE: Ignore arguments in the shell
    final ShellState state = new ShellState();
    try (final Terminal terminal = TerminalBuilder.builder().build();
        final Shell shell = newShell(terminal, new StateFactory(state))) {
      shell.run();
      return 0;
    } catch (final Throwable throwable) {
      handleFatalError(throwable, state);
      return 1;
    } finally {
      state.close();
    }
  }

  static Shell newShell(final Terminal terminal, final IFactory factory) throws IOException {
    requireNonNull(terminal, "No terminal provided");
    final CommandLine commandLine = newShellCommandLine(factory);
    final PicocliCommandRegistry picocliCommands = new PicocliCommandRegistry(commandLine);
    final Parser parser = new DefaultParser().escapeChars(new char[0]);

    return Shell.builder()
        .terminal(terminal)
        .prompt("schemacrawler> ")
        .parser(parser)
        .variable(LineReader.LIST_MAX, 3)
        .groups(picocliCommands, new PosixCommandGroup())
        .exitMessage("Terminating SchemaCrawler shell")
        .helpCommands(false)
        .onReaderReady((reader, dispatcher) -> picocliCommands.setLineReader(reader))
        .build();
  }

  private static void handleFatalError(final Throwable throwable, final ShellState state) {
    final UtilityLogger logger = new UtilityLogger(LOGGER);
    logger.logFatalStackTrace(throwable);
    final CommandLineLogger commandLineLogger = new CommandLineLogger(LOGGER);
    commandLineLogger.logState(state);

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

    printCommandLineErrorMessage(errorMessage, state);
  }

  private static CommandLine newShellCommandLine(final IFactory factory) {
    final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
    final CommandLine commandLine = newCommandLine(commands, factory);
    final CommandLine loadCommandLine = commandLine.getSubcommands().getOrDefault("load", null);
    if (loadCommandLine != null) {
      addPluginCommands(loadCommandLine, catalogLoaderPluginCommands);
      addPluginCommands(loadCommandLine, erModelLoaderPluginCommands);
      commandLine.addSubcommand(loadCommandLine);
    }
    final CommandLine executeCommandLine =
        commandLine.getSubcommands().getOrDefault("execute", null);
    if (executeCommandLine != null) {
      addPluginCommands(executeCommandLine, commandPluginCommands);
      commandLine.addSubcommand(executeCommandLine);
    }
    commandLine.setExecutionExceptionHandler(
        (ex, cmdLine, parseResult) -> {
          if (ex != null && ex.getMessage() != null) {
            cmdLine.getErr().printf("ERROR: %s%n", ex.getMessage());
          }
          return 0;
        });
    return commandLine;
  }

  private SchemaCrawlerShell() {
    // Prevent instantiation
  }
}
