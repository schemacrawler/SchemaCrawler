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

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.newSchemaRetrievalOptions;

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
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
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

      final SchemaRetrievalOptions schemaRetrievalOptions = newSchemaRetrievalOptions();

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
          out.println(String.format("- Table %s%n%s", table.getFullName(), table.getRemarks()));
          for (final Column column : table.getColumns()) {
            out.println(
                String.format("-- Column %s%n%s", column.getFullName(), column.getRemarks()));
          }
          out.println();
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
