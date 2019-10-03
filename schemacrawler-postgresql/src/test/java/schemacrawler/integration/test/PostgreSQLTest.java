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
package schemacrawler.integration.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static sf.util.DatabaseUtility.checkConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Property;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.server.postgresql.PostgreSQLDatabaseConnector;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class PostgreSQLTest
  extends BaseAdditionalDatabaseTest
{

  private boolean isDatabaseRunning;
  private EmbeddedPostgreSQLWrapper embeddedPostgreSQL;

  @BeforeEach
  public void createDatabase()
    throws SchemaCrawlerException, SQLException, IOException
  {
    try
    {
      embeddedPostgreSQL = new EmbeddedPostgreSQLWrapper();
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

  @AfterEach
  public void stopDatabaseServer()
    throws SchemaCrawlerException
  {
    if (isDatabaseRunning)
    {
      embeddedPostgreSQL.stopServer();
    }
  }

  @Test
  public void testPostgreSQLCatalog()
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
      .includeAllSequences().includeAllSynonyms().includeAllRoutines()
      .tableTypes("TABLE,VIEW,MATERIALIZED VIEW");
    final SchemaCrawlerOptions options = schemaCrawlerOptionsBuilder
      .toOptions();

    final Connection connection = checkConnection(getConnection());
    final DatabaseConnector postgreSQLDatabaseConnector = new PostgreSQLDatabaseConnector();

    final SchemaRetrievalOptions schemaRetrievalOptions = postgreSQLDatabaseConnector
      .getSchemaRetrievalOptionsBuilder(connection).toOptions();

    final SchemaCrawler schemaCrawler = new SchemaCrawler(getConnection(),
                                                          schemaRetrievalOptions,
                                                          options);
    final Catalog catalog = schemaCrawler.crawl();
    final List<Property> serverInfo = new ArrayList<>(catalog.getDatabaseInfo()
      .getServerInfo());

    assertThat(serverInfo.size(), equalTo(1));
    assertThat(serverInfo.get(0).getName(), equalTo("current_database"));
    assertThat(serverInfo.get(0).getValue(), equalTo("schemacrawler"));

    LOGGER.log(Level.INFO, "Completed PostgreSQL catalog test successfully");
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
      .includeAllSequences().includeAllSynonyms().includeAllRoutines()
      .tableTypes("TABLE,VIEW,MATERIALIZED VIEW");
    final SchemaCrawlerOptions options = schemaCrawlerOptionsBuilder
      .toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());

    assertThat(outputOf(executableExecution(getConnection(), executable)),
               hasSameContentAs(classpathResource("testPostgreSQLWithConnection.txt")));
    LOGGER.log(Level.INFO, "Completed PostgreSQL test successfully");
  }

}
