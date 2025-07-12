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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@ResolveTestContext
public class ForeignKeyWithoutReferencedPrimaryKeyTest extends BaseSqliteTest {

  @Test
  public void foreignKeyWithoutReferencedPrimaryKey(final TestContext testContext)
      throws Exception {
    run(testContext.testMethodName(), "/foreignKeyWithoutReferencedPrimaryKey.sql", "schema");
  }

  private void run(
      final String currentMethodName, final String databaseSqlResource, final String command)
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
        hasSameContentAs(classpathResource(currentMethodName)));
  }
}
