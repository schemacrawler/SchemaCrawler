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
package schemacrawler.integration.test;


import static java.nio.file.Files.createTempFile;
import static org.junit.Assume.assumeTrue;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.util.SocketUtil;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.server.postgresql.SchemaCrawlerPostgreSQLUtility;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class PostgreSQLDumpTest
  extends BaseAdditionalDatabaseTest
{

  @BeforeClass
  public static void checkRun()
  {
    final String property = System.getProperty("complete");
    final boolean runIf = property != null && isBlank(property)
                          || Boolean.parseBoolean(property);
    assumeTrue(runIf);
  }

  private Path dumpFile;

  @Before
  public void createDatabaseDump()
    throws SchemaCrawlerException, SQLException, IOException
  {
    dumpFile = createTempFile("test_postgres_dump", "sql");

    final EmbeddedPostgres postgres = createDatabase();

    postgres.getProcess()
      .orElseThrow(() -> new RuntimeException("Cannot obtain PostgreSQL process"))
      .exportToFile(dumpFile.toFile());

    postgres.stop();
  }

  @Test
  public void testPostgreSQLWithDump()
    throws Exception
  {
    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder
      .withMaximumSchemaInfoLevel();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.portableNames();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());
    executable.setConnection(SchemaCrawlerPostgreSQLUtility
      .createDatabaseConnection(dumpFile));

    executeExecutable(executable, "testPostgreSQLWithDump.txt");
    LOGGER.log(Level.INFO, "Completed PostgreSQL test successfully");
  }

  private EmbeddedPostgres createDatabase()
    throws SchemaCrawlerException, SQLException, IOException
  {
    final String user = "schemacrawler";
    final String password = "schemacrawler";

    final String homeDirectory = System.getProperty("user.home");
    final Path cachedPostgreSQL = Paths.get(homeDirectory, ".embedpostgresql")
      .toAbsolutePath();
    cachedPostgreSQL.toFile().mkdirs();

    final IRuntimeConfig runtimeConfig = EmbeddedPostgres
      .cachedRuntimeConfig(cachedPostgreSQL);

    final EmbeddedPostgres postgres = new EmbeddedPostgres();
    postgres.start(runtimeConfig,
                   "localhost",
                   SocketUtil.findFreePort(),
                   "schemacrawler",
                   user,
                   password,
                   Arrays.asList("-E", "'UTF-8'"));
    Runtime.getRuntime().addShutdownHook(new Thread(() -> postgres.stop()));

    final String connectionUrl = postgres.getConnectionUrl()
      .orElseThrow(() -> new RuntimeException());
    createDataSource(connectionUrl, user, password);
    createDatabase("/postgresql.scripts.txt");

    return postgres;
  }

}
