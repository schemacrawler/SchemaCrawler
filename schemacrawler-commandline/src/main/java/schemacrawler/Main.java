/*
 * ======================================================================== SchemaCrawler
 * http://www.schemacrawler.com Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>. All
 * rights reserved. ------------------------------------------------------------------------
 *
 * SchemaCrawler is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * SchemaCrawler and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0, GNU General Public License v3 or GNU Lesser General Public License v3.
 *
 * You may elect to redistribute this code under any of these licenses.
 *
 * The Eclipse Public License is available at: http://www.eclipse.org/legal/epl-v10.html
 *
 * The GNU General Public License v3 and the GNU Lesser General Public License v3 are available at:
 * http://www.gnu.org/licenses/
 *
 * ========================================================================
 */

package schemacrawler;

import static java.util.Objects.requireNonNull;
import static picocli.CommandLine.populateCommand;
import java.util.logging.Logger;
import picocli.CommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerShell;
import schemacrawler.tools.commandline.command.CommandLineHelpCommand;
import schemacrawler.tools.commandline.command.LogCommand;
import schemacrawler.tools.commandline.shell.InteractiveShellOptions;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;
import us.fatehi.utility.UtilityLogger;

/** Main class that takes arguments for a database for crawling a schema. */
public final class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public static void main(final String... args) throws Exception {
    requireNonNull(args, "No command-line arguments provided");

    final CommandLine commandLine = new CommandLine(new LogCommand());
    commandLine.setUnmatchedArgumentsAllowed(true);
    commandLine.execute(args);

    final UtilityLogger logger = new UtilityLogger(LOGGER);
    logger.logSafeArguments(args);
    logger.logSystemClasspath();
    logger.logSystemProperties();

    final InteractiveShellOptions interactiveShellOptions = new InteractiveShellOptions();
    populateCommand(interactiveShellOptions, args);

    final boolean isInteractive = interactiveShellOptions.isInteractive();
    if (isInteractive) {
      SchemaCrawlerShell.execute(args);
    } else {
      if (showHelpIfRequested(args)) {
        return;
      }
      if (showVersionIfRequested(args)) {
        return;
      }
      final int exitCode = SchemaCrawlerCommandLine.execute(args);
      if (exitCode != 0) {
        System.exit(exitCode);
      }
    }
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
