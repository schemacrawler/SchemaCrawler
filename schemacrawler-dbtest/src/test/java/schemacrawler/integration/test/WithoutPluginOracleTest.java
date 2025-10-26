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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.integration.utility.OracleTestUtility.newOracleContainer;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@HeavyDatabaseTest("oracle")
@Testcontainers
@ResolveTestContext
public class WithoutPluginOracleTest extends BaseAdditionalDatabaseTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newOracleContainer();

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String urlx = "restrictGetTables=true;useFetchSizeWithLongColumn=true";
    createDataSource(dbContainer.getJdbcUrl(), "SYS AS SYSDBA", dbContainer.getPassword(), urlx);

    createDatabase("/oracle.scripts.txt");
  }

  @Test
  @DisplayName("Issue #628 - retrieve table and columns names with a slash or dot")
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "oracle")
  public void slashedName() throws Exception {

    final Connection connection = getConnection();
    try (final Statement stmt = connection.createStatement()) {
      stmt.execute("CREATE TABLE \"A/B\" (I INT)");
      stmt.execute("CREATE TABLE CD (\"E/F\" INT)");
      stmt.execute("CREATE TABLE \"G.H\" (J INT)");
      stmt.execute("CREATE TABLE \"KL\" (\"M/N\" INT)");
      // Auto-commited
    }

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS"))
            .tableTypes("TABLE");
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final RuntimeException executionRuntimeException =
        assertThrows(
            RuntimeException.class, () -> getCatalog(getDataSource(), schemaCrawlerOptions));
    final SQLException cause = (SQLException) executionRuntimeException.getCause().getCause();
    assertThat(cause.getSQLState(), is("22025")); // ORA-01424: missing or illegal character
    // following the escape character
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "oracle")
  public void testOracleWithConnection() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeTables(table -> !table.contains("Global")) // NOTE: Index retrieval fails
            .includeRoutines(new RegularExpressionInclusionRule("[0-9a-zA-Z_\\.]*"))
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

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    // -- Schema output tests
    final String expectedResource = "testOracleWithConnection.txt";
    assertThat(
        outputOf(executableExecution(getDataSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "oracle")
  public void testOraclePortable(final TestContext testContext) throws Exception {
    final DatabaseConnectionSource dataSource = getDataSource();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeRoutines(new RegularExpressionInclusionRule("[0-9a-zA-Z_\\.]*"))
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

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("schema");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);

    // -- Schema output tests
    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
