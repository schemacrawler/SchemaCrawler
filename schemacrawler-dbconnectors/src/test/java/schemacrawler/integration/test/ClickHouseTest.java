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
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.testdb.TestSchemaCreator;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.test.integration.utility.ClickHouseTestUtility;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@DisableLogging
@HeavyDatabaseTest("clickhouse")
@Testcontainers(disabledWithoutDocker = true)
public class ClickHouseTest {

  @Container
  private final JdbcDatabaseContainer<?> dbContainer =
      ClickHouseTestUtility.newClickhouseContainer();

  private DatabaseConnectionSource connectionSource;

  @BeforeEach
  public void createDatabase() throws Exception {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String jdbcUrl = dbContainer.getJdbcUrl();
    final String urlTail = jdbcUrl.substring("jdbc:clickhouse://".length());
    final int slashIndex = urlTail.indexOf('/');
    final String hostPort = slashIndex >= 0 ? urlTail.substring(0, slashIndex) : urlTail;
    final String database = slashIndex >= 0 ? urlTail.substring(slashIndex + 1) : "";
    final String[] hostAndPort = hostPort.split(":", 2);
    final String host = hostAndPort[0];
    final int port = Integer.parseInt(hostAndPort[1]);

    final DatabaseConnector connector =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry()
            .findDatabaseConnectorFromDatabaseSystemIdentifier("clickhouse");
    final DatabaseConnectionOptions connectionOptions =
        new DatabaseServerHostConnectionOptions("clickhouse", host, port, database, Map.of());
    connectionSource =
        connector.newDatabaseConnectionSource(
            connectionOptions,
            new MultiUseUserCredentials(dbContainer.getUsername(), dbContainer.getPassword()));

    try (final Connection connection = connectionSource.get()) {
      new TestSchemaCreator(connection, "/clickhouse.scripts.txt", false).run();
    }
  }

  @Test
  public void testClickHouseWithConnection() throws Exception {
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

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final String expectedResource = "testClickHouseWithConnection.txt";
    assertThat(
        outputOf(executableExecution(connectionSource, executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
