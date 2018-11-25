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


import static java.util.Objects.requireNonNull;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.V10_6;
import static ru.yandex.qatools.embed.postgresql.util.SocketUtil.findFreePort;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.distribution.Version;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.schemacrawler.UserCredentials;
import schemacrawler.tools.integration.embeddeddb.EmbeddedDatabaseWrapper;
import sf.util.SchemaCrawlerLogger;

public class EmbeddedPostgreSQLWrapper
  extends EmbeddedDatabaseWrapper
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(EmbeddedPostgreSQLWrapper.class.getName());

  private Path databaseFile;
  private EmbeddedPostgres postgreSQL;
  private final Thread hook;
  private final Version postgreSQLVersion;

  public EmbeddedPostgreSQLWrapper()
  {
    this(V10_6);
  }

  public EmbeddedPostgreSQLWrapper(final Version postgreSQLVersion)
  {

    this.postgreSQLVersion = requireNonNull(postgreSQLVersion,
                                            "No PostgreSQL version provided");

    hook = new Thread(() -> {
      try
      {
        stopServer();
      }
      catch (final SchemaCrawlerException e)
      {
        e.printStackTrace(System.err);
      }
    });
  }

  @Override
  public ConnectionOptions createConnectionOptions()
    throws SchemaCrawlerException
  {
    try
    {
      requireNonNull(postgreSQL, "Database server not started");

      final Config config = new Config();
      config.put("url", getConnectionUrl());

      final UserCredentials userCredentials = new SingleUseUserCredentials(getUser(),
                                                                           getPassword());
      final ConnectionOptions connectionOptions = new DatabaseConnectionOptions(userCredentials,
                                                                                config);
      return connectionOptions;
    }
    catch (final Throwable e)
    {
      throw new SchemaCrawlerException("Cannot read file, " + databaseFile, e);
    }
  }

  public void exportToFile(final Path dumpFile)
  {
    requireNonNull(postgreSQL, "Database server not started");
    postgreSQL.getProcess()
      .orElseThrow(() -> new RuntimeException("Cannot obtain PostgreSQL process"))
      .exportToFile(dumpFile.toFile());
  }

  @Override
  public String getConnectionUrl()
  {
    requireNonNull(postgreSQL, "Database server not started");
    final String connectionUrl = postgreSQL.getConnectionUrl()
      .orElseThrow(() -> new RuntimeException("Cannot obtain PostgreSQL connection URL"));
    return connectionUrl;
  }

  @Override
  public void loadDatabaseFile(final Path dbFile)
    throws IOException
  {
    requireNonNull(postgreSQL, "Database server not started");

    databaseFile = checkDatabaseFile(dbFile);

    postgreSQL.getProcess()
      .orElseThrow(() -> new RuntimeException("Cannot obtain PostgreSQL process"))
      .importFromFile(dbFile.toFile());
  }

  @Override
  public void startServer()
    throws SchemaCrawlerException
  {
    try
    {
      final String homeDirectory = System.getProperty("user.home");
      final Path cachedPostgreSQL = Paths.get(homeDirectory, ".embedpostgresql")
        .toAbsolutePath();
      cachedPostgreSQL.toFile().mkdirs();

      final IRuntimeConfig runtimeConfig = EmbeddedPostgres
        .cachedRuntimeConfig(cachedPostgreSQL);

      postgreSQL = new EmbeddedPostgres(postgreSQLVersion);
      postgreSQL.start(runtimeConfig,
                       "localhost",
                       findFreePort(),
                       getDatabase(),
                       getUser(),
                       getPassword(),
                       Arrays.asList("-E", "'UTF-8'"));

      Runtime.getRuntime().addShutdownHook(hook);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not start PostgreSQL server", e);
    }
  }

  @Override
  public void stopServer()
    throws SchemaCrawlerException
  {
    if (postgreSQL != null)
    {
      LOGGER.log(Level.FINE, "Stopping PostgreSQL server");
      postgreSQL.stop();
      postgreSQL = null;
    }
  }

  @Override
  public String getPassword()
  {
    return "schemacrawler";
  }

  @Override
  public String getUser()
  {
    return "schemacrawler";
  }

  @Override
  public String getDatabase()
  {
    return "schemacrawler";
  }

}
