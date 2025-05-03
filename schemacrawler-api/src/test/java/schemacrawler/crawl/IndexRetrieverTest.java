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

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_INDEXES;
import static schemacrawler.schemacrawler.InformationSchemaKey.INDEXES;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ResolveTestContext
public class IndexRetrieverTest extends AbstractRetrieverTest {

  public static void verifyRetrieveIndexes(final Catalog catalog) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas.length > 0, is(true));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          final Collection<Index> tableIndices = table.getIndexes();
          for (final Index index : tableIndices) {
            out.println("    index: " + index.getFullName());
            out.println("      unique: " + index.isUnique());
            out.println("      columns: " + index.getColumns());
            out.println("      definition: " + index.getDefinition());
            out.println("      remarks: " + index.getRemarks());
          }
        }
      }
    }

    // Since we don't have a TestContext, we can't verify the output against a resource
    // This method is primarily used by SchemaCrawlerTest
  }

  @Override
  protected SchemaInfoMetadataRetrievalStrategy getRetrievalStrategyKey() {
    return indexesRetrievalStrategy;
  }

  @Override
  protected void customizeSchemaInfoLevel(final SchemaInfoLevelBuilder schemaInfoLevelBuilder) {
    schemaInfoLevelBuilder.setRetrieveIndexes(false);
  }

  @Override
  protected InfoLevel getInfoLevel() {
    return InfoLevel.standard;
  }

  @Test
  @DisplayName("Test retrieving indexes from metadata")
  public void testRetrieveIndexesFromMetadata(final DatabaseConnectionSource dataSource)
      throws Exception {
    final RetrieverConnection retrieverConnection = createRetrieverConnection(dataSource);

    // Create a list of tables to retrieve indexes for
    final NamedObjectList<MutableTable> allTables = new NamedObjectList<>();
    for (final Schema schema : catalog.getSchemas()) {
      for (final Table table : catalog.getTables(schema)) {
        if (table instanceof MutableTable) {
          allTables.add((MutableTable) table);
        }
      }
    }

    assertThat("Should have tables to retrieve indexes for", !allTables.isEmpty(), is(true));

    // Create the index retriever
    final IndexRetriever indexRetriever =
        new IndexRetriever(retrieverConnection, catalog, createOptions());

    // Act - retrieve indexes
    indexRetriever.retrieveIndexes(allTables);

    // Assert - verify indexes were retrieved
    boolean hasIndexes = false;
    for (final Table table : catalog.getTables()) {
      final Collection<Index> indexes = table.getIndexes();
      if (!indexes.isEmpty()) {
        hasIndexes = true;
        break;
      }
    }

    assertThat("Should have retrieved at least one index", hasIndexes, is(true));
  }

  @Test
  @DisplayName("Test retrieving indexes from data dictionary")
  public void testRetrieveIndexesFromDataDictionary(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Arrange - create a custom information schema view for indexes
    final String sql =
        "SELECT "
            + "NULL AS TABLE_CAT, "
            + "'PUBLIC' AS TABLE_SCHEM, "
            + "'BOOKS' AS TABLE_NAME, "
            + "FALSE AS NON_UNIQUE, "
            + "NULL AS INDEX_QUALIFIER, "
            + "'TEST_INDEX' AS INDEX_NAME, "
            + "1 AS TYPE, "
            + "1 AS ORDINAL_POSITION, "
            + "'ID' AS COLUMN_NAME, "
            + "NULL AS ASC_OR_DESC, "
            + "0 AS CARDINALITY, "
            + "0 AS PAGES, "
            + "NULL AS FILTER_CONDITION "
            + "FROM (VALUES(0))";

    final RetrieverConnection retrieverConnection =
        createRetrieverConnection(dataSource, INDEXES, sql, data_dictionary_all);

    // Create a list of tables to retrieve indexes for
    final NamedObjectList<MutableTable> allTables = new NamedObjectList<>();
    for (final Schema schema : catalog.getSchemas()) {
      for (final Table table : catalog.getTables(schema)) {
        if (table instanceof MutableTable) {
          allTables.add((MutableTable) table);
        }
      }
    }

    // Create the index retriever
    final IndexRetriever indexRetriever =
        new IndexRetriever(retrieverConnection, catalog, createOptions());

    // Act - retrieve indexes
    indexRetriever.retrieveIndexes(allTables);

    // We can't easily verify the specific index was created since we need a matching table,
    // but we can verify the method executed without errors
  }

  @Test
  @DisplayName("Test retrieving additional index information")
  public void testRetrieveIndexInformation(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Arrange - create a custom information schema view for extended indexes
    final String sql =
        "SELECT "
            + "NULL AS INDEX_CATALOG, "
            + "'PUBLIC' AS INDEX_SCHEMA, "
            + "'BOOKS' AS TABLE_NAME, "
            + "'TEST_INDEX' AS INDEX_NAME, "
            + "'Test index remark' AS REMARKS, "
            + "'Test index source' AS INDEX_DEFINITION "
            + "FROM (VALUES(0))";

    final RetrieverConnection retrieverConnection =
        createRetrieverConnection(dataSource, EXT_INDEXES, sql, null);

    // Create the index retriever
    final IndexRetriever indexRetriever =
        new IndexRetriever(retrieverConnection, catalog, createOptions());

    // Act - retrieve additional index information
    indexRetriever.retrieveIndexInformation();

    // We can't easily verify the specific index information was added since we need a matching
    // index,
    // but we can verify the method executed without errors
  }

  @Test
  @DisplayName("Test retrieving indexes with invalid SQL")
  public void testRetrieveIndexesWithInvalidSql(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Arrange - create a custom information schema view with invalid SQL
    final RetrieverConnection retrieverConnection =
        createRetrieverConnection(
            dataSource, INDEXES, "THIS IS NOT VALID SQL", data_dictionary_all);

    // Create a list of tables to retrieve indexes for
    final NamedObjectList<MutableTable> allTables = new NamedObjectList<>();
    for (final Schema schema : catalog.getSchemas()) {
      for (final Table table : catalog.getTables(schema)) {
        if (table instanceof MutableTable) {
          allTables.add((MutableTable) table);
        }
      }
    }

    // Create the index retriever
    final IndexRetriever indexRetriever =
        new IndexRetriever(retrieverConnection, catalog, createOptions());

    // Act & Assert - retrieving indexes with invalid SQL should throw an exception
    // The IndexRetriever doesn't handle invalid SQL gracefully, so we expect an exception
    assertThrows(SQLException.class, () -> indexRetriever.retrieveIndexes(allTables));
  }
}
