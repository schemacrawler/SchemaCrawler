/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.utility.MetaDataUtility.findForeignKeyCardinality;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.crawl.WeakAssociation;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestLoggingExtension;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.sqlite.SQLiteDatabaseConnector;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import schemacrawler.utility.NamedObjectSort;

@ExtendWith(TestLoggingExtension.class)
@ExtendWith(TestContextParameterResolver.class)
public class PrimaryKeyWeakAssociationsTest extends BaseSqliteTest {

  @Test
  public void weakAssociations1(final TestContext testContext) throws Exception {
    weakAssociations(testContext, "/pk_test_1.sql");
  }

  @Test
  public void weakAssociations2(final TestContext testContext) throws Exception {
    weakAssociations(testContext, "/pk_test_2.sql");
  }

  @Test
  public void weakAssociations3(final TestContext testContext) throws Exception {
    weakAssociations(testContext, "/pk_test_3.sql");
  }

  private void weakAssociations(final TestContext testContext, final String databaseSqlResource)
      throws Exception {
    final String currentMethodFullName = testContext.testMethodFullName();
    final Path sqliteDbFile = createTestDatabase(databaseSqlResource);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final SchemaCrawlerOptions schemaCrawlerOptions =
          DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

      final DataSource dataSource = createDataSource(sqliteDbFile);
      final Connection connection = dataSource.getConnection();

      final SchemaRetrievalOptions schemaRetrievalOptions =
          new SQLiteDatabaseConnector().getSchemaRetrievalOptionsBuilder(connection).toOptions();

      final Config config = new Config();
      config.put("weak-associations", Boolean.TRUE);

      final Catalog catalog =
          SchemaCrawlerUtility.getCatalog(
              connection, schemaRetrievalOptions, schemaCrawlerOptions, config);

      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, is(arrayWithSize(1)));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          final Collection<WeakAssociation> weakAssociations = table.getWeakAssociations();
          for (final WeakAssociation weakFk : weakAssociations) {
            out.println(
                String.format(
                    "    weak association (1 to %s):", findForeignKeyCardinality(weakFk)));
            for (final ColumnReference weakAssociationColumnReference : weakFk) {
              out.println(
                  String.format("      column reference: %s", weakAssociationColumnReference));
            }
          }
        }
      }
    }
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(currentMethodFullName)));
  }
}
