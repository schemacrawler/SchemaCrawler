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
package schemacrawler.server.postgresql;


import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.isFileReadable;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.ConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConfigConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.UserCredentials;
import schemacrawler.tools.iosource.ClasspathInputResource;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public final class PostgreSQLDatabaseConnector
  extends DatabaseConnector
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(PostgreSQLDatabaseConnector.class.getName());

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

    readEnv(config);

    final Path databaseDumpFile = getDatabaseDumpFile(config);

    final ConnectionOptions connectionOptions;
    if (databaseDumpFile != null)
    {
      try
      {
        final EmbeddedPostgreSQLWrapper postgreSQLDumpLoader = new EmbeddedPostgreSQLWrapper();
        postgreSQLDumpLoader.startServer();
        postgreSQLDumpLoader.loadDatabaseFile(databaseDumpFile);
        connectionOptions = postgreSQLDumpLoader.createConnectionOptions();
      }
      catch (final IOException e)
      {
        throw new SchemaCrawlerException("Could not load database file, "
                                         + databaseDumpFile,
                                         e);
      }
    }
    else if (getDatabaseServerType().isUnknownDatabaseSystem()
             || config.hasValue("url"))
    {
      connectionOptions = new DatabaseConnectionOptions(userCredentials,
                                                        config);
    }
    else
    {
      connectionOptions = new DatabaseConfigConnectionOptions(userCredentials,
                                                              config);
    }

    return connectionOptions;
  }

  private Path getDatabaseDumpFile(final Config config)
  {
    final String database = config.get("database");
    final Path databaseFile;
    if (!isBlank(database))
    {
      if (isFileReadable(Paths.get(database)))
      {
        databaseFile = Paths.get(database);
      }
      else
      {
        databaseFile = null;
      }
    }
    else
    {
      databaseFile = null;
    }
    return databaseFile;
  }

  private void readEnv(final Config config)
  {
    try
    {
      final Map<String, String> env = System.getenv();

      final String host;
      if (env.containsKey("PGHOSTADDR"))
      {
        host = env.get("PGHOSTADDR");
      }
      else if (env.containsKey("PGHOST"))
      {
        host = env.get("PGHOST");
      }
      else
      {
        host = null;
      }
      if (!isBlank(host))
      {
        LOGGER.log(Level.INFO,
                   new StringFormat("Read PGHOSTADDR/PGHOST=%s", host));
        config.put("host", host);
      }

      final String port;
      if (env.containsKey("PGPORT"))
      {
        port = env.get("PGPORT");
      }
      else
      {
        port = null;
      }
      if (!isBlank(port) && port.chars().allMatch(Character::isDigit))
      {
        LOGGER.log(Level.INFO, new StringFormat("Read PGPORT=%s", port));
        config.put("port", port);
      }

      final String database;
      if (env.containsKey("PGDATABASE"))
      {
        database = env.get("PGDATABASE");
      }
      else
      {
        database = null;
      }
      if (!isBlank(database))
      {
        LOGGER.log(Level.INFO,
                   new StringFormat("Read PGDATABASE=%s", database));
        config.put("database", database);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.INFO, "Could not read environmental variables");
    }
  }

}
