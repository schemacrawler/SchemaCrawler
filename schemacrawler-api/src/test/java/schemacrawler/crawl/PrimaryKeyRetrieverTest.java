/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

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
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
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
            out.println("  primary key: %s".formatted(primaryKey.getName()));
            out.println("    columns: %s".formatted(primaryKey.getConstrainedColumns()));
            out.println("    constraint type: %s".formatted(primaryKey.getType()));
            out.println("    is deferrable: %b".formatted(primaryKey.isDeferrable()));
            out.println(
                "    is initially deferred: %b".formatted(primaryKey.isInitiallyDeferred()));
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

    catalog = (MutableCatalog) getCatalog(connection, schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(20));
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
    assertThat(tables, hasSize(20));
    for (final Table table : tables) {
      if (!Arrays.asList(
              "AUTHORSLIST",
              "BOOKAUTHORS",
              "Global Counts",
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
