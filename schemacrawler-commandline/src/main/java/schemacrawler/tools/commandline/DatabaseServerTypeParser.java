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

package schemacrawler.tools.commandline;


import static sf.util.Utility.isBlank;
import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class DatabaseServerTypeParser
  implements OptionsParser<DatabaseConnector>
{

  private final CommandLine commandLine;

  @CommandLine.Option(names = {
    "-server", "--server" }, description = "Database server type")
  private String databaseSystemIdentifier = null;
  @CommandLine.Option(names = {
    "-url", "--url" }, description = "Database connection string")
  private String connectionUrl = null;

  @CommandLine.Parameters
  private String[] remainder = new String[0];

  public DatabaseServerTypeParser()
  {
    commandLine = newCommandLine(this);
  }

  @Override
  public DatabaseConnector parse(final String[] args)
    throws SchemaCrawlerException
  {
    commandLine.parse(args);

    if (!hasDatabaseSystemIdentifier() && isBlank(connectionUrl))
    {
      throw new SchemaCrawlerCommandLineException(
        "Please specify a database connection");
    }

    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();

    if (hasDatabaseSystemIdentifier() && !registry
      .hasDatabaseSystemIdentifier(databaseSystemIdentifier))
    {
      throw new SchemaCrawlerCommandLineException(String.format(
        "Unsupported server <%s> %n"
        + "Please provide a database connection URL on the command-line, %n"
        + "and re-run SchemaCrawler without the -server argument",
        databaseSystemIdentifier));
    }

    final DatabaseConnector dbConnector;
    if (hasDatabaseSystemIdentifier())
    {
      dbConnector = registry.lookupDatabaseConnector(databaseSystemIdentifier);
    }
    else
    {
      dbConnector = registry.lookupDatabaseConnectorFromUrl(connectionUrl);
    }

    return dbConnector;
  }

  private boolean hasDatabaseSystemIdentifier()
  {
    return !isBlank(databaseSystemIdentifier);
  }

  public boolean isBundled()
  {
    return hasDatabaseSystemIdentifier();
  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

}
