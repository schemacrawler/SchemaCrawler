/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.commandline;


import static sf.util.Utility.isBlank;
import static sf.util.Utility.readResourceFully;

import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseServerType;
import schemacrawler.tools.executable.CommandRegistry;
import us.fatehi.commandlineparser.CommandLineUtility;

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
   * @param dbServerType
   *        Database server type.
   * @param connectionHelpResource
   *        Help options.
   * @param configResource
   *        Config resource.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerHelpCommandLine(final String[] args,
                                      final DatabaseServerType dbServerType,
                                      final String connectionHelpResource,
                                      final boolean showVersionOnly)
                                        throws SchemaCrawlerException
  {
    requireNonNull(args, "No command-line arguments provided");

    final Config config = CommandLineUtility.loadConfig(args);

    this.connectionHelpResource = connectionHelpResource;
    this.showVersionOnly = showVersionOnly;

    String command = null;
    if (args.length != 0)
    {
      final CommandParser parser = new CommandParser(config);
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

    this.dbServerType = dbServerType;
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
