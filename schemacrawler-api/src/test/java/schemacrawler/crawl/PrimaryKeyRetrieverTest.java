/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.primaryKeysRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrimaryKeyRetrieverTest {

  public static void verifyRetrievePrimaryKeys(final Catalog catalog) throws IOException {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println(table.getFullName());
          if (table.hasPrimaryKey()) {
            final PrimaryKey primaryKey = table.getPrimaryKey();
            out.println(String.format("  primary key: %s", primaryKey.getName()));
            out.println(String.format("    columns: %s", primaryKey.getConstrainedColumns()));
            out.println(String.format("    constraint type: %s", primaryKey.getType()));
            out.println(String.format("    is deferrable: %b", primaryKey.isDeferrable()));
            out.println(
                String.format("    is initially deferred: %b", primaryKey.isInitiallyDeferred()));
          }
        }
      }
    }
    // IMPORTANT: The data dictionary should return the same information as the metadata test
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource("SchemaCrawlerTest.primaryKeys")));
  }

  private MutableCatalog catalog;

  @BeforeAll
  public void loadBaseCatalog(final Connection connection) {
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.minimum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions());

    catalog =
        (MutableCatalog)
            getCatalog(connection, schemaRetrievalOptionsDefault, schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables) {
      assertThat(table.getIndexes(), is(empty()));
      assertThat(table.getPrimaryKey(), is(nullValue()));
    }
  }

  @Test
  @DisplayName("Retrieve primary keys from data dictionary")
  public void primaryKeysFromDataDictionary(final DatabaseConnectionSource dataSource)
      throws Exception {
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.PRIMARY_KEYS,
                "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_PRIMARYKEYS")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .with(primaryKeysRetrievalStrategy, data_dictionary_all)
        .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final PrimaryKeyRetriever primaryKeyRetriever =
        new PrimaryKeyRetriever(retrieverConnection, catalog, options);
    primaryKeyRetriever.retrievePrimaryKeys(catalog.getAllTables());

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables) {
      if (!Arrays.asList(
              "Global Counts",
              "AUTHORSLIST",
              "BOOKAUTHORS",
              "PUBLICATIONWRITERS",
              "SALES",
              "SALESDATA")
          .contains(table.getName())) {
        assertThat(
            "Did not find primary key for " + table.getFullName(),
            table.getPrimaryKey(),
            is(not(nullValue())));
      }
    }
  }
}
