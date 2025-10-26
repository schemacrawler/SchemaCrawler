/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.matchSchemaRetrievalOptions;
import static us.fatehi.test.integration.utility.MySQLTestUtility.newMySQLContainer;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.database.SqlScript;

@DisableLogging
@HeavyDatabaseTest("mysql")
@Testcontainers
@ResolveTestContext
public class AdditionalMySQLTest extends BaseAdditionalDatabaseTest {

  @Container
  private static final JdbcDatabaseContainer<?> dbContainer =
      newMySQLContainer().withUsername("schemacrawler");

  @Test
  public void columnWithEnum(final TestContext testContext) throws Exception {

    try (final Connection connection = getConnection();
        final Statement stmt = connection.createStatement(); ) {
      stmt.execute("CREATE TABLE shirts (name VARCHAR(40), size ENUM('small', 'medium', 'large'))");
      // Auto-commited
    }

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(schema -> Arrays.asList("test").contains(schema));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        schemaCrawlerOptionsWithMaximumSchemaInfoLevel.withLimitOptions(
            limitOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    assertThat(
        outputOf(executableExecution(getDataSource(), executable)),
        hasSameContentAs(classpathResource(testContext.testMethodFullName())));

    // Additional programmatic test
    final Catalog catalog = executable.getCatalog();
    final Schema schema = catalog.lookupSchema("test").orElse(null);
    assertThat(schema, notNullValue());
    final Table table = catalog.lookupTable(schema, "shirts").orElse(null);
    assertThat(table, notNullValue());

    final Column nameColumn = table.lookupColumn("name").orElse(null);
    assertThat(nameColumn, notNullValue());
    final List<String> nameEnumValues = nameColumn.getColumnDataType().getEnumValues();
    assertThat(nameEnumValues, is(empty()));

    final Column sizeColumn = table.lookupColumn("size").orElse(null);
    assertThat(sizeColumn, notNullValue());
    final List<String> enumValues = sizeColumn.getColumnDataType().getEnumValues();
    assertThat(enumValues, containsInAnyOrder("small", "medium", "large"));
  }

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    // IMPORTANT: Use root user, since permissions are not granted to the new databases created by
    // the script. Also do not verify the server, and allow public key retrieval
    final String connectionUrl =
        dbContainer.getJdbcUrl()
            + "?verifyServerCertificate=false"
            + "&allowPublicKeyRetrieval=true";
    createDataSource(connectionUrl, "root", dbContainer.getPassword());
  }

  @Test
  @DisplayName("Issue #252 - Retrieve table and columns names with a dot in them")
  public void dotName() throws Exception {

    try (final Connection connection = getConnection();
        final Statement stmt = connection.createStatement(); ) {
      stmt.execute("CREATE TABLE `test.abc` (`a.b` INT(11) DEFAULT NULL)");
      // Auto-commited
    }

    final SchemaCrawlerOptions schemaCrawlerOptions =
        schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder()
            .fromOptions(matchSchemaRetrievalOptions(getDataSource()))
            .with(tableColumnsRetrievalStrategy, data_dictionary_all);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(getDataSource(), schemaRetrievalOptions, schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();

    final Schema schema = catalog.lookupSchema("test").orElse(null);
    assertThat(schema, notNullValue());
    final Table table = catalog.lookupTable(schema, "test.abc").orElse(null);
    assertThat(table, notNullValue());
    final Column column = table.lookupColumn("a.b").orElse(null);
    assertThat(column, notNullValue());
  }

  @Test
  @DisplayName("Issue #2065 - Verify that constraint names are scoped within a table")
  public void duplicateNames(final TestContext testContext) throws Exception {

    // Note: The database connection needs to be closed for the new schemas to be recognized
    try (final Connection connection = getConnection()) {
      SqlScript.executeScriptFromResource("/duplicate_names.sql", connection);
      // Auto-commited
    }

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(schema -> Arrays.asList("duplicate_names").contains(schema));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        schemaCrawlerOptionsWithMaximumSchemaInfoLevel.withLimitOptions(
            limitOptionsBuilder.toOptions());

    final Catalog catalog = SchemaCrawlerUtility.getCatalog(getDataSource(), schemaCrawlerOptions);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      for (final Table table : catalog.getTables()) {
        out.println("- [table] %s".formatted(table.getFullName()));
        for (final TableConstraint tableConstraint : table.getTableConstraints()) {
          out.println("  - [%s] %s".formatted(tableConstraint.getType(), tableConstraint.key()));
          for (final TableConstraintColumn tableConstraintColumn :
              tableConstraint.getConstrainedColumns()) {
            out.println("    - [column] %s".formatted(tableConstraintColumn.getFullName()));
          }
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
