/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tablesRetrievalStrategy;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.sql.SQLException;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableTypes;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ResolveTestContext
public class TableRetrieverTest extends AbstractRetrieverTest {

  @Test
  @DisplayName("Test with empty result set")
  public void emptyResultSet(final DatabaseConnectionSource dataSource) throws Exception {
    final RetrieverConnection retrieverConnection =
        createRetrieverConnection(
            dataSource,
            InformationSchemaKey.TABLES,
            "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE 1=0",
            data_dictionary_all);

    final TableRetriever tableRetriever =
        new TableRetriever(retrieverConnection, catalog, createOptions());
    tableRetriever.retrieveTables("", TableTypes.from("TABLE", "VIEW"), new IncludeAll());

    // Verify that no new tables were added to the catalog
    assertThat(catalog.getTables(), is(empty()));
  }

  @Test
  @DisplayName("Test handling of invalid SQL in table retrieval")
  public void handlingOfInvalidSQLInTableRetrieval(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Use invalid SQL that will cause a SQL exception
    final RetrieverConnection retrieverConnection =
        createRetrieverConnection(
            dataSource, InformationSchemaKey.TABLES, "THIS IS NOT VALID SQL", data_dictionary_all);

    final TableRetriever tableRetriever =
        new TableRetriever(retrieverConnection, catalog, createOptions());

    // Verify that the retriever handles SQL exceptions gracefully
    final SQLException sqlException =
        assertThrows(
            SQLException.class,
            () ->
                tableRetriever.retrieveTables(
                    "", TableTypes.from("TABLE", "VIEW"), new IncludeAll()));
    assertThat(sqlException.getCause().getMessage(), is("unexpected token: THIS"));
  }

  @Test
  @DisplayName("Test with malformed data in result sets")
  public void malformedDataInResultSets(final DatabaseConnectionSource dataSource)
      throws Exception {
    final RetrieverConnection retrieverConnection =
        createRetrieverConnection(
            dataSource,
            InformationSchemaKey.TABLES,
            """
            SELECT
              'INVALID_CAT' AS TABLE_CAT,
              'INVALID_SCHEMA' AS TABLE_SCHEM,
              '' AS TABLE_NAME,
              'INVALID_TYPE' AS TABLE_TYPE,
              'Test remarks' AS REMARKS
            FROM
              (VALUES(0))
            """,
            data_dictionary_all);

    final TableRetriever tableRetriever =
        new TableRetriever(retrieverConnection, catalog, createOptions());
    // Should handle empty table name gracefully
    tableRetriever.retrieveTables("", TableTypes.from("TABLE", "VIEW"), new IncludeAll());

    // Verify that no tables with empty names were added
    boolean foundEmptyNameTable = false;
    for (final Table table : catalog.getTables()) {
      if (table.getName().isEmpty()) {
        foundEmptyNameTable = true;
        break;
      }
    }
    assertThat("Table with empty name should not be added", foundEmptyNameTable, is(false));
  }

  @Test
  @DisplayName("Retrieve tables from data dictionary")
  public void tablesFromDataDictionary(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final RetrieverConnection retrieverConnection =
        createRetrieverConnection(
            dataSource,
            InformationSchemaKey.TABLES,
            "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES",
            data_dictionary_all);

    final TableRetriever tableRetriever =
        new TableRetriever(retrieverConnection, catalog, createOptions());
    tableRetriever.retrieveTables("", TableTypes.from("TABLE", "VIEW"), new IncludeAll());

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println("%s [%s]".formatted(table.getFullName(), table.getTableType()));
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Override
  protected void customizeSchemaInfoLevel(final SchemaInfoLevelBuilder schemaInfoLevelBuilder) {
    schemaInfoLevelBuilder.setRetrieveTables(false);
  }

  @Override
  protected SchemaInfoMetadataRetrievalStrategy getRetrievalStrategyKey() {
    return tablesRetrievalStrategy;
  }
}
