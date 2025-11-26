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
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.integration.utility.PostgreSQLTestUtility.newPostgreSQLContainer;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.database.SqlScript;

@DisableLogging
@ResolveTestContext
@HeavyDatabaseTest("postgresql")
@Testcontainers(disabledWithoutDocker = true)
public class AcrossDatabaseDataTypeTest extends BaseAdditionalDatabaseTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newPostgreSQLContainer();

  private static String printColumnDataType(final ColumnDataType columnDataType) {
    final StringBuilder buffer = new StringBuilder();

    final boolean isUserDefined = columnDataType.getType() == user_defined;
    final String key = columnDataType.key().toString();
    final String name = columnDataType.getFullName();
    final String dataType = columnDataType.getType().toString();
    final String javaSqlType = "java.sql.Types: " + columnDataType.getStandardTypeName();

    buffer
        .append(key)
        .append("\n")
        .append(name)
        .append("\n")
        .append("  ")
        .append(dataType)
        .append("\n")
        .append("  ")
        .append(javaSqlType)
        .append("\n");

    if (isUserDefined) {
      final String baseTypeName;
      final ColumnDataType baseColumnDataType = columnDataType.getBaseType();
      if (baseColumnDataType == null) {
        baseTypeName = "";
      } else {
        baseTypeName = baseColumnDataType.getFullName();
      }
      buffer.append("  ").append("based on ").append(baseTypeName).append("\n");
    }

    return buffer.toString();
  }

  @Test
  public void acrossDatabaseDataType(final TestContext testContext) throws Exception {

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(schema -> Arrays.asList("schema1", "schema2").contains(schema));
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder =
        SchemaInfoLevelBuilder.builder()
            .withTag("maximum-without-grants")
            .withInfoLevel(InfoLevel.maximum)
            .setRetrieveTablePrivileges(false)
            .setRetrieveTableColumnPrivileges(false);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(schemaInfoLevelBuilder.toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Catalog catalog = getCatalog(getDataSource(), schemaCrawlerOptions);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final List<ColumnDataType> columnDataTypes =
          (List<ColumnDataType>) catalog.getColumnDataTypes();
      Collections.sort(columnDataTypes, NamedObjectSort.alphabetical);
      for (final ColumnDataType columnDataType : columnDataTypes) {
        out.println(printColumnDataType(columnDataType));
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @BeforeEach
  public void createDatabase() throws SQLException {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    createDataSource(
        dbContainer.getJdbcUrl(), dbContainer.getUsername(), dbContainer.getPassword());

    // Note: The database connection needs to be closed for the new schemas to be recognized
    try (Connection connection = getConnection()) {
      SqlScript.executeScriptFromResource("/across-database-data-type.sql", connection);
    }
  }
}
