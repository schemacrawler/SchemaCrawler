/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.shell;

import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.command.AvailableCatalogLoaders;

@Command(
    name = "loaders",
    header = "** List available SchemaCrawler catalog loaders",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"loaders"},
    optionListHeading = "Options:%n")
public class AvailableCatalogLoadersCommand implements Runnable {

  @Override
  public void run() {
    final AvailableCatalogLoaders availableCatalogLoaders = new AvailableCatalogLoaders();
    if (!availableCatalogLoaders.isEmpty()) {
      availableCatalogLoaders.printHelp(System.out);

      System.out.println("Notes:");
      System.out.println("- For help on an individual catalog loader,");
      System.out.println(
          "  run SchemaCrawler with options like: `-h loader:weakassociationsloader`");
      System.out.println(
          "  or, from the SchemaCrawler interactive shell: `help loader:weakassociationsloader`");
      System.out.println(
          "- Options for the catalog loaders should be provided with the `load` command");
    }
  }
}
