/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.test.utility.crawl.LightForeignKey;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.linter.LinterProviderCatalogSql;
import schemacrawler.tools.linter.LinterProviderColumnTypes;
import schemacrawler.tools.linter.LinterProviderForeignKeyMismatch;
import schemacrawler.tools.linter.LinterProviderForeignKeySelfReference;
import schemacrawler.tools.linter.LinterProviderForeignKeyWithNoIndexes;
import schemacrawler.tools.linter.LinterProviderNullColumnsInIndex;
import schemacrawler.tools.linter.LinterProviderNullIntendedColumns;
import schemacrawler.tools.linter.LinterProviderRedundantIndexes;
import schemacrawler.tools.linter.LinterProviderTableAllNullableColumns;
import schemacrawler.tools.linter.LinterProviderTableCycles;
import schemacrawler.tools.linter.LinterProviderTableEmpty;
import schemacrawler.tools.linter.LinterProviderTableSql;
import schemacrawler.tools.linter.LinterProviderTableWithBadlyNamedColumns;
import schemacrawler.tools.linter.LinterProviderTableWithIncrementingColumns;
import schemacrawler.tools.linter.LinterProviderTableWithNoIndexes;
import schemacrawler.tools.linter.LinterProviderTableWithNoPrimaryKey;
import schemacrawler.tools.linter.LinterProviderTableWithNoRemarks;
import schemacrawler.tools.linter.LinterProviderTableWithNoSurrogatePrimaryKey;
import schemacrawler.tools.linter.LinterProviderTableWithPrimaryKeyNotFirst;
import schemacrawler.tools.linter.LinterProviderTableWithQuotedNames;
import schemacrawler.tools.linter.LinterProviderTableWithSingleColumn;
import schemacrawler.tools.linter.LinterProviderTooManyLobs;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.test.utility.TestObjectUtility.Results;

@TestInstance(Lifecycle.PER_CLASS)
public class LintersTest {

  private Connection connection;
  private LinterConfig linterConfig;

  @BeforeAll
  public void setup() {
    connection = TestObjectUtility.mockConnection();
    linterConfig =
        new LinterConfig("test-linters", true, LintSeverity.high, 0, ".*", "", ".*", "", Map.of());
  }

  @Test
  public void testCatalogSql() throws SQLException {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderCatalogSql().newLinter(lintCollector);
    linter.configure(
        new LinterConfig(
            "test-linters",
            true,
            LintSeverity.high,
            0,
            ".*",
            "",
            ".*",
            "",
            Map.of("message", "catalog lint", "sql", "SELECT 'lint'")));

    final Connection connection =
        TestObjectUtility.mockConnection(
            new Results(new String[] {"COL1"}, new Object[][] {new Object[] {"lint"}}));

    final Catalog catalog = LightCatalogUtility.lightCatalog();
    linter.setCatalog(catalog);

    ((BaseLinter) linter).start(connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("catalog lint lint"));
  }

  @Test
  public void testColumnTypes() {

    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderColumnTypes().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table1 = new LightTable("TABLE1");
    table1.addDataColumn("COL", "TYPE1");
    final LightTable table2 = new LightTable("TABLE2");
    table2.addDataColumn("COL", "TYPE2");

    final Catalog catalog = LightCatalogUtility.lightCatalog(table1, table2);
    linter.setCatalog(catalog);

    ((BaseLinter) linter).start(connection);
    ((BaseLinter) linter).lint(table1, connection);
    ((BaseLinter) linter).lint(table2, connection);
    ((BaseLinter) linter).end(connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("column with same name but different data types"));
  }

  @Test
  public void testForeignKeyMismatch() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderForeignKeyMismatch().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable pkTable = new LightTable("TEST_PKTABLE");
    final Column pkColumn = spy(pkTable.addColumn("PK_COLUMN"));
    when(pkColumn.getSize()).thenReturn(20);

    final LightTable fkTable = spy(new LightTable("TEST_FKTABLE"));
    final Column fkColumn = spy(fkTable.addColumn("FK_COLUMN"));
    when(fkColumn.getSize()).thenReturn(10);

    final ForeignKey foreignKey = new LightForeignKey("TEST_FK", fkColumn, pkColumn);
    when(fkTable.getImportedForeignKeys()).thenReturn(List.of(foreignKey));

    ((BaseLinter) linter).lint(fkTable, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("foreign key data type different from primary key"));
  }

  @Test
  public void testForeignKeySelfReference() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderForeignKeySelfReference().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable fkTable = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));
    when(fkTable.isSelfReferencing()).thenReturn(true);

    final Column column = fkTable.addColumn("COL");

    final ForeignKey foreignKey = new LightForeignKey("SELF_FK", column, column);
    when(fkTable.getImportedForeignKeys()).thenReturn(List.of(foreignKey));

    ((BaseLinter) linter).lint(fkTable, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("foreign key self-references primary key"));
  }

  @Test
  public void testForeignKeyWithNoIndex() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderForeignKeyWithNoIndexes().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable pkTable = new LightTable("TEST_PKTABLE");
    final Column pkColumn = spy(pkTable.addColumn("PK_COLUMN"));
    when(pkColumn.getSize()).thenReturn(20);

    final LightTable fkTable = spy(new LightTable("TEST_FKTABLE"));
    final Column fkColumn = spy(fkTable.addColumn("FK_COLUMN"));
    when(fkColumn.getSize()).thenReturn(10);

    final ForeignKey foreignKey = new LightForeignKey("TEST_FK", fkColumn, pkColumn);
    when(fkTable.getImportedForeignKeys()).thenReturn(List.of(foreignKey));

    ((BaseLinter) linter).lint(fkTable, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(), is("foreign key with no index"));
  }

  @Test
  public void testNullColumnsInIndex() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderNullColumnsInIndex().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));
    final Index index = spy(LightCatalogUtility.lightNamedObject(Index.class, "IDX"));
    when(index.isUnique()).thenReturn(true);

    final IndexColumn indexColumn =
        spy(LightCatalogUtility.lightNamedObject(IndexColumn.class, "COL"));
    when(indexColumn.isNullable()).thenReturn(true);
    when(indexColumn.isGenerated()).thenReturn(false);
    when(index.iterator()).thenReturn(List.of(indexColumn).iterator());

    when(table.getIndexes()).thenReturn(List.of(index));

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("unique index with nullable columns"));
  }

  @Test
  public void testNullIntendedColumns() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderNullIntendedColumns().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    final Column column = spy(table.addColumn("TEST_COLUMN"));
    when(column.getDefaultValue()).thenReturn(" NULL ");
    table.addColumn(column);

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("column where NULL may be intended"));
  }

  @Test
  public void testRedundantIndexes() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderRedundantIndexes().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));

    final IndexColumn idxCol1 = LightCatalogUtility.lightNamedObject(IndexColumn.class, "COL1");
    final IndexColumn idxCol2 = LightCatalogUtility.lightNamedObject(IndexColumn.class, "COL2");

    final Index index1 = spy(LightCatalogUtility.lightNamedObject(Index.class, "IDX1"));
    when(index1.getColumns()).thenReturn(List.of(idxCol1, idxCol2));

    final Index index2 = spy(LightCatalogUtility.lightNamedObject(Index.class, "IDX2"));
    when(index2.getColumns()).thenReturn(List.of(idxCol1));

    when(table.getIndexes()).thenReturn(List.of(index1, index2));

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("redundant index"));
  }

  @Test
  public void testTableAllNullableColumns() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableAllNullableColumns().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));
    final Column pkCol = spy(LightCatalogUtility.lightNamedObject(Column.class, "COL"));
    when(pkCol.isPartOfPrimaryKey()).thenReturn(true);
    when(pkCol.isNullable()).thenReturn(false);
    when(table.getColumns()).thenReturn(List.of(pkCol));
    final Column column = spy(LightCatalogUtility.lightNamedObject(Column.class, "COL"));
    when(column.isPartOfPrimaryKey()).thenReturn(false);
    when(column.isNullable()).thenReturn(true);
    when(table.getColumns()).thenReturn(List.of(column));

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("no non-nullable data columns"));
  }

  @Test
  public void testTableCycles() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableCycles().newLinter(lintCollector);
    linter.configure(linterConfig);

    final Table table1 = spy(new LightTable("TABLE1"));
    final Table table2 = spy(new LightTable("TABLE2"));

    final ForeignKey fk1 = new LightForeignKey("FK1", table1, table2);
    when(table1.getForeignKeys()).thenReturn(List.of(fk1));
    final ForeignKey fk2 = new LightForeignKey("FK2", table2, table1);
    when(table2.getForeignKeys()).thenReturn(List.of(fk2));

    final Catalog catalog = LightCatalogUtility.lightCatalog();
    linter.setCatalog(catalog);

    ((BaseLinter) linter).start(connection);
    ((BaseLinter) linter).lint(table1, connection);
    ((BaseLinter) linter).lint(table2, connection);
    ((BaseLinter) linter).end(connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("cycles in table relationships"));
  }

  @Test
  public void testTableEmpty() throws SQLException {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableEmpty().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));

    final Connection connection =
        TestObjectUtility.mockConnection(
            new Results(new String[] {"COUNT"}, new Object[][] {{0L}}));

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("empty table"));
  }

  @Test
  public void testTableSql() throws SQLException {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableSql().newLinter(lintCollector);
    linter.configure(
        new LinterConfig(
            "test-linters",
            true,
            LintSeverity.high,
            0,
            ".*",
            "",
            ".*",
            "",
            Map.of("message", "table lint", "sql", "SELECT 'lint'")));

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));

    final Connection connection =
        TestObjectUtility.mockConnection(
            new Results(new String[] {"COL1"}, new Object[][] {new Object[] {"lint"}}));

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("table lint lint"));
  }

  @Test
  public void testTableWithBadlyNamedColumns() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithBadlyNamedColumns().newLinter(lintCollector);
    linter.configure(
        new LinterConfig(
            "test-linters",
            true,
            LintSeverity.high,
            0,
            ".*",
            "",
            ".*",
            "",
            Map.of("bad-column-names", ".*BAD.*")));

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("GOOD_COLUMN");
    table.addColumn("BAD_COLUMN");

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("badly named column"));
  }

  @Test
  public void testTableWithIncrementingColumns() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithIncrementingColumns().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = new LightTable("TEST_TABLE");
    table.addColumn("COL1");
    table.addColumn("COL2");

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(2));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("incrementing columns"));
  }

  @Test
  public void testTableWithNoIndexes() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoIndexes().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");
    table.addColumn("NAME");

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("no indexes"));
  }

  @Test
  public void testTableWithNoPrimaryKey() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoPrimaryKey().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");
    table.addColumn("NAME");

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("no primary key"));
  }

  @Test
  public void testTableWithNoRemarks() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoRemarks().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");
    table.addColumn("NAME");

    ((BaseLinter) linter).lint(table, connection);

    // Should have 2 lints: one for table, one for columns
    assertThat(lintCollector.getLints().size(), is(2));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("should have remarks"));
  }

  @Test
  public void testTableWithNoSurrogatePrimaryKey() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter =
        new LinterProviderTableWithNoSurrogatePrimaryKey().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));
    final PrimaryKey pk = mock(PrimaryKey.class);
    final TableConstraintColumn col1 = mock(TableConstraintColumn.class);
    final TableConstraintColumn col2 = mock(TableConstraintColumn.class);
    when(pk.getConstrainedColumns()).thenReturn(List.of(col1, col2));
    when(table.getPrimaryKey()).thenReturn(pk);

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("primary key should not be a surrogate"));
  }

  @Test
  public void testTableWithPrimaryKeyNotFirst() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithPrimaryKeyNotFirst().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));
    final PrimaryKey pk = mock(PrimaryKey.class);
    final TableConstraintColumn col = mock(TableConstraintColumn.class);
    when(col.getTableConstraintOrdinalPosition()).thenReturn(1);
    when(col.getOrdinalPosition()).thenReturn(3);
    when(pk.getConstrainedColumns()).thenReturn(List.of(col));
    when(table.getPrimaryKey()).thenReturn(pk);

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(), is("primary key not first"));
  }

  @Test
  public void testTableWithQuotedNames() throws SQLException {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithQuotedNames().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = new LightTable(new SchemaReference(), "TABLE WITH SPACES");

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("spaces in name, or reserved word"));
  }

  @Test
  public void testTableWithSingleColumn() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithSingleColumn().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("single column"));
  }

  @Test
  public void testTooManyLobs() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTooManyLobs().newLinter(lintCollector);
    linter.configure(linterConfig);

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addDataColumn("COL1", "CLOB");
    table.addDataColumn("COL2", "BLOB");

    ((BaseLinter) linter).lint(table, connection);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(), is("too many binary objects"));
  }
}
