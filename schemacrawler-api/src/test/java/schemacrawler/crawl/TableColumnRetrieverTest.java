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
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InfoLevel;
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
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableColumnRetrieverTest {

  public static void verifyRetrieveTableColumns(final Catalog catalog) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column : columns) {
            out.println(String.format("%s", column.getFullName()));

            out.println(String.format("  - %s=%s", "short name", column.getShortName()));
            out.println(String.format("  - %s=%s", "data-type", column.getColumnDataType()));
            out.println(String.format("  - %s=%s", "size", column.getSize()));
            out.println(String.format("  - %s=%s", "decimal digits", column.getDecimalDigits()));
            out.println(String.format("  - %s=%s", "width", column.getWidth()));
            out.println(String.format("  - %s=%s", "default value", column.getDefaultValue()));
            out.println(String.format("  - %s=%s", "auto-incremented", column.isAutoIncremented()));
            out.println(String.format("  - %s=%s", "nullable", column.isNullable()));
            out.println(String.format("  - %s=%s", "generated", column.isGenerated()));
            out.println(String.format("  - %s=%s", "hidden", column.isHidden()));
            out.println(
                String.format("  - %s=%s", "part of primary key", column.isPartOfPrimaryKey()));
            out.println(
                String.format("  - %s=%s", "part of foreign key", column.isPartOfForeignKey()));
            out.println(
                String.format("  - %s=%s", "ordinal position", column.getOrdinalPosition()));
            out.println(String.format("  - %s=%s", "remarks", column.getRemarks()));

            out.println(String.format("  - %s=%s", "attibutes", ""));
            final SortedMap<String, Object> columnAttributes =
                new TreeMap<>(column.getAttributes());
            for (final Entry<String, Object> columnAttribute : columnAttributes.entrySet()) {
              out.println(
                  String.format(
                      "    ~ %s=%s", columnAttribute.getKey(), columnAttribute.getValue()));
            }

            assertThat(column.getType(), is(column.getColumnDataType()));
          }

          out.println();
        }
      }
    }
    // IMPORTANT: The data dictionary test should return the same information as the metadata test
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource("SchemaCrawlerTest.tableColumns")));
  }

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve hidden table columns from data dictionary")
  public void hiddenTableColumns(final DatabaseConnectionSource dataSource) throws Exception {
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.TABLE_COLUMNS,
                IOUtility.readResourceFully("/TABLE_COLUMNS.sql"))
            .withSql(
                InformationSchemaKey.EXT_HIDDEN_TABLE_COLUMNS,
                IOUtility.readResourceFully("/EXT_HIDDEN_TABLE_COLUMNS.sql"))
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .with(tableColumnsRetrievalStrategy, data_dictionary_all)
        .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableColumnRetriever tableColumnRetriever =
        new TableColumnRetriever(retrieverConnection, catalog, options);
    tableColumnRetriever.retrieveTableColumns(catalog.getAllTables(), new IncludeAll());

    int columnCount = 0;
    int hiddenColumnCount = 0;
    for (final Table table : catalog.getTables()) {
      final Column[] columns = table.getColumns().toArray(new Column[0]);
      for (final Column column : columns) {
        columnCount = columnCount + 1;
        if (column.isHidden()) {
          hiddenColumnCount = hiddenColumnCount + 1;
        }
      }
    }
    assertThat(columnCount, is(53));
    assertThat(hiddenColumnCount, is(1));
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            .withSchemaInfoLevel(
                SchemaInfoLevelBuilder.builder()
                    .withInfoLevel(InfoLevel.standard)
                    .setRetrieveTableColumns(false)
                    .setRetrieveForeignKeys(false)
                    .toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());
    catalog =
        (MutableCatalog)
            getCatalog(connection, schemaRetrievalOptionsDefault, schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(13));
    for (final Table table : tables) {
      assertThat(table.getColumns(), is(empty()));
      assertThat(table.getForeignKeys(), is(empty()));
      assertThat(table.getPrimaryKey(), is(nullValue()));
    }
  }

  @Test
  @DisplayName("Retrieve table columns from data dictionary")
  public void tableColumnsFromDataDictionary(final DatabaseConnectionSource dataSource)
      throws Exception {
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.TABLE_COLUMNS,
                IOUtility.readResourceFully("/TABLE_COLUMNS.sql"))
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .with(tableColumnsRetrievalStrategy, data_dictionary_all)
        .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableColumnRetriever tableColumnRetriever =
        new TableColumnRetriever(retrieverConnection, catalog, options);
    tableColumnRetriever.retrieveTableColumns(catalog.getAllTables(), new IncludeAll());

    // Fix foreign-keys in the original catalog
    final ForeignKeyRetriever foreignKeyRetriever =
        new ForeignKeyRetriever(retrieverConnection, catalog, options);
    foreignKeyRetriever.retrieveForeignKeys(catalog.getAllTables());
    final PrimaryKeyRetriever primaryKeyRetriever =
        new PrimaryKeyRetriever(retrieverConnection, catalog, options);
    primaryKeyRetriever.retrievePrimaryKeys(catalog.getAllTables());

    verifyRetrieveTableColumns(catalog);
  }
}
