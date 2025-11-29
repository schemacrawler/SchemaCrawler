/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_TABLES;
import static schemacrawler.schemacrawler.InformationSchemaKey.VIEW_TABLE_USAGE;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.utility.MetaDataUtility.isView;

import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableExtRetrieverTest {

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve enum data types")
  public void enumDataTypes(final DatabaseConnectionSource dataSource) throws Exception {

    final EnumDataTypeHelper enumDataTypeHelper =
        (column, columnDataType, databaseConnection) -> {
          final String columnDataTypeName = columnDataType.getName();
          switch (columnDataTypeName) {
            case "NAME_TYPE":
              final EnumDataTypeInfo enumDataTypeType =
                  new EnumDataTypeInfo(
                      EnumDataTypeTypes.enumerated_data_type, List.of("Moe", "Larry", "Curly"));
              assertThat(
                  enumDataTypeType.toString(),
                  is("EnumDataTypeInfo [enumerated_data_type, [Moe, Larry, Curly]]"));
              return enumDataTypeType;
            case "AGE_TYPE":
              final EnumDataTypeInfo enumColumnType =
                  new EnumDataTypeInfo(
                      EnumDataTypeTypes.enumerated_column, List.of("1", "16", "29"));
              assertThat(
                  enumColumnType.toString(),
                  is("EnumDataTypeInfo [enumerated_column, [1, 16, 29]]"));
              return enumColumnType;
            default:
              assertThat(
                  EnumDataTypeInfo.EMPTY_ENUM_DATA_TYPE_INFO.toString(),
                  is("EnumDataTypeInfo [not_enumerated, []]"));
              return EnumDataTypeInfo.EMPTY_ENUM_DATA_TYPE_INFO;
          }
        };
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withEnumDataTypeHelper(enumDataTypeHelper);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever =
        new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveAdditionalColumnMetadata();

    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(20));
    final Table table =
        catalog.lookupTable(schema, "CUSTOMERS").orElseThrow(IllegalAccessException::new);

    // Non-enum data type
    final Column id = table.lookupColumn("ID").orElseThrow(IllegalAccessException::new);
    final ColumnDataType idDataType = id.getColumnDataType();
    final ColumnDataType idDataTypeMain =
        catalog
            .lookupColumnDataType(new SchemaReference(), idDataType.getName())
            .orElseThrow(IllegalAccessException::new);
    assertThat(idDataType == idDataTypeMain, is(true)); // assert same object reference
    assertThat(idDataType.isEnumerated(), is(false));
    assertThat(idDataType.getEnumValues(), is(Collections.EMPTY_LIST));

    // Globally enumerated data type
    final Column firstName =
        table.lookupColumn("FIRSTNAME").orElseThrow(IllegalAccessException::new);
    final ColumnDataType firstNameDataType = firstName.getColumnDataType();
    final ColumnDataType firstNameDataTypeMain =
        catalog
            .lookupColumnDataType(schema, firstNameDataType.getName())
            .orElseThrow(IllegalAccessException::new);
    assertThat(
        firstNameDataType == firstNameDataTypeMain, is(true)); // assert same object reference
    assertThat(firstNameDataType.isEnumerated(), is(true));
    assertThat(firstNameDataType.getEnumValues(), is(List.of("Moe", "Larry", "Curly")));

    // Enumerated column data type
    final Column age = table.lookupColumn("AGE").orElseThrow(IllegalAccessException::new);
    final ColumnDataType ageDataType = age.getColumnDataType();
    final ColumnDataType ageDataTypeMain =
        catalog
            .lookupColumnDataType(schema, ageDataType.getName())
            .orElseThrow(IllegalAccessException::new);
    assertThat(ageDataType == ageDataTypeMain, is(false)); // assert different object references
    assertThat(ageDataType.isEnumerated(), is(true));
    assertThat(ageDataType.getEnumValues(), is(List.of("1", "16", "29")));
    assertThat(ageDataTypeMain.isEnumerated(), is(false));
    assertThat(ageDataTypeMain.getEnumValues(), is(Collections.EMPTY_LIST));
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection) {
    catalog =
        (MutableCatalog)
            getCatalog(connection, SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(20));
    for (final Table table : tables) {
      for (final Index index : table.getIndexes()) {
        final List<IndexColumn> columns = index.getColumns();
        assertThat(columns, is(not(empty())));
        for (final IndexColumn column : columns) {
          assertThat(column.isGenerated(), is(false));
        }
      }
    }
  }

  @Test
  @DisplayName("Retrieve table constraint definitions from INFORMATION_SCHEMA")
  public void tableConstraintInfo(final DatabaseConnectionSource dataSource) throws Exception {

    final String remarks = "TEST Table Constraint remarks";
    final String definition = "TEST Table Constraint definition";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.EXT_TABLE_CONSTRAINTS,
                """
                SELECT DISTINCT
                	CONSTRAINT_CATALOG,
                	CONSTRAINT_SCHEMA,
                	TABLE_NAME,
                    CONSTRAINT_NAME,
                    '%s' AS REMARKS,
                    '%s' AS CONSTRAINT_DEFINITION
                FROM
                	INFORMATION_SCHEMA.TABLE_CONSTRAINTS
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

    final TableConstraintRetriever tableExtRetriever =
        new TableConstraintRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveTableConstraintInformation();

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(20));
    for (final Table table : tables) {
      for (final TableConstraint constraint : table.getTableConstraints()) {
        if (constraint instanceof ForeignKey) {
          continue;
        }
        assertThat(
            "<%s> remarks do not match expected".formatted(constraint),
            constraint.getRemarks(),
            is(remarks));
        // NOTE: Table constraint definition is not set
        assertThat(constraint.getDefinition(), is(""));
      }
    }
  }

  @Test
  @DisplayName("Retrieve table definitions from INFORMATION_SCHEMA")
  public void tableDefinitions(final DatabaseConnectionSource dataSource) throws Exception {

    final String definition = "TEST Table definition";

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                EXT_TABLES,
                """
                SELECT DISTINCT
                   	TABLE_CAT AS TABLE_CATALOG,
                   	TABLE_SCHEM AS TABLE_SCHEMA,
                    TABLE_NAME,
                    '%s' AS TABLE_DEFINITION
                FROM
                   	INFORMATION_SCHEMA.SYSTEM_TABLES
                """
                    .formatted(definition))
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final TableExtRetriever tableExtRetriever =
        new TableExtRetriever(retrieverConnection, catalog, options);
    tableExtRetriever.retrieveTableDefinitions();

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(20));
    for (final Table table : tables) {
      assertThat(table.getDefinition(), is(definition));
    }
  }

  @Test
  @DisplayName("Retrieve view table usage from INFORMATION_SCHEMA")
  public void viewTableUsage(final DatabaseConnectionSource dataSource) throws Exception {

    int viewCount;
    final Collection<Table> tables = catalog.getTables();
    viewCount = 0;
    assertThat(tables, hasSize(20));
    for (final Table table : tables) {
      if (!isView(table)) {
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
                """
                SELECT
                  VIEW_CATALOG,
                  VIEW_SCHEMA,
                  VIEW_NAME,
                  TABLE_CATALOG,
                  TABLE_SCHEMA,
                  TABLE_NAME
                FROM
                  INFORMATION_SCHEMA.VIEW_TABLE_USAGE
                """)
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final ViewExtRetriever viewExtRetriever =
        new ViewExtRetriever(retrieverConnection, catalog, options);
    viewExtRetriever.retrieveViewTableUsage();

    viewCount = 0;
    assertThat(tables, hasSize(20));
    for (final Table table : tables) {
      if (!isView(table)) {
        continue;
      }
      viewCount = viewCount + 1;
      final View view = (View) table;
      assertThat(view.getTableUsage(), is(not(empty())));
    }
    assertThat(viewCount, is(1));
  }
}
