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
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AttributesCatalogLoaderTest {

  @Test
  public void noRemarksForExternalColumn(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final String attributesFile = "/attributes-external-column.yaml";
    showRemarks(testContext, dataSource, attributesFile);
  }

  @Test
  public void noRemarksForExternalTable(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final String attributesFile = "/attributes-external-table.yaml";
    showRemarks(testContext, dataSource, attributesFile);
  }

  @Test
  public void showRemarks(final TestContext testContext, final DatabaseConnectionSource dataSource)
      throws Exception {
    final String attributesFile = "/attributes.yaml";
    showRemarks(testContext, dataSource, attributesFile);
  }

  private void showRemarks(
      final TestContext testContext,
      final DatabaseConnectionSource dataSource,
      final String attributesFile)
      throws IOException {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {

      final SchemaRetrievalOptions schemaRetrievalOptions =
          DatabaseTestUtility.newSchemaRetrievalOptions();

      final SchemaCrawlerOptions schemaCrawlerOptions =
          SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

      final Config additionalConfig = new Config();
      additionalConfig.put("attributes-file", attributesFile);

      final Catalog catalog =
          SchemaCrawlerUtility.getCatalog(
              dataSource, schemaRetrievalOptions, schemaCrawlerOptions, additionalConfig);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(6));
      for (final Schema schema : schemas) {
        for (final Table table : catalog.getTables(schema)) {
          out.println("- Table %s%n%s".formatted(table.getFullName(), table.getRemarks()));
          for (final Column column : table.getColumns()) {
            out.println("-- Column %s%n%s".formatted(column.getFullName(), column.getRemarks()));
          }
          out.println();
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
