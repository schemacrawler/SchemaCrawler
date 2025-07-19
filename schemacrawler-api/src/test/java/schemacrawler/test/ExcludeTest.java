/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.sql.Connection;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;

@WithTestDatabase
@ResolveTestContext
public class ExcludeTest {

  private static final Logger LOGGER = Logger.getLogger(ExcludeTest.class.getName());

  @Test
  public void excludeColumns(final TestContext testContext, final Connection connection)
      throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final LimitOptionsBuilder limitOptionsBuilder =
          LimitOptionsBuilder.builder()
              .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
              .includeColumns(new RegularExpressionExclusionRule(".*\\..*\\.ID"));
      final SchemaCrawlerOptions schemaCrawlerOptions =
          SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
              .withLimitOptions(limitOptionsBuilder.toOptions());

      final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column : columns) {
            LOGGER.log(Level.FINE, column.toString());
            out.println("    column: " + column.getFullName());
            out.println(
                "      database type: " + column.getColumnDataType().getDatabaseSpecificTypeName());
            out.println("      type: " + column.getColumnDataType().getStandardTypeName());
          }
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
