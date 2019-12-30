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


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Property;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.server.sqlserver.SqlServerDatabaseConnector;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseBuildCondition;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(HeavyDatabaseBuildCondition.class)
public class SqlServerTest
  extends BaseAdditionalDatabaseTest
{

  @Container
  private JdbcDatabaseContainer dbContainer =
    new HeavyDatabaseBuildCondition().getJdbcDatabaseContainer(() -> new MSSQLServerContainer<>());

  @BeforeEach
  public void createDatabase()
    throws SQLException, SchemaCrawlerException
  {
    createDataSource(dbContainer.getJdbcUrl(),
                     dbContainer.getUsername(),
                     dbContainer.getPassword());

    createDatabase("/sqlserver.scripts.txt");
  }

  @Test
  public void testSQLServerCatalog()
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      SchemaCrawlerOptionsBuilder.builder();
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule("BOOKS\\.dbo"))
      .includeAllSequences()
      .includeAllSynonyms()
      .includeAllRoutines()
      .tableTypes("TABLE,VIEW,MATERIALIZED VIEW");
    final SchemaCrawlerOptions options =
      schemaCrawlerOptionsBuilder.toOptions();

    final Connection connection = checkConnection(getConnection());
    final DatabaseConnector databaseConnector =
      new SqlServerDatabaseConnector();

    final SchemaRetrievalOptions schemaRetrievalOptions = databaseConnector
      .getSchemaRetrievalOptionsBuilder(connection)
      .toOptions();

    final SchemaCrawler schemaCrawler =
      new SchemaCrawler(getConnection(), schemaRetrievalOptions, options);
    final Catalog catalog = schemaCrawler.crawl();
    final List<Property> serverInfo = new ArrayList<>(catalog
                                                        .getDatabaseInfo()
                                                        .getServerInfo());
    System.out.println(serverInfo);
    assertThat(serverInfo.size(), equalTo(3));
    assertThat(serverInfo
                 .get(0)
                 .getName(), equalTo("InstanceName"));
    assertThat(serverInfo
                 .get(0)
                 .getValue(), is(nullValue()));
  }

  @Test
  public void testSQLServerWithConnection()
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      SchemaCrawlerOptionsBuilder.builder();
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule("BOOKS\\.dbo"))
      .includeAllSequences()
      .includeAllSynonyms()
      .includeAllRoutines()
      .tableTypes("TABLE,VIEW,MATERIALIZED VIEW");
    final SchemaCrawlerOptions options =
      schemaCrawlerOptionsBuilder.toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder =
      SchemaTextOptionsBuilder.builder();
    textOptionsBuilder
      .showDatabaseInfo()
      .showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable =
      new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
                                            .builder(textOptions)
                                            .toConfig());

    assertThat(outputOf(executableExecution(getConnection(), executable)),
               hasSameContentAs(classpathResource(
                 "testSQLServerWithConnection.txt")));
  }

}
