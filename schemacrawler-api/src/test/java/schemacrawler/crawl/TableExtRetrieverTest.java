/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_INDEXES;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_TABLES;
import static schemacrawler.schemacrawler.InformationSchemaKey.VIEW_TABLE_USAGE;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableExtRetrieverTest {

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve index definitions from INFORMATION_SCHEMA")
  public void indexInfo(final Connection connection) throws Exception {

    final String remarks = "TEST Index remarks";
    final String definition = "TEST Index definition";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                EXT_INDEXES,
                String.format(
                    "SELECT DISTINCT TABLE_CAT AS INDEX_CATALOG, TABLE_SCHEM AS INDEX_SCHEMA, "
                        + "TABLE_NAME, INDEX_NAME, '%s' AS REMARKS, '%s' AS INDEX_DEFINITION "
                        + "FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO",
                    remarks, definition))
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever =
        new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveIndexInformation();

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables) {
      for (final Index index : table.getIndexes()) {
        assertThat(index.getRemarks(), is(remarks));
        assertThat(index.getDefinition(), is(definition));
      }
    }
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection) throws SchemaCrawlerException {
    catalog =
        (MutableCatalog)
            getCatalog(
                connection,
                SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions(),
                SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables) {
      for (final Index index : table.getIndexes()) {
        final List<IndexColumn> columns = index.getColumns();
        assertThat(columns, is(not(empty())));
        for (final IndexColumn column : columns) {
          assertThat(column.isGenerated(), is(false));
          assertThat(column.getDefinition(), is(""));
        }
      }
    }
  }

  @Test
  @DisplayName("Retrieve table definitions from INFORMATION_SCHEMA")
  public void tableDefinitions(final Connection connection) throws Exception {

    final String definition = "TEST Table definition";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                EXT_TABLES,
                String.format(
                    "SELECT DISTINCT TABLE_CAT AS TABLE_CATALOG, TABLE_SCHEM AS TABLE_SCHEMA, "
                        + "TABLE_NAME, '%s' AS TABLE_DEFINITION "
                        + "FROM INFORMATION_SCHEMA.SYSTEM_TABLES",
                    definition))
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever =
        new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveTableDefinitions();

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(19));
    for (final Table table : tables) {
      assertThat(table.getDefinition(), is(definition));
    }
  }

  @Test
  @DisplayName("Retrieve view table usage from INFORMATION_SCHEMA")
  public void viewTableUsage(final Connection connection) throws Exception {

    int viewCount;
    final Collection<Table> tables = catalog.getTables();
    viewCount = 0;
    assertThat(tables, hasSize(19));
    for (final Table table : tables) {
      if (!(table instanceof View)) {
        continue;
      }
      viewCount = viewCount + 1;
      final View view = (View) table;
      assertThat(view.getTableUsage(), is(empty()));
    }
    assertThat(viewCount, is(1));

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                VIEW_TABLE_USAGE,
                "SELECT "
                    + "VIEW_CATALOG, VIEW_SCHEMA, VIEW_NAME, "
                    + "TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME "
                    + "FROM INFORMATION_SCHEMA.VIEW_TABLE_USAGE")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever =
        new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveViewTableUsage();

    viewCount = 0;
    assertThat(tables, hasSize(19));
    for (final Table table : tables) {
      if (!(table instanceof View)) {
        continue;
      }
      viewCount = viewCount + 1;
      final View view = (View) table;
      assertThat(view.getTableUsage(), is(not(empty())));
    }
    assertThat(viewCount, is(1));
  }
}
