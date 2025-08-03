/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.shell;

import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.command.AvailableCommands;

@Command(
    name = "commands",
    header = "** List available SchemaCrawler commands",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"commands"},
    optionListHeading = "Options:%n")
public class AvailableCommandsCommand implements Runnable {

  @Override
  public void run() {
    final AvailableCommands availableCommands = new AvailableCommands();
    if (!availableCommands.isEmpty()) {
      availableCommands.printHelp(System.out);

      System.out.println("Notes:");
      System.out.println("- For help on an individual SchemaCrawler command,");
      System.out.println("  run SchemaCrawler with options like: `-h command:schema`");
      System.out.println("  or from the SchemaCrawler interactive shell: `help command:schema`");
      System.out.println(
          "- Options for the commands should be provided with the `execute` command");
    }
  }
}
