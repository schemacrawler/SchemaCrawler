/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.SqlServerTestUtility.newSqlServerContainer;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestDebugLogging;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.utility.database.SqlScript;

@TestDebugLogging("INFO")
@ResolveTestContext
@HeavyDatabaseTest("sqlserver")
@Testcontainers
@EnabledOnOs(
    architectures = {"x64", "x86_64", "amd64"},
    disabledReason = "SQL Server Docker container does not run on ARM")
public class AcrossDatabaseTest extends BaseAdditionalDatabaseTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newSqlServerContainer();

  @Test
  public void acrossDatabase(final TestContext testContext) throws Exception {

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(
                schema ->
                    Arrays.asList(
                            "DATABASE_A.dbo", "DATABASE_A.SCHEMA_A_A", "DATABASE_A.SCHEMA_A_B")
                        .contains(schema));
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder =
        SchemaInfoLevelBuilder.builder()
            .withTag("maximum-without-grants")
            .withInfoLevel(InfoLevel.maximum)
            .setRetrieveTablePrivileges(false)
            .setRetrieveTableColumnPrivileges(false);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(schemaInfoLevelBuilder.toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final String expectedResource = testContext.testMethodFullName();
    assertThat(
        outputOf(executableExecution(getDataSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }

  @BeforeEach
  public void createDatabase() throws SQLException {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String jdbcUrl = dbContainer.getJdbcUrl();
    final String user = dbContainer.getUsername();
    final String password = dbContainer.getPassword();

    createDataSource(jdbcUrl, user, password);

    // Note: The database connection needs to be closed for the new schemas to be recognized
    try (final Connection connection = getConnection()) {
      SqlScript.executeScriptFromResource("/across-database.sql", connection);
    }

    // Create a new set of database connections for crawling the schema,
    // with the master database as the default
    createDataSource(jdbcUrl, user, password, "database=master");
  }
}
