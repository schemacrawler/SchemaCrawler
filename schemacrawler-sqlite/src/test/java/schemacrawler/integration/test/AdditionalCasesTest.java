/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@ResolveTestContext
public class AdditionalCasesTest extends BaseSqliteTest {

  @Test
  public void advancedUsage(final TestContext testContext) throws Exception {
    run(testContext.testMethodFullName(), "/advanced_usage.sql", "details");
  }

  @Test
  public void quotedCreateScript(final TestContext testContext) throws Exception {
    run(testContext.testMethodFullName(), "/identifiers_unquoted.sql", "schema");
    run(testContext.testMethodFullName(), "/identifiers_quoted.sql", "schema");
  }

  @Test
  public void renameTable(final TestContext testContext) throws Exception {
    run(testContext.testMethodFullName(), "/rename_table.sql", "details");
  }

  @Test
  public void generatedColumn(final TestContext testContext) throws Exception {
    run(testContext.testMethodFullName(), "/generated_column.sql", "schema");
  }

  private void run(
      final String currentMethodFullName, final String databaseSqlResource, final String command)
      throws Exception {

    final DatabaseConnectionSource dataSource =
        createDatabaseFromScriptInMemory(databaseSqlResource);

    final SchemaCrawlerOptions options =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final SchemaTextOptions textOptions = SchemaTextOptionsBuilder.newSchemaTextOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(currentMethodFullName)));
  }
}
