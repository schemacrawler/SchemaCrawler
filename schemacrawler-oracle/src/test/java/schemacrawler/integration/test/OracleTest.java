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
import static schemacrawler.integration.test.utility.OracleTestUtility.newOracleContainer;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

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
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@HeavyDatabaseTest("oracle")
@Testcontainers
@ResolveTestContext
public class OracleTest extends BaseOracleWithConnectionTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newOracleContainer();

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String jdbcUrl = dbContainer.getJdbcUrl();
    final String urlx = "restrictGetTables=true;useFetchSizeWithLongColumn=true";
    createDataSource(jdbcUrl, "SYS AS SYSDBA", dbContainer.getPassword(), urlx);

    createDatabase("/oracle.scripts.txt");
  }

  @Test
  public void testOracleWithConnection() throws Exception {
    final DatabaseConnectionSource dataSource = getDataSource();

    final String expectedResource = "testOracleWithConnection.txt";
    testOracleWithConnection(dataSource, expectedResource, 33, false);

    testSelectQuery(dataSource, "testOracleWithConnectionQuery.txt");
  }

  @Test
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
