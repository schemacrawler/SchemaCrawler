/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.shell;

import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;

@Command(
    name = "sweep",
    aliases = {"clean"},
    header = "** Disconnect from a database, and clear loaded catalog",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"sweep"},
    optionListHeading = "Options:%n")
public class SweepCommand extends BaseStateHolder implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(SweepCommand.class.getName());

  public SweepCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {
    LOGGER.log(Level.INFO, "sweep");
    state.sweep();
  }
}
