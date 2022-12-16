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

package schemacrawler.tools.commandline.shell;

import static picocli.CommandLine.Help.Column.Overflow.SPAN;
import static picocli.CommandLine.Help.Column.Overflow.WRAP;
import static picocli.CommandLine.Help.TextTable.forColumns;
import static us.fatehi.utility.Utility.isBlank;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Column;
import picocli.CommandLine.Help.TextTable;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

@Command(
    name = "servers",
    header = "** List available SchemaCrawler database plugins",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"servers"},
    optionListHeading = "Options:%n")
public class AvailableServersCommand implements Runnable {

  private static String availableServersDescriptive() {
    final CommandLine.Help.ColorScheme.Builder colorSchemaBuilder =
        new CommandLine.Help.ColorScheme.Builder();
    colorSchemaBuilder.ansi(CommandLine.Help.Ansi.OFF);
    final TextTable textTable =
        forColumns(colorSchemaBuilder.build(), new Column(15, 1, SPAN), new Column(65, 1, WRAP));

    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    for (final DatabaseServerType serverType : databaseConnectorRegistry) {
      textTable.addRowValues(
          serverType.getDatabaseSystemIdentifier(), serverType.getDatabaseSystemName());
    }
    return textTable.toString();
  }

  @Override
  public void run() {
    final String availableServers = availableServersDescriptive();
    if (!isBlank(availableServers)) {
      System.out.println();
      System.out.println("Available SchemaCrawler database server plugins:");
      System.out.println(availableServers);

      System.out.println("Notes:");
      System.out.println("- For help on an individual database plugin,");
      System.out.println("  run SchemaCrawler with options like: `-h server:mysql`");
      System.out.println("  or, from the SchemaCrawler interactive shell: `help server:mysql`");
      System.out.println(
          "- Options for the server plugins should be provided with the `connect` command");
    }
  }
}
