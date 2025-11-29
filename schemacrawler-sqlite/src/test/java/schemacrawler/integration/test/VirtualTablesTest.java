/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithLocale;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@ResolveTestContext
public class VirtualTablesTest extends BaseSqliteTest {

  @Test
  @WithLocale("us-US")
  public void count(final TestContext testContext) throws Exception {
    run(
        testContext.testMethodFullName(),
        InfoLevel.minimum,
        OperationType.count.name(),
        new IncludeAll());
  }

  @Test
  public void list(final TestContext testContext) throws Exception {
    run(testContext.testMethodFullName(), InfoLevel.minimum, "list", new IncludeAll());
  }

  @Test
  public void schema(final TestContext testContext) throws Exception {
    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () ->
                run(
                    testContext.testMethodFullName(),
                    InfoLevel.standard,
                    "schema",
                    new IncludeAll()));
    assertThat(
        exception.getMessage(),
        endsWith(
            "Could not retrieve table columns for table <demo>: "
                + "[SQLITE_ERROR] SQL error or missing database (no such module: spellfix1)"));
  }

  @Test
  public void schemaNonVirtual(final TestContext testContext) throws Exception {
    run(
        testContext.testMethodFullName(),
        InfoLevel.standard,
        "schema",
        new RegularExpressionExclusionRule("demo.*"));
  }

  private void run(
      final String currentMethodFullName,
      final InfoLevel infoLevel,
      final String command,
      final InclusionRule tableInclusionRule)
      throws Exception {

    final DatabaseConnectionSource dataSource =
        createDataSourceFromResource("with_spellfix1_tables.db");

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeTables(tableInclusionRule);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel());
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions())
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaTextOptions textOptions = SchemaTextOptionsBuilder.newSchemaTextOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(currentMethodFullName)));
  }
}
