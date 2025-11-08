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
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.integration.utility.OracleTestUtility.newOracleContainer;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.test.integration.utility.OracleTestUtility;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@TestInstance(Lifecycle.PER_CLASS)
@HeavyDatabaseTest("oracle")
@Testcontainers(disabledWithoutDocker = true)
@ResolveTestContext
@DisplayName("Test for operations including tablesample")
public class OracleOperationsTest extends BaseAdditionalDatabaseTest {

  @Container private static final JdbcDatabaseContainer<?> dbContainer = newOracleContainer();

  @Test
  public void count(final TestContext testContext) throws Exception {
    runWithContentComparison(
        testContext.testMethodFullName(),
        getDataSource(),
        InfoLevel.minimum,
        OperationType.count.name());
  }

  @BeforeAll
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String jdbcUrl = dbContainer.getJdbcUrl();
    createDataSource(jdbcUrl, "SYS AS SYSDBA", dbContainer.getPassword(), OracleTestUtility.urlx());

    createDatabase("/oracle.scripts.txt");
  }

  @Test
  public void dump(final TestContext testContext) throws Exception {
    runWithContentComparison(
        testContext.testMethodFullName(),
        getDataSource(),
        InfoLevel.standard,
        OperationType.dump.name());
  }

  @Test
  public void list(final TestContext testContext) throws Exception {
    runWithContentComparison(
        testContext.testMethodFullName(), getDataSource(), InfoLevel.minimum, "list");
  }

  @Test
  public void tablesample(final TestContext testContext) throws Exception {
    runWithFileSizeCheck(getDataSource(), InfoLevel.standard, OperationType.tablesample.name());
  }

  private void runExecutable(
      final DatabaseConnectionSource dataSource,
      final InfoLevel infoLevel,
      final String command,
      final Consumer<Path> outputAssertion)
      throws Exception {

    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel());
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptions textOptions = SchemaTextOptionsBuilder.builder().noInfo().toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final Path outputFile = executableExecution(dataSource, executable);
    outputAssertion.accept(outputFile);
  }

  private void runWithContentComparison(
      final String currentMethodFullName,
      final DatabaseConnectionSource dataSource,
      final InfoLevel infoLevel,
      final String command)
      throws Exception {
    final Consumer<Path> assertion =
        outputFile ->
            assertThat(
                outputOf(outputFile), hasSameContentAs(classpathResource(currentMethodFullName)));

    runExecutable(dataSource, infoLevel, command, assertion);
  }

  private void runWithFileSizeCheck(
      final DatabaseConnectionSource dataSource, final InfoLevel infoLevel, final String command)
      throws Exception {
    final Consumer<Path> assertion =
        outputFile -> {
          try {
            assertThat("Output file should exist", Files.exists(outputFile), is(true));
            assertThat("Output file should have content", Files.size(outputFile), greaterThan(0L));
          } catch (final IOException e) {
            throw new RuntimeException("Failed to check file size", e);
          }
        };

    runExecutable(dataSource, infoLevel, command, assertion);
  }
}
