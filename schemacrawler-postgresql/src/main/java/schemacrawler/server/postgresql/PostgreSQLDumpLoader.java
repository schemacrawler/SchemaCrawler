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
import static ru.yandex.qatools.embed.postgresql.util.SocketUtil.findFreePort;
import static sf.util.IOUtility.isFileReadable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.schemacrawler.UserCredentials;
import schemacrawler.tools.integration.dbdump.DatabaseDumpLoader;
import sf.util.SchemaCrawlerLogger;

public class PostgreSQLDumpLoader
  extends DatabaseDumpLoader
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(PostgreSQLDumpLoader.class.getName());

  public PostgreSQLDumpLoader(final Path databaseFile)
    throws IOException
  {
    super(databaseFile);
  }

  @Override
  public ConnectionOptions createConnectionOptions()
    throws SchemaCrawlerException
  {
    try
    {
      requireNonNull(databaseFile, "No database file provided");
      if (!isFileReadable(databaseFile))
      {
        throw new SchemaCrawlerException("Cannot read file, " + databaseFile);
      }

      final String user = "schemacrawler";
      final String password = "schemacrawler";

      final EmbeddedPostgres postgres = startEmbeddedPostgreSQLServer(user,
                                                                      password);
      loadDump(postgres, databaseFile);

      final Config config = new Config();
      config.put("url", getConnectionUrl(postgres));

      final UserCredentials userCredentials = new SingleUseUserCredentials(user,
                                                                           password);
      final ConnectionOptions connectionOptions = new DatabaseConnectionOptions(userCredentials,
                                                                                config);
      return connectionOptions;

    }
    catch (final Throwable e)
    {
      throw new SchemaCrawlerException("Cannot read file, " + databaseFile, e);
    }
  }

  private String getConnectionUrl(final EmbeddedPostgres postgres)
  {
    final String connectionUrl = postgres.getConnectionUrl()
      .orElseThrow(() -> new RuntimeException("Cannot obtain PostgreSQL connection URL"));
    return connectionUrl;
  }

  private void loadDump(final EmbeddedPostgres postgres, final Path dbFile)
  {
    postgres.getProcess()
      .orElseThrow(() -> new RuntimeException("Cannot obtain PostgreSQL process"))
      .importFromFile(dbFile.toFile());
  }

  private EmbeddedPostgres startEmbeddedPostgreSQLServer(final String user,
                                                         final String password)
    throws IOException
  {
    final String homeDirectory = System.getProperty("user.home");
    final Path cachedPostgreSQL = Paths.get(homeDirectory, ".embedpostgresql")
      .toAbsolutePath();
    cachedPostgreSQL.toFile().mkdirs();

    final IRuntimeConfig runtimeConfig = EmbeddedPostgres
      .cachedRuntimeConfig(cachedPostgreSQL);

    final EmbeddedPostgres postgres = new EmbeddedPostgres();
    postgres.start(runtimeConfig,
                   "localhost",
                   findFreePort(),
                   "schemacrawler",
                   user,
                   password,
                   Arrays.asList("-E", "'UTF-8'"));
    Runtime.getRuntime().addShutdownHook(new Thread(() -> postgres.stop()));
    return postgres;
  }

}
