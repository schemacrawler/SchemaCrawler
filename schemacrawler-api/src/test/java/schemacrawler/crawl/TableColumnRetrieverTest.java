/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
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
import schemacrawler.schemacrawler.SchemaReference;
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
            out.println("%s".formatted(column.getFullName()));

            out.println("  - %s=%s".formatted("short name", column.getShortName()));
            out.println("  - %s=%s".formatted("data-type", column.getColumnDataType()));
            out.println("  - %s=%s".formatted("size", column.getSize()));
            out.println("  - %s=%s".formatted("decimal digits", column.getDecimalDigits()));
            out.println("  - %s=%s".formatted("width", column.getWidth()));
            out.println("  - %s=%s".formatted("default value", column.getDefaultValue()));
            out.println("  - %s=%s".formatted("auto-incremented", column.isAutoIncremented()));
            out.println("  - %s=%s".formatted("nullable", column.isNullable()));
            out.println("  - %s=%s".formatted("generated", column.isGenerated()));
            out.println("  - %s=%s".formatted("hidden", column.isHidden()));
            out.println("  - %s=%s".formatted("part of primary key", column.isPartOfPrimaryKey()));
            out.println("  - %s=%s".formatted("part of foreign key", column.isPartOfForeignKey()));
            out.println("  - %s=%s".formatted("ordinal position", column.getOrdinalPosition()));
            out.println("  - %s=%s".formatted("remarks", column.getRemarks()));

            out.println("  - %s=%s".formatted("attibutes", ""));
            final SortedMap<String, Object> columnAttributes =
                new TreeMap<>(column.getAttributes());
            for (final Entry<String, Object> columnAttribute : columnAttributes.entrySet()) {
              out.println(
                  "    ~ %s=%s".formatted(columnAttribute.getKey(), columnAttribute.getValue()));
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
  @DisplayName("Test with empty result set for columns")
  public void emptyResultSetForColumns(final DatabaseConnectionSource dataSource) throws Exception {

    // Verify that the test catalog has tables but no columns
    assertThat(catalog.getTables(), hasSize(14));
    for (final Table table : catalog.getTables()) {
      assertThat(table.getColumns(), is(empty()));
    }

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.TABLE_COLUMNS,
                "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE 1=0")
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

    // Verify that no columns were added to the tables in the test catalog
    for (final Table table : catalog.getTables()) {
      assertThat(
          "Table should have no columns after retrieving with empty result set",
          table.getColumns(),
          is(empty()));
    }
  }

  @Test
  @DisplayName("Test exception handling when column retrieval operations fail")
  public void exceptionHandlingWhenColumnRetrievalFails() throws Exception {
    // Create a mock connection source that throws an exception
    final DatabaseConnectionSource mockDataSource = Mockito.mock(DatabaseConnectionSource.class);
    final Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDataSource.get()).thenReturn(mockConnection);
    Mockito.when(mockConnection.getMetaData()).thenThrow(new SQLException("Test exception"));

    final SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaRetrievalOptionsBuilder.builder().toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(mockDataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableColumnRetriever tableColumnRetriever =
        new TableColumnRetriever(retrieverConnection, catalog, options);

    // Verify that the exception is properly handled
    final SQLException exception =
        assertThrows(
            SQLException.class,
            () -> {
              tableColumnRetriever.retrieveTableColumns(catalog.getAllTables(), new IncludeAll());
            });
    assertThat(
        exception.getMessage(), containsString("Could not retrieve table columns for table"));
    assertThat(exception.getCause().getMessage(), is("Test exception"));
  }

  @Test
  @DisplayName("Retrieve hidden table columns from data dictionary")
  public void hiddenTableColumns(final DatabaseConnectionSource dataSource) throws Exception {

    final MutableTable couponsTable =
        catalog.lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "COUPONS").get();
    assertThat(couponsTable.getAllColumns().size(), is(0));

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

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeColumns(new RegularExpressionInclusionRule(".*\\.COUPONS\\..*"));
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final TableColumnRetriever tableColumnRetriever =
        new TableColumnRetriever(retrieverConnection, catalog, options);
    tableColumnRetriever.retrieveTableColumns(catalog.getAllTables(), new IncludeAll());

    assertThat(couponsTable.getColumns().size(), is(3));
    assertThat(couponsTable.getHiddenColumns().size(), is(1));
    assertThat(couponsTable.getColumns(), not(contains(couponsTable.getHiddenColumns())));
  }

  @BeforeEach
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
    catalog = (MutableCatalog) getCatalog(connection, schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(14));
    for (final Table table : tables) {
      assertThat(table.getColumns(), is(empty()));
      assertThat(table.getForeignKeys(), is(empty()));
      assertThat(table.getPrimaryKey(), is(nullValue()));
    }
  }

  @Test
  @DisplayName("Test with malformed data in column result sets")
  public void malformedDataInColumnResultSets(final DatabaseConnectionSource dataSource)
      throws Exception {

    final MutableTable authorsTable =
        catalog.lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "AUTHORS").get();
    assertThat(authorsTable.getAllColumns().size(), is(0));

    // Create a SQL query that returns malformed data
    final String sqlWithMalformedData =
        "SELECT "
            + "'PUBLIC' AS TABLE_CAT, "
            + "'BOOKS' AS TABLE_SCHEM, "
            + "'AUTHORS' AS TABLE_NAME, "
            + // Using a known table name
            "'TEST_COLUMN' AS COLUMN_NAME, "
            + "999999 AS DATA_TYPE, "
            + // Invalid data type
            "'INVALID_TYPE' AS TYPE_NAME, "
            + "-1 AS COLUMN_SIZE, "
            + // Negative size
            "999 AS DECIMAL_DIGITS, "
            + // Too many decimal digits
            "0 AS NUM_PREC_RADIX, "
            + // Invalid radix
            "999 AS NULLABLE, "
            + // Invalid nullable value
            "'Test remarks' AS REMARKS, "
            + "'default' AS COLUMN_DEF, "
            + "0 AS SQL_DATA_TYPE, "
            + "0 AS SQL_DATETIME_SUB, "
            + "0 AS CHAR_OCTET_LENGTH, "
            + "1 AS ORDINAL_POSITION, "
            + "'INVALID' AS IS_NULLABLE, "
            + // Invalid is_nullable value
            "NULL AS SCOPE_CATALOG, "
            + "NULL AS SCOPE_SCHEMA, "
            + "NULL AS SCOPE_TABLE, "
            + "NULL AS SOURCE_DATA_TYPE, "
            + "'INVALID' AS IS_AUTOINCREMENT, "
            + // Invalid
            // is_autoincrement
            // value
            "'INVALID' AS IS_GENERATEDCOLUMN "
            + // Invalid is_generatedcolumn value
            "FROM (VALUES(0))";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(InformationSchemaKey.TABLE_COLUMNS, sqlWithMalformedData)
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

    // Assert column was added
    assertThat(authorsTable.getAllColumns().size(), is(1));

    final MutableColumn testColumn = authorsTable.lookupColumn("TEST_COLUMN").get();
    assertThat(testColumn.getRemarks(), is("Test remarks"));
    assertThat(testColumn.isNullable(), is(false));
    assertThat(testColumn.getRemarks(), is("Test remarks"));
    assertThat(testColumn.getColumnDataType().getFullName(), is("PUBLIC.BOOKS.INVALID_TYPE"));
  }

  @Test
  @DisplayName("Test with null column name")
  public void nulColumnName(final DatabaseConnectionSource dataSource) throws Exception {

    final MutableTable authorsTable =
        catalog.lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "AUTHORS").get();
    assertThat(authorsTable.getAllColumns().size(), is(0));

    // Create a SQL query that returns a row with NULL values in critical fields
    final String sqlWithNullColumnName =
        "SELECT "
            + "'PUBLIC' AS TABLE_CAT, "
            + "'BOOKS' AS TABLE_SCHEM, "
            + "'AUTHORS' AS TABLE_NAME, "
            + // Using a known table name
            "NULL AS COLUMN_NAME, "
            + // Use a null column name
            "NULL AS DATA_TYPE, "
            + // NULL data type
            "NULL AS TYPE_NAME, "
            + // NULL type name
            "NULL AS COLUMN_SIZE, "
            + "NULL AS DECIMAL_DIGITS, "
            + "NULL AS NUM_PREC_RADIX, "
            + "NULL AS NULLABLE, "
            + "NULL AS REMARKS, "
            + "NULL AS COLUMN_DEF, "
            + "NULL AS SQL_DATA_TYPE, "
            + "NULL AS SQL_DATETIME_SUB, "
            + "NULL AS CHAR_OCTET_LENGTH, "
            + "1 AS ORDINAL_POSITION, "
            + // Need a valid ordinal position
            "NULL AS IS_NULLABLE, "
            + "NULL AS SCOPE_CATALOG, "
            + "NULL AS SCOPE_SCHEMA, "
            + "NULL AS SCOPE_TABLE, "
            + "NULL AS SOURCE_DATA_TYPE, "
            + "NULL AS IS_AUTOINCREMENT, "
            + "NULL AS IS_GENERATEDCOLUMN "
            + "FROM (VALUES(0))";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(InformationSchemaKey.TABLE_COLUMNS, sqlWithNullColumnName)
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

    try {
      final TableColumnRetriever tableColumnRetriever =
          new TableColumnRetriever(retrieverConnection, catalog, options);
      tableColumnRetriever.retrieveTableColumns(catalog.getAllTables(), new IncludeAll());

      // Check that no new columns have been added, and no exception is thrown
      assertThat(authorsTable.getAllColumns().size(), is(0));

    } catch (final Exception e) {
      fail(e);
    }
  }

  @Test
  @DisplayName("Test with null values in critical column fields")
  public void nullValuesInCriticalColumnFields(final DatabaseConnectionSource dataSource)
      throws Exception {

    final MutableTable authorsTable =
        catalog.lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "AUTHORS").get();
    assertThat(authorsTable.getAllColumns().size(), is(0));

    // Create a SQL query that returns a row with NULL values in critical fields
    final String sqlWithNulls =
        "SELECT "
            + "'PUBLIC' AS TABLE_CAT, "
            + "'BOOKS' AS TABLE_SCHEM, "
            + "'AUTHORS' AS TABLE_NAME, "
            + // Using a known table name
            "'NULL_TEST_COLUMN' AS COLUMN_NAME, "
            + // Use a unique column name
            "NULL AS DATA_TYPE, "
            + // NULL data type
            "NULL AS TYPE_NAME, "
            + // NULL type name
            "NULL AS COLUMN_SIZE, "
            + "NULL AS DECIMAL_DIGITS, "
            + "NULL AS NUM_PREC_RADIX, "
            + "NULL AS NULLABLE, "
            + "NULL AS REMARKS, "
            + "NULL AS COLUMN_DEF, "
            + "NULL AS SQL_DATA_TYPE, "
            + "NULL AS SQL_DATETIME_SUB, "
            + "NULL AS CHAR_OCTET_LENGTH, "
            + "1 AS ORDINAL_POSITION, "
            + // Need a valid ordinal position
            "NULL AS IS_NULLABLE, "
            + "NULL AS SCOPE_CATALOG, "
            + "NULL AS SCOPE_SCHEMA, "
            + "NULL AS SCOPE_TABLE, "
            + "NULL AS SOURCE_DATA_TYPE, "
            + "NULL AS IS_AUTOINCREMENT, "
            + "NULL AS IS_GENERATEDCOLUMN "
            + "FROM (VALUES(0))";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(InformationSchemaKey.TABLE_COLUMNS, sqlWithNulls)
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

    try {
      final TableColumnRetriever tableColumnRetriever =
          new TableColumnRetriever(retrieverConnection, catalog, options);
      tableColumnRetriever.retrieveTableColumns(catalog.getAllTables(), new IncludeAll());

      // Assert column was added
      assertThat(authorsTable.getAllColumns().size(), is(1));

      final MutableColumn testColumn = authorsTable.lookupColumn("NULL_TEST_COLUMN").get();
      assertThat(testColumn.isNullable(), is(false));
      assertThat(testColumn.getColumnDataType().getName(), is(nullValue()));

    } catch (final Exception e) {
      fail(e);
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
