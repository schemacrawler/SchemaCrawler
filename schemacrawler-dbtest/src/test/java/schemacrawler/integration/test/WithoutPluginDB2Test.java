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
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.integration.utility.DB2TestUtility.newDB2Container;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
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

@DisableLogging
@HeavyDatabaseTest("db2")
@Testcontainers
@ResolveTestContext
@EnabledOnOs(
    architectures = {"x64", "x86_64", "amd64"},
    disabledReason = "IBM DB2 Docker container does not run on ARM")
public class WithoutPluginDB2Test extends BaseAdditionalDatabaseTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newDB2Container();
  private String schemaName;

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String username = dbContainer.getUsername();
    createDataSource(dbContainer.getJdbcUrl(), username, dbContainer.getPassword());
    schemaName = username.toUpperCase();

    createDatabase("/db2.scripts.txt");
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "db2")
  // See Issue #658 - Failure in scanning a DB2 database in z/os
  public void testDB2WithConnection() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(schemaName))
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

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    // -- Schema output tests
    final String expectedResource = "testDB2WithConnection.txt";
    assertThat(
        outputOf(executableExecution(getDataSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "db2")
  public void testDB2Portable(final TestContext testContext) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(schemaName))
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
    textOptionsBuilder.noInfo().portable(PortableType.broad);
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();
    final Config config = SchemaTextOptionsBuilder.builder(textOptions).toConfig();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("schema");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);

    // -- Schema output tests
    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(
        outputOf(executableExecution(getDataSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
