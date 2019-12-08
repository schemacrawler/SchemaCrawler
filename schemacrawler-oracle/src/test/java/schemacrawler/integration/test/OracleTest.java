/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.core.IsEqual.equalTo;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.*;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Property;
import schemacrawler.schemacrawler.*;
import schemacrawler.server.oracle.OracleDatabaseConnector;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseBuildCondition;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(HeavyDatabaseBuildCondition.class)
public class OracleTest
  extends BaseAdditionalDatabaseTest
{

  @Container
  private OracleContainer dbContainer = new OracleContainer(
    "wnameless/oracle-xe-11g-r2");

  @BeforeEach
  public void createDatabase()
    throws SQLException, SchemaCrawlerException
  {
    final String urlx = "restrictGetTables=true;useFetchSizeWithLongColumn=true";
    createDataSource(dbContainer.getJdbcUrl(),
                     dbContainer.getUsername(),
                     dbContainer.getPassword(),
                     urlx);

    createDatabase("/oracle.11g.scripts.txt");
  }

  @Test
  public void testOracleCatalogServerInfo()
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder();
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule("BOOKS"));
    final SchemaCrawlerOptions options = schemaCrawlerOptionsBuilder
      .toOptions();

    final Connection connection = checkConnection(getConnection());
    final DatabaseConnector databaseConnector = new OracleDatabaseConnector();

    final SchemaRetrievalOptions schemaRetrievalOptions = databaseConnector
      .getSchemaRetrievalOptionsBuilder(connection).toOptions();

    final SchemaCrawler schemaCrawler = new SchemaCrawler(getConnection(),
                                                          schemaRetrievalOptions,
                                                          options);
    final Catalog catalog = schemaCrawler.crawl();
    final List<Property> serverInfo = new ArrayList<>(catalog.getDatabaseInfo()
                                                        .getServerInfo());

    assertThat(serverInfo.size(), equalTo(1));
    assertThat(serverInfo.get(0).getName(), equalTo("GLOBAL_NAME"));
    assertThat(String.valueOf(serverInfo.get(0).getValue()),
               matchesPattern("[0-9a-zA-Z]{1,12}"));
  }

  @Disabled("Very long running test, since getting definitions takes time")
  @Test
  public void testOracleWithConnection()
    throws Exception
  {
    final SchemaInfoLevelBuilder infoLevelBuilder = SchemaInfoLevelBuilder
      .builder().withTag("maximum").withInfoLevel(InfoLevel.maximum)
      /*
      .setRetrievePrimaryKeyDefinitions(false)
      .setRetrieveForeignKeyDefinitions(false)
      .setRetrieveTableConstraintDefinitions(false)
      .setRetrieveTableDefinitionsInformation(false)
      .setRetrieveIndexInformation(false)
      .setRetrieveIndexColumnInformation(false)
      .setRetrieveRoutineInformation(false);
      */
      ;

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder();
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(infoLevelBuilder.toOptions())
      .includeSchemas(new RegularExpressionInclusionRule("BOOKS"))
      .includeAllSequences().includeAllSynonyms()
      .includeRoutines(new RegularExpressionInclusionRule("[0-9a-zA-Z_\\.]*"))
      .tableTypes("TABLE,VIEW");
    final SchemaCrawlerOptions options = schemaCrawlerOptionsBuilder
      .toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(
      "details");
    executable.setSchemaCrawlerOptions(options);
    executable
      .setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions)
                                    .toConfig());

    assertThat(outputOf(executableExecution(getConnection(), executable)),
               hasSameContentAs(classpathResource("testOracleWithConnection.txt")));
  }

}
