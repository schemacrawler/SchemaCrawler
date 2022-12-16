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

import java.util.Collection;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Column;
import picocli.CommandLine.Help.TextTable;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.CommandRegistry;

@Command(
    name = "commands",
    header = "** List available SchemaCrawler commands",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"commands"},
    optionListHeading = "Options:%n")
public class AvailableCommandsCommand implements Runnable {

  private static String availableCommandsDescriptive() {
    final CommandLine.Help.ColorScheme.Builder colorSchemaBuilder =
        new CommandLine.Help.ColorScheme.Builder();
    colorSchemaBuilder.ansi(CommandLine.Help.Ansi.OFF);
    final TextTable textTable =
        forColumns(colorSchemaBuilder.build(), new Column(15, 1, SPAN), new Column(65, 1, WRAP));
    final Collection<CommandDescription> commandDescriptions =
        CommandRegistry.getCommandRegistry().getSupportedCommands();
    commandDescriptions.add(
        new CommandDescription(
            "<query_name>",
            "Shows results of query <query_name>, "
                + "as specified in the configuration properties file"));
    commandDescriptions.add(
        new CommandDescription(
            "<query>",
            String.join(
                "\n",
                "Shows results of SQL <query>",
                "The query itself can contain the variables ${table}, ${columns} "
                    + "and ${tabletype}, or system properties referenced as ${<system-property-name>}",
                "Queries without any variables are executed exactly once",
                "Queries with variables are executed once for each table, "
                    + "with the variables substituted")));

    for (final CommandDescription commandDescription : commandDescriptions) {
      textTable.addRowValues(commandDescription.getName(), commandDescription.getDescription());
    }
    return textTable.toString();
  }

  @Override
  public void run() {
    final String availableCommands = availableCommandsDescriptive();
    if (!isBlank(availableCommands)) {
      System.out.println();
      System.out.println("Available SchemaCrawler commands:");
      System.out.println(availableCommands);

      System.out.println("Notes:");
      System.out.println("- For help on an individual SchemaCrawler command,");
      System.out.println("  run SchemaCrawler with options like: `-h command:schema`");
      System.out.println("  or from the SchemaCrawler interactive shell: `help command:schema`");
      System.out.println(
          "- Options for the commands should be provided with the `execute` command");
    }
  }
}
