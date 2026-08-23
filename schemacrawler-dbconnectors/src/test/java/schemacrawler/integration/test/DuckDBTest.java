/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.testdb.TestSchemaCreator;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@DisableLogging
@DisabledOnOs(value = OS.WINDOWS, disabledReason = "DuckDB does not run on Windows ARM")
public class DuckDBTest extends BaseAdditionalDatabaseTest {

  @BeforeEach
  public void createDatabase() throws IOException, Exception {
    final Path databasePath = IOUtility.createTempFilePath("sc.", "db");

    final DatabaseConnector connector =
        DatabaseConnectorRegistry.getRegistry().getDatabaseConnector("duckdb");
    final DatabaseConnectionOptions connectionOptions =
        new DatabaseServerHostConnectionOptions(
            "duckdb", null, null, databasePath.toString(), Map.of());
    createConnectionSource(
        connector.newDatabaseConnectionSource(connectionOptions, new MultiUseUserCredentials()));

    try (final Connection connection = getConnection()) {
      new TestSchemaCreator(connection, "/duckdb.scripts.txt", false).run();
    }
  }

  @Test
  public void testDuckDBWithConnection() throws Exception {
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions());
    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noIndexNames().showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final String expectedResource = "testDuckDBWithConnection.txt";
    assertThat(
        outputOf(executableExecution(getConnectionSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
