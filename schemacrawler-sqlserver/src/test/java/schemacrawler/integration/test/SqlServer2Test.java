/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.database.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.server.sqlserver.SqlServerDatabaseConnector;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.property.Property;

@DisableLogging
@TestInstance(Lifecycle.PER_CLASS)
@ResolveTestContext
@EnabledIfEnvironmentVariable(
    named = "TEST_AGAINST_RUNNING_SERVER",
    matches = "true",
    disabledReason = "No running server defined")
public class SqlServer2Test extends BaseAdditionalDatabaseTest {

  @BeforeAll
  public void createDatabase() {
    final String host = System.getenv("SCHCRWLR_HOST");
    final String user = System.getenv("SCHCRWLR_DATABASE_USER");
    final String password = System.getenv("SCHCRWLR_DATABASE_PASSWORD");
    final String connectionUrl =
        "jdbc:sqlserver://"
            + host
            + ":1433"
            + ";databaseName=master;trustServerCertificate=true;encrypt=false";
    createDataSource(connectionUrl, user, password);
  }

  @Test
  public void testSQLServerCatalog() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS\\.dbo"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllRoutines()
            .tableTypes("TABLE,VIEW,MATERIALIZED VIEW");
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Connection connection = checkConnection(getConnection());
    final DatabaseConnector databaseConnector = new SqlServerDatabaseConnector();

    final SchemaRetrievalOptions schemaRetrievalOptions =
        databaseConnector.getSchemaRetrievalOptionsBuilder(connection).toOptions();

    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(getDataSource(), schemaRetrievalOptions, schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();
    final List<Property> serverInfo = new ArrayList<>(catalog.getDatabaseInfo().getServerInfo());

    assertThat(serverInfo.size(), equalTo(15));

    final List<DatabaseUser> databaseUsers = (List<DatabaseUser>) catalog.getDatabaseUsers();
    assertThat(databaseUsers, hasSize(2));
    assertThat(
        databaseUsers.stream().map(DatabaseUser::getName).collect(Collectors.toList()),
        hasItems("OTHERUSER"));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().size())
            .collect(Collectors.toList()),
        hasItems(5));
    assertThat(
        databaseUsers.stream()
            .map(databaseUser -> databaseUser.getAttributes().keySet())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet()),
        hasItems("CREATE_DATE", "TYPE", "MODIFY_DATE", "AUTHENTICATION_TYPE"));
  }

  @Test
  public void testSQLServerWithConnection() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS\\.dbo"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllRoutines()
            .tableTypes("TABLE,VIEW,MATERIALIZED VIEW");
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();
    final Config config = SchemaTextOptionsBuilder.builder(textOptions).toConfig();

    // -- Schema output tests for "details" command
    final SchemaCrawlerExecutable executableDetails = new SchemaCrawlerExecutable("details");
    executableDetails.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executableDetails.setAdditionalConfiguration(config);

    final String expectedResource = "testSQLServerWithConnection.txt";
    assertThat(
        outputOf(executableExecution(getDataSource(), executableDetails)),
        hasSameContentAs(classpathResource(expectedResource)));

    // -- Additional catalog tests
    final Catalog catalog = executableDetails.getCatalog();

    final Table table = catalog.lookupTable(new SchemaReference("BOOKS", "dbo"), "Authors").get();
    final Column column = table.lookupColumn("FirstName").get();
    assertThat(column.getPrivileges(), is(empty()));
  }

  @Test
  public void testSQLServerPortable(final TestContext testContext) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS\\.dbo"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllRoutines()
            .tableTypes("TABLE,VIEW,MATERIALIZED VIEW");
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noInfo().portable(PortableType.broad);
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();
    final Config config = SchemaTextOptionsBuilder.builder(textOptions).toConfig();

    // -- Schema output tests for "details" command
    final SchemaCrawlerExecutable executableDetails = new SchemaCrawlerExecutable("schema");
    executableDetails.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executableDetails.setAdditionalConfiguration(config);

    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(
        outputOf(executableExecution(getDataSource(), executableDetails)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
