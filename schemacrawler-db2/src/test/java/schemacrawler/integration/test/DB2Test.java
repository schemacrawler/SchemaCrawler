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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.javaVersion;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.JdbcDatabaseContainer;
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
import schemacrawler.server.db2.DB2DatabaseConnector;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseBuildCondition;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(HeavyDatabaseBuildCondition.class)
public class DB2Test
  extends BaseAdditionalDatabaseTest
{

  @Container
  private JdbcDatabaseContainer dbContainer =
    new HeavyDatabaseBuildCondition().getJdbcDatabaseContainer(() -> new Db2Container().acceptLicense());

  @BeforeEach
  public void createDatabase()
    throws SQLException, SchemaCrawlerException
  {
    /**
     // Add the following trace properties to the URL for debugging
     // Set the trace directory appropriately
     final String traceProperties =
     ":traceDirectory=C:\\Java" + ";traceFile=trace3"
     + ";traceFileAppend=false" + ";traceLevel="
     + (DB2BaseDataSource.TRACE_ALL) + ";";
     */
    createDataSource(dbContainer.getJdbcUrl(),
                     dbContainer.getUsername(),
                     dbContainer.getPassword());

    createDatabase("/db2.scripts.txt");
  }

  @Test
  public void testDB2CatalogServerInfo()
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      SchemaCrawlerOptionsBuilder.builder();
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule("DB2INST1"));
    final SchemaCrawlerOptions options =
      schemaCrawlerOptionsBuilder.toOptions();

    final Connection connection = checkConnection(getConnection());
    final DatabaseConnector db2DatabaseConnector = new DB2DatabaseConnector();

    final SchemaRetrievalOptions schemaRetrievalOptions = db2DatabaseConnector
      .getSchemaRetrievalOptionsBuilder(connection)
      .toOptions();

    final SchemaCrawler schemaCrawler =
      new SchemaCrawler(getConnection(), schemaRetrievalOptions, options);
    final Catalog catalog = schemaCrawler.crawl();
    final List<Property> serverInfo = new ArrayList<>(catalog
                                                        .getDatabaseInfo()
                                                        .getServerInfo());

    assertThat(serverInfo.size(), equalTo(4));
    assertThat(serverInfo
                 .get(0)
                 .getName(), equalTo("HOST_NAME"));
    assertThat(String.valueOf(serverInfo
                                .get(0)
                                .getValue()), matchesPattern("[0-9a-z]{12}"));
  }

  @Test
  public void testDB2WithConnection()
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      SchemaCrawlerOptionsBuilder.builder();
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule("DB2INST1"))
      .includeAllSequences()
      .includeAllSynonyms()
      .includeRoutines(new RegularExpressionInclusionRule("[0-9a-zA-Z_\\.]*"))
      .tableTypes("TABLE,VIEW,MATERIALIZED QUERY TABLE");
    final SchemaCrawlerOptions options =
      schemaCrawlerOptionsBuilder.toOptions();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder()
      .portableNames();
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

    final String expectedResource = String.format("testDB2WithConnection.%s.txt", javaVersion());
    assertThat(outputOf(executableExecution(getConnection(), executable)),
               hasSameContentAs(classpathResource(expectedResource)));
  }

}
