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
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tablesRetrievalStrategy;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
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
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ResolveTestContext
public class TableRetrieverTest extends AbstractRetrieverTest {

  @Override
  protected SchemaInfoMetadataRetrievalStrategy getRetrievalStrategyKey() {
    return tablesRetrievalStrategy;
  }

  @Override
  protected void customizeSchemaInfoLevel(final SchemaInfoLevelBuilder schemaInfoLevelBuilder) {
    schemaInfoLevelBuilder.setRetrieveTables(false);
  }

  @Test
  @DisplayName("Test with empty result set")
  public void emptyResultSet(final DatabaseConnectionSource dataSource) throws Exception {
    final RetrieverConnection retrieverConnection = createRetrieverConnection(
        dataSource,
        InformationSchemaKey.TABLES,
        "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE 1=0",
        data_dictionary_all);

    final TableRetriever tableRetriever = new TableRetriever(retrieverConnection, catalog, createOptions());
    tableRetriever.retrieveTables("", TableTypes.from("TABLE", "VIEW"), new IncludeAll());

    // Verify that no new tables were added to the catalog
    assertThat(catalog.getTables(), is(empty()));
  }

  @Test
  @DisplayName("Test handling of invalid SQL in table retrieval")
  public void handlingOfInvalidSQLInTableRetrieval(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Use invalid SQL that will cause a SQL exception
    final RetrieverConnection retrieverConnection = createRetrieverConnection(
        dataSource,
        InformationSchemaKey.TABLES,
        "THIS IS NOT VALID SQL",
        data_dictionary_all);

    final TableRetriever tableRetriever = new TableRetriever(retrieverConnection, catalog, createOptions());

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
    final String sql = "SELECT 'INVALID_CAT' AS TABLE_CAT, 'INVALID_SCHEMA' AS TABLE_SCHEM, '' AS TABLE_NAME,"
        + " 'INVALID_TYPE' AS TABLE_TYPE, 'Test remarks' AS REMARKS FROM (VALUES(0))";

    final RetrieverConnection retrieverConnection = createRetrieverConnection(
        dataSource,
        InformationSchemaKey.TABLES,
        sql,
        data_dictionary_all);

    final TableRetriever tableRetriever = new TableRetriever(retrieverConnection, catalog, createOptions());
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
    final RetrieverConnection retrieverConnection = createRetrieverConnection(
        dataSource,
        InformationSchemaKey.TABLES,
        "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES",
        data_dictionary_all);

    final TableRetriever tableRetriever = new TableRetriever(retrieverConnection, catalog, createOptions());
    tableRetriever.retrieveTables("", TableTypes.from("TABLE", "VIEW"), new IncludeAll());

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println(String.format("%s [%s]", table.getFullName(), table.getTableType()));
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
