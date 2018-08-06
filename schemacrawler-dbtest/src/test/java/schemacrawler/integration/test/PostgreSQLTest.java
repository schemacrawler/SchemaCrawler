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


import static ru.yandex.qatools.embed.postgresql.distribution.Version.V10_3;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.util.SocketUtil;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class PostgreSQLTest
  extends BaseAdditionalDatabaseTest
{

  private boolean isDatabaseRunning;
  private EmbeddedPostgres postgres;

  @Before
  public void createDatabase()
    throws SchemaCrawlerException, SQLException, IOException
  {
    try
    {
      postgres = new EmbeddedPostgres(V10_3);
      final String url = postgres.start("localhost",
                                        SocketUtil.findFreePort(),
                                        "schemacrawler",
                                        "schemacrawler",
                                        "schemacrawler");
      System.out.println("PostgreSQL connection URL: " + url);

      dropDatabase(url, "/db/books/00_drop_schemas_01_E.sql");
      createDatabase(url,
                     "schemacrawler",
                     "schemacrawler",
                     "/postgresql.scripts.txt");

      isDatabaseRunning = true;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      // Do not run if database server cannot be loaded
      isDatabaseRunning = false;
    }
  }

  @After
  public void stopDatabaseServer()
  {
    if (isDatabaseRunning)
    {
      postgres.stop();
    }
  }

  @Test
  public void testPostgreSQLWithConnection()
    throws Exception
  {
    if (!isDatabaseRunning)
    {
      System.out.println("Did not run PostgreSQL test");
      return;
    }

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder
      .withMaximumSchemaInfoLevel();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noInfo().portableNames();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());

    executeExecutable(executable, "text", "testPostgreSQLWithConnection.txt");
  }

}
