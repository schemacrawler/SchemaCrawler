/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.shell;

import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.command.AvailableERModelLoaders;

@Command(
    name = "ermodelloaders",
    header = "** List available ER model loaders",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"ermodelloaders"},
    optionListHeading = "Options:%n")
public class AvailableERModelLoadersCommand implements Runnable {

  @Override
  public void run() {
    final AvailableERModelLoaders availableLoaders = new AvailableERModelLoaders();
    if (!availableLoaders.isEmpty()) {
      availableLoaders.printHelp(System.out);

      System.out.println("Notes:");
      System.out.println("- For help on an individual ER model loader,");
      System.out.println(
          "  run SchemaCrawler with options like: `-h loader:implicitassociationsloader`");
      System.out.println(
          "- Options for the ER model loaders should be provided with the `load` command");
    }
  }
}
