/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.sql.Connection;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.testdb.TestSchemaCreator;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.test.integration.utility.SnowflakeTestUtility;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.JdbcUrl;
import us.fatehi.utility.datasource.JdbcUrlParser;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@DisableLogging
@HeavyDatabaseTest("snowflake")
@EnabledIfEnvironmentVariable(named = "LOCALSTACK_AUTH_TOKEN", matches = ".+")
@Testcontainers(disabledWithoutDocker = true)
public class SnowflakeTest extends BaseAdditionalDatabaseTest {

  @Container
  private final JdbcDatabaseContainer<?> dbContainer = SnowflakeTestUtility.newSnowflakeContainer();

  @BeforeEach
  public void createDatabase() throws Exception {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String jdbcUrl = dbContainer.getJdbcUrl();
    final JdbcUrl parsedUrl = JdbcUrlParser.parse(jdbcUrl);
    final String host = dbContainer.getHost();
    final int port = parsedUrl.port();

    final DatabaseConnector connector =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry()
            .findDatabaseConnectorFromDatabaseSystemIdentifier("snowflake");
    final MultiUseUserCredentials credentials =
        new MultiUseUserCredentials(dbContainer.getUsername(), dbContainer.getPassword());

    // First connection: no database selected, used to create the schema
    final DatabaseConnectionOptions setupConnectionOptions =
        new DatabaseServerHostConnectionOptions("snowflake", host, port, "", Map.of());
    try (final DatabaseConnectionSource setupSource =
        connector.newDatabaseConnectionSource(setupConnectionOptions, credentials)) {
      try (final Connection connection = setupSource.get()) {
        new TestSchemaCreator(connection, "/snowflake.scripts.txt", false).run();
      }
    }

    // Second connection: use database and schema for the actual test
    final DatabaseConnectionOptions connectionOptions =
        new DatabaseServerHostConnectionOptions(
            "snowflake",
            host,
            port,
            dbContainer.getDatabaseName(),
            Map.of("schema", "BOOKS", "tracing", "ALL"));
    createConnectionSource(connector.newDatabaseConnectionSource(connectionOptions, credentials));
  }

  @Test
  public void testSnowflakeWithConnection() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS\\.BOOKS"))
            .includeAllTables()
            .includeAllRoutines()
            .includeAllSequences()
            .includeAllSynonyms()
            .tableTypes("TABLE", "VIEW");
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder().toConfig());

    final String expectedResource = "testSnowflakeWithConnection.txt";
    assertThat(
        outputOf(executableExecution(getConnectionSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
