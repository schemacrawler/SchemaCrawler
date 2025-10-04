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

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.CommandLine.PicocliException;
import picocli.shell.jline3.PicocliCommands;
import picocli.shell.jline3.PicocliCommands.PicocliCommandsFactory;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.commandline.utility.CommandLineLogger;
import us.fatehi.utility.UtilityLogger;

public final class SchemaCrawlerShell {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerShell.class.getName());

  public static int execute(final String[] args) {

    final ShellState state = new ShellState();
    try (final Terminal terminal = TerminalBuilder.builder().build()) {

      requireNonNull(args, "No arguments provided");

      final StateFactory stateFactory = new StateFactory(state);
      final PicocliCommandsFactory factory = new PicocliCommandsFactory(stateFactory);

      final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
      final CommandLine commandLine = newCommandLine(commands, factory);
      final CommandLine loadCommandLine = commandLine.getSubcommands().getOrDefault("load", null);
      if (loadCommandLine != null) {
        addPluginCommands(loadCommandLine, catalogLoaderPluginCommands);
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

      final Supplier<Path> workingDir = () -> Path.of(".");
      final PicocliCommands picocliCommands = new PicocliCommands(commandLine);
      final Parser parser = new DefaultParser().escapeChars(new char[0]);

      final SystemRegistry systemRegistry =
          new SystemRegistryImpl(parser, terminal, workingDir, null);
      systemRegistry.setCommandRegistries(picocliCommands);
      systemRegistry.register("help", picocliCommands);

      final LineReader reader =
          LineReaderBuilder.builder()
              .terminal(terminal)
              .completer(systemRegistry.completer())
              .parser(parser)
              .variable(LineReader.LIST_MAX, 3) // max
              // tab
              // completion
              // candidates
              .build();
      factory.setTerminal(terminal);

      while (true) {
        try {
          systemRegistry.cleanUp();
          final String line =
              reader.readLine("schemacrawler> ", null, (MaskingCallback) null, null);
          if (line.startsWith("help")) {
            final ParsedLine pl = reader.getParser().parse(line, 0);
            final String[] arguments = pl.words().toArray(new String[0]);
            commandLine.execute(arguments);
          } else if ("cls".equals(line) || "clear".equals(line)) {
            ((LineReaderImpl) reader).clearScreen();
          } else if ("quit".equals(line) || "terminate".equals(line)) {
            throw new EndOfFileException(line);
          } else {
            systemRegistry.execute(line);
          }

        } catch (final UserInterruptException e) {
          // Ignore
        } catch (final EndOfFileException e) {
          throw e;
        } catch (final Exception e) {
          LOGGER.log(Level.WARNING, e.getMessage(), e);
          systemRegistry.trace(e);
        }
      }
    } catch (final EndOfFileException e) {
      // User-initiated exit
      return 0;
    } catch (final Throwable throwable) {
      // Exceptional exit
      handleFatalError(args, throwable, state);
      return 1;
    } finally {
      state.close();
    }
  }

  private static void handleFatalError(
      final String[] args, final Throwable throwable, final ShellState state) {

    final UtilityLogger logger = new UtilityLogger(LOGGER);
    logger.logSafeArguments(args);
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

  private SchemaCrawlerShell() {
    // Prevent instantiation
  }
}
