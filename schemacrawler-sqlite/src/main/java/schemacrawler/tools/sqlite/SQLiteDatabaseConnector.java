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
package schemacrawler.tools.sqlite;


import java.util.regex.Pattern;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.UserCredentials;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseServerType;

public final class SQLiteDatabaseConnector
  extends DatabaseConnector
{

  private static final long serialVersionUID = -926915070636247650L;

  public SQLiteDatabaseConnector()
  {
    super(new DatabaseServerType("sqlite", "SQLite"),
          "/help/Connections.sqlite.txt",
          "/schemacrawler-sqlite.config.properties",
          "/sqlite.information_schema",
          url -> Pattern.matches("jdbc:sqlite:.*", url));
  }

  @Override
  public DatabaseSpecificOverrideOptionsBuilder getDatabaseSpecificOverrideOptionsBuilder()
  {
    final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = super.getDatabaseSpecificOverrideOptionsBuilder();
    databaseSpecificOverrideOptionsBuilder.identifierQuoteString("\"");
    return databaseSpecificOverrideOptionsBuilder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConnectionOptions newDatabaseConnectionOptions(final UserCredentials userCredentials,
                                                        final Config additionalConfig)
    throws SchemaCrawlerException
  {
    try
    {
      Class.forName("org.sqlite.JDBC");
    }
    catch (final ClassNotFoundException e)
    {
      throw new SchemaCrawlerException("Could not load SQLite JDBC driver", e);
    }

    return super.newDatabaseConnectionOptions(userCredentials,
                                              additionalConfig);
  }

}
