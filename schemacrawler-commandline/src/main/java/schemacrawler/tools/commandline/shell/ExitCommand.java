/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.commandline.shell;

import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.CommandLine.Command;

@Command(
    name = "exit",
    aliases = {"quit", "terminate"},
    header = "** Terminate the interactive shell",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"exit"},
    optionListHeading = "Options:%n")
public class ExitCommand implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(ExitCommand.class.getName());

  @Override
  public void run() {
    LOGGER.log(Level.INFO, "exit");
    // No-op, since the shell will catch this command and exit with a status code of 0
    // This command is registered only for the help message
  }
}
