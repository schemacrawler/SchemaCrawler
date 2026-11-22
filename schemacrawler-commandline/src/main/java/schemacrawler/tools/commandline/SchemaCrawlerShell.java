/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.printCommandLineErrorMessage;
import static us.fatehi.utility.Utility.isBlank;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine.PicocliException;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.commandline.utility.CommandLineLogger;
import us.fatehi.utility.logging.UtilityLogger;

public final class SchemaCrawlerShell {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerShell.class.getName());

  public static int execute(final String[] args) {

    final ShellState state = new ShellState();
    try (final Terminal terminal = TerminalBuilder.builder().build()) {

      requireNonNull(args, "No arguments provided");

      final StateFactory stateFactory = new StateFactory(state);
      final ShellSession session = new ShellSession(terminal, stateFactory);

      while (true) {
        try {
          session.cleanUp();
          final String line = session.readLine("schemacrawler> ");
          session.execute(line);
        } catch (final UserInterruptException e) {
          // Ignore
        } catch (final EndOfFileException e) {
          throw e;
        } catch (final Exception e) {
          LOGGER.log(Level.WARNING, e.getMessage(), e);
          session.trace(e);
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
