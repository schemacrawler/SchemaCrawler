/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.DB2TestUtility.newDB211Container;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.javaVersion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.Property;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;

@HeavyDatabaseTest
@Testcontainers
public class DB2Test extends BaseAdditionalDatabaseTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newDB211Container();

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    // Add the following trace properties to the URL for debugging
    // Set the trace directory appropriately
    // final String traceProperties = ":traceDirectory=C:\\Java" + ";traceFile=trace3" +
    // ";traceFileAppend=false" + ";traceLevel=" + (DB2BaseDataSource.TRACE_ALL) + ";";

    createDataSource(
        dbContainer.getJdbcUrl(), dbContainer.getUsername(), dbContainer.getPassword());

    createDatabase("/db2.scripts.txt");
  }

  @Test
  public void testDB2Dump() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("DB2INST1"))
            .tableTypes("TABLE,VIEW,MATERIALIZED QUERY TABLE");
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.showDatabaseInfo().noInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("dump");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    // -- Schema output tests
    final String expectedResource = String.format("testDB2Dump.txt", javaVersion());
    assertThat(
        outputOf(executableExecution(getDataSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void testDB2WithConnection() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("DB2INST1"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeRoutines(new RegularExpressionInclusionRule("[0-9a-zA-Z_\\.]*"))
            .tableTypes("TABLE,VIEW,MATERIALIZED QUERY TABLE");
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    // -- Schema output tests for "details" command
    final SchemaCrawlerExecutable executableDetails = new SchemaCrawlerExecutable("details");
    executableDetails.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executableDetails.setAdditionalConfiguration(
        SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final String expectedResource = String.format("testDB2WithConnection.%s.txt", javaVersion());
    assertThat(
        outputOf(executableExecution(getDataSource(), executableDetails)),
        hasSameContentAs(classpathResource(expectedResource)));

    // -- Schema output tests for "dump" command
    final SchemaCrawlerExecutable executableDump = new SchemaCrawlerExecutable("dump");
    executableDump.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executableDump.setAdditionalConfiguration(
        SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    assertThat(
        outputOf(executableExecution(getDataSource(), executableDump)),
        hasSameContentAs(classpathResource("testDB2Dump.txt")));

    // -- Additional catalog tests
    final Catalog catalog = executableDetails.getCatalog();

    final List<Property> serverInfo = new ArrayList<>(catalog.getDatabaseInfo().getServerInfo());
    assertThat(serverInfo.size(), equalTo(6));
    final Property property = serverInfo.get(0);
    assertThat(property.getName(), equalTo("CURRENT_SERVER"));
    assertThat(property.getValue(), equalTo("TEST"));

    final Table table = catalog.lookupTable(new SchemaReference(null, "DB2INST1"), "AUTHORS").get();
    final Column column = table.lookupColumn("FIRSTNAME").get();
    assertThat(column.getPrivileges(), is(empty()));

    final List<DatabaseUser> databaseUsers = (List<DatabaseUser>) catalog.getDatabaseUsers();
    assertThat(databaseUsers, hasSize(1));
    assertThat(
        databaseUsers.stream().map(DatabaseUser::getName).collect(Collectors.toList()),
        hasItems("DB2INST1"));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().size())
            .collect(Collectors.toList()),
        hasItems(16));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().keySet())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet()),
        hasItems(
            "BINDADDAUTH",
            "CONNECTAUTH",
            "CREATETABAUTH",
            "DBADMAUTH",
            "EXTERNALROUTINEAUTH",
            "IMPLSCHEMAAUTH",
            "LOADAUTH",
            "NOFENCEAUTH",
            "QUIESCECONNECTAUTH",
            "SECURITYADMAUTH",
            "SQLADMAUTH",
            "WLMADMAUTH",
            "EXPLAINAUTH",
            "DATAACCESSAUTH",
            "ACCESSCTRLAUTH",
            "CREATESECUREAUTH"));
  }
}
