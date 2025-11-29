/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
    logger.log(Level.INFO, CommandLineUtility.getConnectionInfo(state));
  }
}
