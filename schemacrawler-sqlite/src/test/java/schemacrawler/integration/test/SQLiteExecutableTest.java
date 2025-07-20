/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@ResolveTestContext
public class SQLiteExecutableTest extends BaseSqliteTest {

  @Test
  public void count(final TestContext testContext) throws Exception {
    runWithContentComparison(testContext.testMethodFullName(), InfoLevel.minimum, OperationType.count.name());
  }

  @Test
  public void dump(final TestContext testContext) throws Exception {
    runWithContentComparison(testContext.testMethodFullName(), InfoLevel.standard, OperationType.dump.name());
  }

  @Test
  public void list(final TestContext testContext) throws Exception {
    runWithContentComparison(testContext.testMethodFullName(), InfoLevel.minimum, "list");
  }

  @Test
  public void tablesample(final TestContext testContext) throws Exception {
    runWithFileSizeCheck(InfoLevel.standard, OperationType.tablesample.name());
  }

  private void runWithContentComparison(
      final String currentMethodFullName, final InfoLevel infoLevel, final String command)
      throws Exception {
    final Consumer<Path> assertion = outputFile ->
        assertThat(
            outputOf(outputFile),
            hasSameContentAs(classpathResource(currentMethodFullName)));

    runExecutable(infoLevel, command, assertion);
  }

  private void runWithFileSizeCheck(final InfoLevel infoLevel, final String command)
      throws Exception {
    final Consumer<Path> assertion = outputFile -> {
      try {
        assertThat("Output file should exist", Files.exists(outputFile), is(true));
        assertThat("Output file should have content", Files.size(outputFile), greaterThan(0L));
      } catch (IOException e) {
        throw new RuntimeException("Failed to check file size", e);
      }
    };

    runExecutable(infoLevel, command, assertion);
  }

  private void runExecutable(
      final InfoLevel infoLevel,
      final String command,
      final Consumer<Path> outputAssertion) throws Exception {
    final Path sqliteDbFile = createTestDatabase();
    final DatabaseConnectionSource dataSource = createDataSourceFromFile(sqliteDbFile);

    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel());
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptions textOptions = SchemaTextOptionsBuilder.newSchemaTextOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final Path outputFile = executableExecution(dataSource, executable);
    outputAssertion.accept(outputFile);
  }
}
