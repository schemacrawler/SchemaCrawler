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

package schemacrawler.tools.commandline.utility;

import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.commandline.state.ShellState;

public final class CommandLineLogger {

  private final Logger logger;

  public CommandLineLogger(final Logger logger) {
    this.logger = requireNonNull(logger, "No logger provided");
  }

  public void logState(final ShellState state) {
    if (!logger.isLoggable(Level.INFO)) {
      return;
    }

    logger.log(Level.INFO, CommandLineUtility.getEnvironment(state));
  }
}
