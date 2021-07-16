/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.tools.commandline.utility.CommandLineLoggingUtility.logFatalStackTrace;
import static schemacrawler.tools.commandline.utility.CommandLineLoggingUtility.logSafeArguments;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.catalogLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.commandPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.printCommandLineErrorMessage;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Map;
import java.util.logging.Level;

import picocli.CommandLine;
import java.util.logging.Logger;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;

public final class SchemaCrawlerCommandLine {

  private static final Logger LOGGER =
      Logger.getLogger(SchemaCrawlerCommandLine.class.getName());

  public static void execute(final String[] args) {

    try {
      requireNonNull(args, "No arguments provided");

      final ShellState state = new ShellState();
      final StateFactory stateFactory = new StateFactory(state);

      final SchemaCrawlerCommandLineCommands commands = new SchemaCrawlerCommandLineCommands();
      final CommandLine commandLine = newCommandLine(commands, stateFactory);
      addPluginCommands(commandLine, catalogLoaderPluginCommands);
      addPluginCommands(commandLine, commandPluginCommands);
      commandLine.parseArgs(args);

      executeCommandLine(commandLine);
    } catch (final Throwable throwable) {
      logSafeArguments(args);
      logFatalStackTrace(throwable);

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

      printCommandLineErrorMessage(errorMessage);

      System.exit(1);
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

  private SchemaCrawlerCommandLine() {
    // Prevent instantiation
  }
}
