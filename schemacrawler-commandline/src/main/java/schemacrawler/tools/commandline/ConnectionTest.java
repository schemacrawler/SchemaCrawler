/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import picocli.CommandLine;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import us.fatehi.utility.UtilityLogger;

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

      state.setDeferCatalogLoad(true);
      final StateFactory stateFactory = new StateFactory(state);

      final ConnectionTestCommands commands = new ConnectionTestCommands();
      final CommandLine commandLine = newCommandLine(commands, stateFactory);
      commandLine.parseArgs(args);

      executeCommandLine(commandLine);

      return 0;
    } catch (final Throwable throwable) {
      final UtilityLogger logger = new UtilityLogger(LOGGER);
      logger.logSafeArguments(args);
      logger.logFatalStackTrace(throwable);

      return 1;
    } finally {
      state.close();
    }
  }

  private static void executeCommandLine(final CommandLine commandLine) {
    final Map<String, Object> subcommands = commandLine.getMixins();

    for (final String commandName : new String[] {"system", "connect"}) {
      final Runnable command = (Runnable) subcommands.get(commandName);
      LOGGER.log(Level.INFO, "Running command " + command.getClass().getSimpleName());
      command.run();
    }
  }

  private ConnectionTest() {
    // Prevent instantiation
  }
}
