/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.shell;

import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.command.AvailableServers;

@Command(
    name = "servers",
    header = "** List available SchemaCrawler database plugins",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"servers"},
    optionListHeading = "Options:%n")
public class AvailableServersCommand implements Runnable {

  @Override
  public void run() {
    final AvailableServers availableServers = new AvailableServers();
    if (!availableServers.isEmpty()) {
      availableServers.printHelp(System.out);

      System.out.println("Notes:");
      System.out.println("- For help on an individual database plugin,");
      System.out.println("  run SchemaCrawler with options like: `-h server:mysql`");
      System.out.println("  or, from the SchemaCrawler interactive shell: `help server:mysql`");
      System.out.println(
          "- Options for the server plugins should be provided with the `connect` command");
    }
  }
}
