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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_INDEXES;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.metadata;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
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
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class IndexRetrieverTest {

  public static void verifyRetrieveIndexes(final Catalog catalog) throws IOException {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println(table.getFullName());
          final Collection<Index> indexes = table.getIndexes();
          for (final Index index : indexes) {
            out.println("  index: %s".formatted(index.getName()));
            out.println("    columns: %s".formatted(index.getColumns()));
            out.println("    is unique: %b".formatted(index.isUnique()));
            out.println("    cardinality: %d".formatted(index.getCardinality()));
            out.println("    pages: %d".formatted(index.getPages()));
            out.println("    index type: %s".formatted(index.getIndexType()));
          }
        }
      }
    }
    // IMPORTANT: The data dictionary should return the same information as the metadata test
    assertThat(outputOf(testout), hasSameContentAs(classpathResource("SchemaCrawlerTest.indexes")));
  }

  private MutableCatalog catalog;

  @Test
  @DisplayName("Test handling of edge cases in index retrieval")
  public void edgeCasesInIndexRetrieval(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    assertThat("Should still have indexes after edge case test", countAllIndexes(), is(0));

    // First, populate the catalog with indexes
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.with(indexesRetrievalStrategy, metadata);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);
    indexRetriever.retrieveIndexes(catalog.getAllTables());

    // Now test retrieveIndexInformation with a SQL that returns non-existent tables and indexes
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                EXT_INDEXES,
                """
                SELECT
                  'NON_EXISTENT_CATALOG' AS INDEX_CATALOG,
                  'NON_EXISTENT_SCHEMA' AS INDEX_SCHEMA,
                  'NON_EXISTENT_TABLE' AS TABLE_NAME,
                  'NON_EXISTENT_INDEX' AS INDEX_NAME,
                  'Test remarks' AS REMARKS,
                  'Test definition' AS INDEX_DEFINITION
                FROM
                  INFORMATION_SCHEMA.SYSTEM_TABLES
                WHERE
                  1=1
                """)
            .toOptions();
    final SchemaRetrievalOptionsBuilder edgeCaseOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    edgeCaseOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions edgeCaseOptions = edgeCaseOptionsBuilder.toOptions();
    final RetrieverConnection edgeCaseConnection =
        new RetrieverConnection(dataSource, edgeCaseOptions);

    final IndexRetriever edgeCaseRetriever =
        new IndexRetriever(edgeCaseConnection, catalog, options);

    // The method should handle non-existent tables and indexes gracefully
    assertDoesNotThrow(() -> edgeCaseRetriever.retrieveIndexInformation());

    assertThat("Should have indexes after edge case test", countAllIndexes(), is(23));
  }

  private int countAllIndexes() {
    // Verify that the original indexes are still intact
    int count = 0;
    for (final Table table : catalog.getTables()) {
      count = count + table.getIndexes().size();
    }
    return count;
  }

  @Test
  @DisplayName("Test error handling in retrieveIndexInformation")
  public void errorHandlingInRetrieveIndexInformation(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    // Create a retriever connection with invalid SQL to simulate errors
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(EXT_INDEXES, "SELECT NOTHING FROM NO_TABLE")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);

    // The method should handle exceptions gracefully and not throw
    assertDoesNotThrow(() -> indexRetriever.retrieveIndexInformation());
    assertThat("Should not have indexes after test", countAllIndexes(), is(0));
  }

  @Test
  @DisplayName("Retrieve indexes from data dictionary")
  public void indexesFromDataDictionary(final DatabaseConnectionSource dataSource)
      throws Exception {
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.INDEXES, "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .with(indexesRetrievalStrategy, data_dictionary_all)
        .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);
    indexRetriever.retrieveIndexes(catalog.getAllTables());

    verifyRetrieveIndexes(catalog);
  }

  @Test
  @DisplayName("Retrieve indexes from metadata")
  public void indexesFromMetadata(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.with(indexesRetrievalStrategy, metadata);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);
    indexRetriever.retrieveIndexes(catalog.getAllTables());

    // Verify that indexes were retrieved
    boolean foundIndexes = false;
    for (final Table table : catalog.getTables()) {
      if (!table.getIndexes().isEmpty()) {
        foundIndexes = true;
        break;
      }
    }
    assertThat("Should find at least one index", foundIndexes, is(true));

    // Verify the retrieved indexes match the expected output
    verifyRetrieveIndexes(catalog);
  }

  @Test
  @DisplayName("Retrieve index definitions from INFORMATION_SCHEMA")
  public void indexInfo(final DatabaseConnectionSource dataSource) throws Exception {

    final String remarks = "TEST Index remarks";
    final String definition = "TEST Index definition";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                EXT_INDEXES,
                """
                SELECT DISTINCT
                  TABLE_CAT AS INDEX_CATALOG,
                  TABLE_SCHEM AS INDEX_SCHEMA,
                  TABLE_NAME,
                  INDEX_NAME,
                  '%s' AS REMARKS,
                  '%s' AS INDEX_DEFINITION
                FROM
                  INFORMATION_SCHEMA.SYSTEM_INDEXINFO
                """
                    .formatted(remarks, definition))
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);
    indexRetriever.retrieveIndexInformation();

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(14));
    for (final Table table : tables) {
      for (final Index index : table.getIndexes()) {
        assertThat(index.getRemarks(), is(remarks));
        assertThat(index.getDefinition(), is(definition));
      }
    }
  }

  @BeforeEach
  public void loadBaseCatalog(final Connection connection) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.minimum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());
    catalog = (MutableCatalog) getCatalog(connection, schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(14));
    for (final Table table : tables) {
      assertThat(table.getIndexes(), is(empty()));
      assertThat(table.getPrimaryKey(), is(nullValue()));
    }
  }

  @Test
  @DisplayName("Test error handling in retrieveIndexInformation")
  public void missingExtIndexes(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    // Create a retriever connection with invalid SQL to simulate errors
    // No EXT_INDEXES in InformationSchemaViews
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder().toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final IndexRetriever indexRetriever = new IndexRetriever(retrieverConnection, catalog, options);

    // The method should handle exceptions gracefully and not throw
    assertDoesNotThrow(() -> indexRetriever.retrieveIndexInformation());
  }
}
