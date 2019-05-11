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

import picocli.CommandLine;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

@CommandLine.Command(name = "servers",
                     header = "** Servers Options - List available SchemaCrawler database plugins")
public class AvailableServersCommand
  implements Runnable
{
  private static String availableServersDescriptive()
  {
    final CommandLine.Help.TextTable textTable = forColumns(CommandLine.Help.Ansi.OFF,
                                                            new CommandLine.Help.Column(
                                                              15,
                                                              1,
                                                              SPAN),
                                                            new CommandLine.Help.Column(
                                                              65,
                                                              1,
                                                              WRAP));
    try
    {
      for (final DatabaseServerType serverType : new DatabaseConnectorRegistry())
      {
        textTable.addRowValues(serverType.getDatabaseSystemIdentifier(),
                               serverType.getDatabaseSystemName());
      }
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SchemaCrawlerRuntimeException(
        "Could not initialize server registry",
        e);
    }
    return textTable.toString();
  }

  @Override
  public void run()
  {
    final String availableServers = availableServersDescriptive();
    if (!isBlank(availableServers))
    {
      System.out.println();
      System.out.println("Available Database Server Types:");
      System.out.println(availableServers);
    }
  }

}
