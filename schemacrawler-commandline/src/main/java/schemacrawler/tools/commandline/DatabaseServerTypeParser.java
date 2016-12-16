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


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class DatabaseServerTypeParser
  extends BaseOptionsParser<DatabaseConnector>
{

  private static final String URL = "url";
  private static final String SERVER = "server";

  public DatabaseServerTypeParser(final Config config)
  {
    super(config);
  }

  @Override
  public DatabaseConnector getOptions()
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();

    final String serverType = config.getStringValue(SERVER, null);
    if (config.hasValue(SERVER)
        && !registry.hasDatabaseSystemIdentifier(serverType))
    {
      throw new SchemaCrawlerCommandLineException(String.format(
                                                                "Unsupported server, %s %n"
                                                                + "Please provide a database connection URL on the command-line, %n"
                                                                + "and re-run SchemaCrawler without the -server argument",
                                                                serverType));
    }

    final DatabaseConnector dbConnector;
    if (serverType != null)
    {
      dbConnector = registry.lookupDatabaseConnector(serverType);
      consumeOption(SERVER);
    }
    else
    {
      final String connectionUrl = config.getStringValue(URL, null);
      dbConnector = registry.lookupDatabaseConnectorFromUrl(connectionUrl);
      // NOTE: Do not consume URL option, since it is needed later
    }

    return dbConnector;
  }

}
