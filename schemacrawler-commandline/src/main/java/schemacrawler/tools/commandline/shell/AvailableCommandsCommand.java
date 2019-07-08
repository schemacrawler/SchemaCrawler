/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static sf.util.Utility.isBlank;

import java.util.Collection;

import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Help.Column;
import picocli.CommandLine.Help.TextTable;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.CommandRegistry;

@Command(name = "commands",
         header = "** Commands Options - List available SchemaCrawler commands")
public class AvailableCommandsCommand
  implements Runnable
{

  private static String availableCommandsDescriptive()
  {
    final TextTable textTable = forColumns(Ansi.OFF,
                                           new Column(15, 1, SPAN),
                                           new Column(65, 1, WRAP));
    try
    {
      final Collection<CommandDescription> commandDescriptions = CommandRegistry
        .getCommandRegistry()
        .getSupportedCommands();
      commandDescriptions.add(new CommandDescription("<query_name>",
                                                     "Shows results of query <query_name>, "
                                                     + "as specified in the configuration properties file"));
      commandDescriptions.add(new CommandDescription("<query>",
                                                     String.join("\n",
                                                                 "Shows results of SQL <query>",
                                                                 "The query itself can contain the variables ${table}, ${columns} "
                                                                 + "and ${tabletype}, or system properties referenced as ${<system-property-name>}",
                                                                 "Queries without any variables are executed exactly once",
                                                                 "Queries with variables are executed once for each table, "
                                                                 + "with the variables substituted")));

      for (final CommandDescription commandDescription : commandDescriptions)
      {
        textTable.addRowValues(commandDescription.getName(),
                               commandDescription.getDescription());
      }
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SchemaCrawlerRuntimeException(
        "Could not initialize command registry",
        e);
    }
    return textTable.toString();
  }

  @Override
  public void run()
  {
    final String availableCommands = availableCommandsDescriptive();
    if (!isBlank(availableCommands))
    {
      System.out.println();
      System.out.println("Available SchemaCrawler Commands:");
      System.out.println(availableCommands);
    }
  }

}
