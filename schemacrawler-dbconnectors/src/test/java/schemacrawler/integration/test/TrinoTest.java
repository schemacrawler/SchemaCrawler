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
import static us.fatehi.test.integration.utility.TrinoTestUtility.newTrinoContainer;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.JdbcUrl;
import us.fatehi.utility.datasource.JdbcUrlParser;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@HeavyDatabaseTest("trino")
@Testcontainers(disabledWithoutDocker = true)
public class TrinoTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newTrinoContainer();

  private DatabaseConnectionSource connectionSource;

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String jdbcUrl = dbContainer.getJdbcUrl();
    final JdbcUrl parsedUrl = JdbcUrlParser.parse(jdbcUrl);
    final String host = dbContainer.getHost();
    final int port = parsedUrl.port();
    final String database = parsedUrl.databaseName();

    final DatabaseConnector connector =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry()
            .findDatabaseConnectorFromDatabaseSystemIdentifier("trino");
    final DatabaseConnectionOptions connectionOptions =
        new DatabaseServerHostConnectionOptions("trino", host, port, database, Map.of());
    connectionSource =
        connector.newDatabaseConnectionSource(
            connectionOptions,
            new MultiUseUserCredentials(dbContainer.getUsername(), dbContainer.getPassword()));
  }

  @Test
  public void testTrinoWithConnection() throws Exception {

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeSchemas(Pattern.compile("tpch\\.sf100"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());
    schemaCrawlerOptions.withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);

    final String expectedResource = "testTrinoWithConnection.txt";
    assertThat(
        outputOf(executableExecution(connectionSource, executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
