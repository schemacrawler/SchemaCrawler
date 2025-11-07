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
import static us.fatehi.test.integration.utility.DB2TestUtility.newDB2Container;
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
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@TestInstance(Lifecycle.PER_CLASS)
@HeavyDatabaseTest("db2")
@Testcontainers(disabledWithoutDocker = true)
@ResolveTestContext
@DisplayName("Test for operations including tablesample")
public class DB2OperationsTest extends BaseAdditionalDatabaseTest {

  @Container private static final JdbcDatabaseContainer<?> dbContainer = newDB2Container();
  private String schemaName;

  @BeforeAll
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    // Add the following trace properties to the URL for debugging
    // Set the trace directory appropriately
    // final String traceProperties = ":traceDirectory=C:\\Java" + ";traceFile=trace3" +
    // ";traceFileAppend=false" + ";traceLevel=" + (DB2BaseDataSource.TRACE_ALL) + ";";

    final String username = dbContainer.getUsername();
    createDataSource(dbContainer.getJdbcUrl(), username, dbContainer.getPassword());
    schemaName = username.toUpperCase();

    createDatabase("/db2.scripts.txt");
  }

  @Test
  public void count(final TestContext testContext) throws Exception {
    runWithContentComparison(
        testContext.testMethodFullName(),
        getDataSource(),
        InfoLevel.minimum,
        OperationType.count.name());
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

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeSchemas(new RegularExpressionInclusionRule("BOOKS"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel());
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions())
            .withLimitOptions(limitOptionsBuilder.toOptions());

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
