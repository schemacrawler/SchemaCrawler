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
import static schemacrawler.integration.test.utility.MySQLTestUtility.newMySQLContainer;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.database.SqlScript;

@DisableLogging
@HeavyDatabaseTest("mysql")
@Testcontainers
@ResolveTestContext
public class DuplicateNamesTest extends BaseAdditionalDatabaseTest {

  @Container
  private static final JdbcDatabaseContainer<?> dbContainer =
      newMySQLContainer().withUsername("schemacrawler");

  @BeforeEach
  public void createDatabase() throws SQLException {

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

    // Note: The database connection needs to be closed for the new schemas to be recognized
    try (final Connection connection = getConnection()) {
      SqlScript.executeScriptFromResource("/duplicate_names.sql", connection);
    }
  }

  @Test
  @DisplayName("Issue #2064 - Verify that constraint names are scoped within a table")
  public void dupeNames(final TestContext testContext) throws Exception {

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(schema -> Arrays.asList("test").contains(schema));
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder =
        SchemaInfoLevelBuilder.builder().withInfoLevel(InfoLevel.maximum);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(schemaInfoLevelBuilder.toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Catalog catalog = SchemaCrawlerUtility.getCatalog(getDataSource(), schemaCrawlerOptions);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      for (final Table table : catalog.getTables()) {
        out.println(String.format("- [table] %s", table.getFullName()));
        for (final TableConstraint tableConstraint : table.getTableConstraints()) {
          out.println(
              String.format("  - [%s] %s", tableConstraint.getType(), tableConstraint.key()));
          for (final TableConstraintColumn tableConstraintColumn :
              tableConstraint.getConstrainedColumns()) {
            out.println(String.format("    - [column] %s", tableConstraintColumn.getFullName()));
          }
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
