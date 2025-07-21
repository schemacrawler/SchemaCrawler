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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.MySQLTestUtility.newMySQLContainer;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.javaVersion;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;

@HeavyDatabaseTest("mysql")
@Testcontainers
@ResolveTestContext
public class MySQLTest extends BaseAdditionalDatabaseTest {

  @Container
  private final JdbcDatabaseContainer<?> dbContainer =
      newMySQLContainer().withUsername("schemacrawler").withDatabaseName("books");

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    // Use default connection properties from MySQLDatabaseConnector
    final Properties connectionProperties = new Properties();
    connectionProperties.put("nullNamePatternMatchesAll", "true");
    connectionProperties.put("getProceduresReturnsFunctions", "false");
    connectionProperties.put("noAccessToProcedureBodies", "true");
    connectionProperties.put("logger", "Jdk14Logger");
    connectionProperties.put("dumpQueriesOnException", "true");
    connectionProperties.put("dumpMetadataOnColumnNotFound", "true");
    connectionProperties.put("maxQuerySizeToLog", "4096");
    connectionProperties.put("disableMariaDbDriver", "true");
    connectionProperties.put("useInformationSchema", "true");

    final StringBuilder connectionPropertiesString = new StringBuilder();
    connectionProperties.entrySet().stream()
        .forEach(
            entry ->
                connectionPropertiesString
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(";"));

    createDataSource(
        dbContainer.getJdbcUrl(),
        dbContainer.getUsername(),
        dbContainer.getPassword(),
        connectionPropertiesString.toString());

    createDatabase("/mysql.scripts.txt");
  }

  @Test
  public void testMySQLWithConnection() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("books"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllRoutines();
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

    final String expectedResource = String.format("testMySQLWithConnection.%s.txt", javaVersion());
    assertThat(
        outputOf(executableExecution(getDataSource(), executableDetails)),
        hasSameContentAs(classpathResource(expectedResource)));

    // Additional catalog tests
    final Catalog catalog = executableDetails.getCatalog();

    final Table table = catalog.lookupTable(new SchemaReference("books", null), "authors").get();
    final Column column = table.lookupColumn("FirstName").get();
    assertThat(column.getPrivileges(), is(empty()));

    // INFO: Current user has no access to MYSQL.USER
    final List<DatabaseUser> databaseUsers = (List<DatabaseUser>) catalog.getDatabaseUsers();
    assertThat(databaseUsers, hasSize(0));
  }

  @Test
  public void testMySQLPortable(final TestContext testContext) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("books"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllRoutines();
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

    // -- Schema output tests
    final SchemaCrawlerExecutable executableDetails = new SchemaCrawlerExecutable("schema");
    executableDetails.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executableDetails.setAdditionalConfiguration(config);

    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(
        outputOf(executableExecution(getDataSource(), executableDetails)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
