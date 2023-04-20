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

package schemacrawler.tools.commandline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.catalogLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.commandPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.printCommandLineErrorMessage;
import static us.fatehi.utility.Utility.isBlank;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import picocli.shell.jline3.PicocliCommands;
import picocli.shell.jline3.PicocliCommands.PicocliCommandsFactory;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import schemacrawler.tools.commandline.utility.CommandLineLogger;
import us.fatehi.utility.UtilityLogger;

public final class SchemaCrawlerShell {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerShell.class.getName());

  public static void execute(final String[] args) {

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

      final Supplier<Path> workingDir = () -> Paths.get(".");
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
          } else if (line.equals("cls") || line.equals("clear")) {
            ((LineReaderImpl) reader).clearScreen();
          } else {
            systemRegistry.execute(line);
          }

        } catch (final UserInterruptException e) {
          // Ignore
        } catch (final EndOfFileException e) {
          return;
        } catch (final Exception e) {
          System.err.println("ERROR: " + e.getMessage());
          LOGGER.log(Level.WARNING, e.getMessage(), e);
          systemRegistry.trace(e);
        }
      }
    } catch (final Throwable throwable) {
      handleFatalError(args, throwable, state);
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
    if (throwable instanceof picocli.CommandLine.PicocliException) {
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

    System.exit(1);
  }

  private SchemaCrawlerShell() {
    // Prevent instantiation
  }
}
