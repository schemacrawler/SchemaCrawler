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
package schemacrawler.server.postgresql;


import static java.nio.file.Files.exists;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConfigConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.UserCredentials;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.iosource.ClasspathInputResource;

public final class PostgreSQLDatabaseConnector
  extends DatabaseConnector
{

  public PostgreSQLDatabaseConnector()
    throws IOException
  {
    super(new DatabaseServerType("postgresql", "PostgreSQL"),
          new ClasspathInputResource("/help/Connections.postgresql.txt"),
          new ClasspathInputResource("/schemacrawler-postgresql.config.properties"),
          (informationSchemaViewsBuilder,
           connection) -> informationSchemaViewsBuilder
             .fromResourceFolder("/postgresql.information_schema"),
          url -> Pattern.matches("jdbc:postgresql:.*", url));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConnectionOptions newDatabaseConnectionOptions(final UserCredentials userCredentials,
                                                        final Config additionalConfig)
    throws SchemaCrawlerException
  {
    requireNonNull(userCredentials,
                   "No database connection user credentials provided");

    final Config config = getConfig();
    if (additionalConfig != null)
    {
      config.putAll(additionalConfig);
      // Remove sensitive properties from the original configuration
      additionalConfig.remove("user");
      additionalConfig.remove("password");
    }

    final ConnectionOptions connectionOptions;
    if (getDatabaseServerType().isUnknownDatabaseSystem()
        || config.hasValue("url"))
    {
      connectionOptions = new DatabaseConnectionOptions(userCredentials,
                                                        config);
    }
    else
    {
      final String database = config.get("database");
      if (!isBlank(database) && exists(Paths.get(database)))
      {
        // Load PostgreSQL dump file, and connect to the local database
        // with that dump loaded
        connectionOptions = SchemaCrawlerPostgreSQLUtility
          .createConnectionOptions(Paths.get(database));
      }
      else
      {
        connectionOptions = new DatabaseConfigConnectionOptions(userCredentials,
                                                                config);
      }
    }

    return connectionOptions;
  }

}
