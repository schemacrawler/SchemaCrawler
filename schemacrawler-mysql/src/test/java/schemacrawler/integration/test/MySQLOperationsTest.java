/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.MySQLTestUtility.newMySQLContainer;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
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
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@TestInstance(Lifecycle.PER_CLASS)
@HeavyDatabaseTest("mysql")
@Testcontainers
@ResolveTestContext
@DisplayName("Test for operations including tablesample")
public class MySQLOperationsTest extends BaseAdditionalDatabaseTest {

  @Container
  private static final JdbcDatabaseContainer<?> dbContainer =
      newMySQLContainer().withUsername("schemacrawler").withDatabaseName("books");

  @BeforeAll
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    // Use default connection properties from MySQLDatabaseConnector
    final Properties connectionProperties = new Properties();
    connectionProperties.put("nullNamePatternMatchesAll", "true");
    connectionProperties.put("getProceduresReturnsFunctions", "false");
    connectionProperties.put("noAccessToProcedureBodies", "true");
    connectionProperties.put("logger", "Jdk14Logger");
    connectionProperties.put("dumpQueriesOnException", "true");
    connectionProperties.put("dumpMetadataOnColumnNotFound", "true");
    connectionProperties.put("maxQuerySizeToLog", "4096");
    connectionProperties.put("disableMariaDbDriver", "true");
    connectionProperties.put("useInformationSchema", "true");

    final StringBuilder connectionPropertiesString = new StringBuilder();
    connectionProperties.entrySet().stream()
        .forEach(
            entry ->
                connectionPropertiesString
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(";"));

    createDataSource(
        dbContainer.getJdbcUrl(),
        dbContainer.getUsername(),
        dbContainer.getPassword(),
        connectionPropertiesString.toString());

    createDatabase("/mysql.scripts.txt");
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
