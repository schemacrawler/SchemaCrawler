/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.commandline;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;
import static sf.util.Utility.readResourceFully;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseServerType;
import schemacrawler.tools.executable.CommandRegistry;

/**
 * Utility for parsing the SchemaCrawler command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerHelpCommandLine
  implements CommandLine
{

  private static String loadHelpText(final String helpResource)
  {
    if (isBlank(helpResource)
        || SchemaCrawlerHelpCommandLine.class.getResource(helpResource) == null)
    {
      return "";
    }

    return readResourceFully(helpResource);
  }

  private final String command;
  private final boolean showVersionOnly;
  private final String connectionHelpResource;
  private final DatabaseServerType dbServerType;

  /**
   * Loads objects from command-line options. Optionally loads the
   * config from the classpath.
   *
   * @param args
   *        Command line arguments.
   * @param configResource
   *        Config resource.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerHelpCommandLine(final Config argsMap,
                                      final boolean showVersionOnly)
    throws SchemaCrawlerException
  {
    requireNonNull(argsMap, "No command-line arguments provided");

    final DatabaseServerTypeParser dbServerTypeParser = new DatabaseServerTypeParser(argsMap);
    final DatabaseConnector dbConnector = dbServerTypeParser.getOptions();

    connectionHelpResource = dbConnector.getConnectionHelpResource();
    dbServerType = dbConnector.getDatabaseServerType();

    this.showVersionOnly = showVersionOnly;

    String command = null;
    if (!argsMap.isEmpty())
    {
      final CommandParser parser = new CommandParser(argsMap);
      if (parser.hasOptions())
      {
        command = parser.getOptions().toString();
      }
      if (isBlank(command))
      {
        command = null;
      }
    }
    this.command = command;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.commandline.CommandLine#execute()
   */
  @Override
  public void execute()
    throws SchemaCrawlerException
  {
    final CommandRegistry commandRegistry = new CommandRegistry();

    if (dbServerType != null && !dbServerType.isUnknownDatabaseSystem())
    {
      System.out
        .println("SchemaCrawler for " + dbServerType.getDatabaseSystemName());
    }
    System.out.println(loadHelpText("/help/SchemaCrawler.txt"));
    System.out.println();
    if (showVersionOnly)
    {
      System.exit(0);
    }

    if (isBlank(connectionHelpResource))
    {
      final DatabaseConnectorRegistry databaseConnectorRegistry = new DatabaseConnectorRegistry();
      System.out.println(loadHelpText("/help/Connections.txt"));
      System.out.println("Available servers are: ");
      for (final String availableServer: databaseConnectorRegistry)
      {
        System.out.println("  " + availableServer);
      }
      System.out.println();
    }
    else
    {
      System.out.println(loadHelpText(connectionHelpResource));
    }
    System.out.println(loadHelpText("/help/SchemaCrawlerOptions.txt"));
    System.out.println(loadHelpText("/help/Config.txt"));
    System.out.println(loadHelpText("/help/ApplicationOptions.txt"));
    if (!commandRegistry.hasCommand(command))
    {
      System.out.println(loadHelpText("/help/Command.txt"));
      System.out.println("  Available commands are: ");
      for (final String availableCommand: commandRegistry)
      {
        System.out.println("    " + availableCommand);
      }
      System.out.println();
    }
    else
    {
      final String helpResource = commandRegistry.getHelpResource(command);
      System.out.println(loadHelpText(helpResource));

      final String helpAdditionalText = commandRegistry
        .getHelpAdditionalText(command);
      System.out.println(helpAdditionalText);
    }

    System.exit(0);
  }

  public final String getCommand()
  {
    return command;
  }

}
