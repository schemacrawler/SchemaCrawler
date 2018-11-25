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


import static org.junit.Assume.assumeTrue;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.server.postgresql.EmbeddedPostgreSQLWrapper;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class PostgreSQLTest
  extends BasePostgreSQLTest
{

  @BeforeClass
  public static void checkRun()
  {
    final String property = System.getProperty("complete");
    final boolean runIf = property != null && isBlank(property)
                          || Boolean.parseBoolean(property);
    assumeTrue(runIf);
  }

  private boolean isDatabaseRunning;
  private EmbeddedPostgreSQLWrapper embeddedPostgreSQL;

  @Before
  public void createDatabase()
    throws SchemaCrawlerException, SQLException, IOException
  {
    try
    {
      embeddedPostgreSQL = new EmbeddedPostgreSQLWrapper(getEmbeddedPostgreSQLVersion());
      embeddedPostgreSQL.startServer();
      createDataSource(embeddedPostgreSQL.getConnectionUrl(),
                       embeddedPostgreSQL.getUser(),
                       embeddedPostgreSQL.getPassword());
      createDatabase("/postgresql.scripts.txt");

      isDatabaseRunning = true;
    }
    catch (final Throwable e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
      // Do not run if database server cannot be loaded
      isDatabaseRunning = false;
    }
  }

  @After
  public void stopDatabaseServer()
    throws SchemaCrawlerException
  {
    if (isDatabaseRunning)
    {
      embeddedPostgreSQL.stopServer();
    }
  }

  @Test
  public void testPostgreSQLWithConnection()
    throws Exception
  {
    if (!isDatabaseRunning)
    {
      LOGGER.log(Level.INFO, "Did NOT run PostgreSQL test");
      return;
    }

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder();
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule("books"))
      .includeAllSequences().includeAllSynonyms().includeAllRoutines();
    final SchemaCrawlerOptions options = schemaCrawlerOptionsBuilder
      .toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());

    executeExecutable(executable, "testPostgreSQLWithConnection.txt");
    LOGGER.log(Level.INFO, "Completed PostgreSQL test successfully");
  }

}
