/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import java.util.logging.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import us.fatehi.utility.LoggingConfig;

@Command(
    name = "log",
    header = "** Turn logging on or off",
    description = {""},
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"log"},
    optionListHeading = "Options:%n")
public final class LogCommand implements Runnable {

  @Option(
      names = {"--log-level"},
      description = {
        "Set log level using one of ${COMPLETION-CANDIDATES}",
        "Optional, defaults to OFF"
      })
  private LogLevel loglevel;

  public LogLevel getLogLevel() {
    if (loglevel == null) {
      loglevel = LogLevel.OFF;
    }
    return loglevel;
  }

  @Override
  public void run() {
    final Level level = getLogLevel().getLevel();
    new LoggingConfig(level);
  }
}
