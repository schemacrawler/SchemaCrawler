/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.logging.Level;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.iosource.InputResource;
import sf.util.IOUtility;
import sf.util.SchemaCrawlerLogger;

/**
 * Utility for parsing the SchemaCrawler command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerHelpCommandLine
  implements CommandLine
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerHelpCommandLine.class.getName());

  private final PrintWriter out;
  private final String command;
  private final boolean showVersionOnly;
  private final InputResource connectionHelpResource;
  private final DatabaseServerType dbServerType;

  /**
   * Shows help based on command-line options.
   *
   * @param argsMap
   *        Parsed command line arguments.
   * @param showVersionOnly
   *        Whether to show version message and exit.
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

    out = new PrintWriter(System.out);

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
   */
  @Override
  public void execute()
    throws SchemaCrawlerException
  {
    final CommandRegistry commandRegistry = new CommandRegistry();

    if (dbServerType != null && !dbServerType.isUnknownDatabaseSystem())
    {
      out.println("SchemaCrawler for " + dbServerType.getDatabaseSystemName());
    }
    printHelpText("/help/SchemaCrawler.txt");
    out.println();
    if (showVersionOnly)
    {
      return;
    }

    if (connectionHelpResource == null)
    {
      final DatabaseConnectorRegistry databaseConnectorRegistry = new DatabaseConnectorRegistry();
      printHelpText("/help/Connections.txt");
      out.println("Available servers are: ");
      for (final DatabaseServerType availableServer: databaseConnectorRegistry)
      {
        out.println("  " + availableServer);
      }
      out.println();
    }
    else
    {
      printHelpText(connectionHelpResource);
    }
    printHelpText("/help/SchemaCrawlerOptions.txt");
    printHelpText("/help/Config.txt");
    printHelpText("/help/ApplicationOptions.txt");

    if (!commandRegistry.isCommandSupported(command))
    {
      printHelpText("/help/Command.txt");
      out.println("  Available commands are: ");
      for (final CommandDescription availableCommand: commandRegistry)
      {
        out.println("    " + availableCommand);
      }
      out.println();
    }
    else
    {
      printHelpText(commandRegistry.getHelp(command));
    }

    out.flush();

  }

  public final String getCommand()
  {
    return command;
  }

  private void printHelpText(final InputResource helpResource)
  {
    if (helpResource == null)
    {
      return;
    }
    try (final Reader helpReader = helpResource.openNewInputReader(UTF_8);)
    {
      IOUtility.copy(helpReader, out);
      out.println();
      out.flush();
      // Do not close System.out
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING,
                 String.format("Could not print help from resource <%s>",
                               helpResource),
                 e);
    }
  }

  private void printHelpText(final String helpClaspathResource)
  {
    try
    {
      printHelpText(new ClasspathInputResource(helpClaspathResource));
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING,
                 String.format("Could not find help resource <%s>",
                               helpClaspathResource),
                 e);
    }
  }

}
