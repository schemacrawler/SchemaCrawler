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

  public DatabaseServerTypeParser(final Config config)
  {
    super(config);
  }

  @Override
  public DatabaseConnector getOptions()
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();

    final String serverType = config.getStringValue("server", null);
    if (config.hasValue("server")
        && !registry.hasDatabaseSystemIdentifier(serverType))
    {
      throw new SchemaCrawlerCommandLineException("Unsupported server, "
                                                  + serverType);
    }

    final DatabaseConnector dbConnector = registry
      .lookupDatabaseConnector(serverType);
    return dbConnector;
  }

}
