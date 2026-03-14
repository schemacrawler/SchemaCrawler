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
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import schemacrawler.ermodel.utility.EntityModelUtility;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.JavaSqlTypeGroup;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.linter.LinterProviderForeignKeyMismatch;
import schemacrawler.tools.linter.LinterProviderForeignKeySelfReference;
import schemacrawler.tools.linter.LinterProviderForeignKeyWithNoIndexes;
import schemacrawler.tools.linter.LinterProviderNullIntendedColumns;
import schemacrawler.tools.linter.LinterProviderRedundantIndexes;
import schemacrawler.tools.linter.LinterProviderTableWithBadlyNamedColumns;
import schemacrawler.tools.linter.LinterProviderTableWithNoIndexes;
import schemacrawler.tools.linter.LinterProviderTableWithNoPrimaryKey;
import schemacrawler.tools.linter.LinterProviderTableWithNoRemarks;
import schemacrawler.tools.linter.LinterProviderTableWithSingleColumn;
import schemacrawler.tools.linter.LinterProviderTooManyLobs;
import us.fatehi.utility.OptionalBoolean;

public class LintersTest {

  @Test
  public void testForeignKeyMismatch() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderForeignKeyMismatch().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));
    final Column pkColumn = mockNamedObject(Column.class, "PK_COLUMN");
    final ColumnDataType pkType = mock(ColumnDataType.class);
    final JavaSqlType pkJavaSqlType = mock(JavaSqlType.class);
    when(pkType.getJavaSqlType()).thenReturn(pkJavaSqlType);
    when(pkColumn.isColumnDataTypeKnown()).thenReturn(true);
    when(pkColumn.getColumnDataType()).thenReturn(pkType);
    when(pkColumn.getSize()).thenReturn(10);

    final Column fkColumn = mockNamedObject(Column.class, "FK_COLUMN");
    final ColumnDataType fkType = mock(ColumnDataType.class);
    final JavaSqlType fkJavaSqlType = mock(JavaSqlType.class);
    when(fkType.getJavaSqlType()).thenReturn(fkJavaSqlType);
    when(fkColumn.isColumnDataTypeKnown()).thenReturn(true);
    when(fkColumn.getColumnDataType()).thenReturn(fkType);
    when(fkColumn.getSize()).thenReturn(20);

    final ColumnReference columnReference = mock(ColumnReference.class);
    when(columnReference.getPrimaryKeyColumn()).thenReturn(pkColumn);
    when(columnReference.getForeignKeyColumn()).thenReturn(fkColumn);

    final ForeignKey foreignKey = mockNamedObject(ForeignKey.class, "FK");
    when(foreignKey.iterator()).thenReturn(List.of(columnReference).iterator());

    when(table.getImportedForeignKeys()).thenReturn(List.of(foreignKey));

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("foreign key data type different from primary key"));
  }

  @Test
  public void testForeignKeySelfReference() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderForeignKeySelfReference().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));
    when(table.isSelfReferencing()).thenReturn(true);

    final Column column = mockNamedObject(Column.class, "COL");

    final ColumnReference columnReference = mock(ColumnReference.class);
    when(columnReference.getPrimaryKeyColumn()).thenReturn(column);
    when(columnReference.getForeignKeyColumn()).thenReturn(column);

    final ForeignKey foreignKey = mockNamedObject(ForeignKey.class, "FK");
    when(foreignKey.iterator()).thenReturn(List.of(columnReference).iterator());

    when(table.getImportedForeignKeys()).thenReturn(List.of(foreignKey));

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("foreign key self-references primary key"));
  }

  @Test
  public void testForeignKeyWithNoIndex() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderForeignKeyWithNoIndexes().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = mockNamedObject(LightTable.class, "TEST_TABLE");

    final ForeignKey foreignKey = mockNamedObject(ForeignKey.class, "FK");
    when(table.getImportedForeignKeys()).thenReturn(List.of(foreignKey));

    try (final MockedStatic<EntityModelUtility> entityModelUtility =
        mockStatic(EntityModelUtility.class)) {
      entityModelUtility
          .when(() -> EntityModelUtility.coveredByIndex(foreignKey))
          .thenReturn(OptionalBoolean.false_value);

      ((BaseLinter) linter).lint(table, null);

      assertThat(lintCollector.getLints().size(), is(1));
      assertThat(
          lintCollector.getLints().iterator().next().getMessage(), is("foreign key with no index"));
    }
  }

  @Test
  public void testNullIntendedColumns() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderNullIntendedColumns().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));
    final Column column = mockNamedObject(Column.class, "TEST_COLUMN");
    when(column.getDefaultValue()).thenReturn(" NULL ");

    when(table.getColumns()).thenReturn(List.of(column));

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(),
        is("column where NULL may be intended"));
  }

  @Test
  public void testRedundantIndexes() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderRedundantIndexes().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));

    mockNamedObject(Column.class, "COL1");
    mockNamedObject(Column.class, "COL2");

    final IndexColumn idxCol1 = mockNamedObject(IndexColumn.class, "COL1");
    // Depending on how listStartsWith is implemented, we might need more

    final IndexColumn idxCol2 = mockNamedObject(IndexColumn.class, "COL2");

    final Index index1 = mockNamedObject(Index.class, "IDX1");
    when(index1.getColumns()).thenReturn(List.of(idxCol1, idxCol2));

    final Index index2 = mockNamedObject(Index.class, "IDX2");
    when(index2.getColumns()).thenReturn(List.of(idxCol1));

    when(table.getIndexes()).thenReturn(List.of(index1, index2));

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("redundant index"));
  }

  @Test
  public void testTableWithBadlyNamedColumns() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithBadlyNamedColumns().newLinter(lintCollector);
    ((BaseLinter) linter)
        .configure(
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

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("badly named column"));
  }

  @Test
  public void testTableWithNoIndexes() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoIndexes().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("no indexes"));
  }

  @Test
  public void testTableWithNoPrimaryKey() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoPrimaryKey().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");
    table.addColumn("NAME");

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("no primary key"));
  }

  @Test
  public void testTableWithNoRemarks() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoRemarks().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");

    ((BaseLinter) linter).lint(table, null);

    // Should have 2 lints: one for table, one for column ID
    assertThat(lintCollector.getLints().size(), is(2));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("should have remarks"));
  }

  @Test
  public void testTableWithSingleColumn() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithSingleColumn().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("single column"));
  }

  @Test
  public void testTooManyLobs() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTooManyLobs().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = spy(new LightTable(new SchemaReference(), "TEST_TABLE"));

    final Column lob1 = mockNamedObject(Column.class, "LOB1");
    final ColumnDataType type1 = mock(ColumnDataType.class);
    final JavaSqlType sqlType1 = mock(JavaSqlType.class);
    when(lob1.isColumnDataTypeKnown()).thenReturn(true);
    when(lob1.getColumnDataType()).thenReturn(type1);
    when(type1.getJavaSqlType()).thenReturn(sqlType1);
    when(sqlType1.getJavaSqlTypeGroup()).thenReturn(JavaSqlTypeGroup.large_object);

    final Column lob2 = mockNamedObject(Column.class, "LOB2");
    final ColumnDataType type2 = mock(ColumnDataType.class);
    final JavaSqlType sqlType2 = mock(JavaSqlType.class);
    when(lob2.isColumnDataTypeKnown()).thenReturn(true);
    when(lob2.getColumnDataType()).thenReturn(type2);
    when(type2.getJavaSqlType()).thenReturn(sqlType2);
    when(sqlType2.getJavaSqlTypeGroup()).thenReturn(JavaSqlTypeGroup.large_object);

    when(table.getColumns()).thenReturn(List.of(lob1, lob2));

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(
        lintCollector.getLints().iterator().next().getMessage(), is("too many binary objects"));
  }

  private LinterConfig createConfig() {
    return new LinterConfig(
        "test-linters", true, LintSeverity.high, 0, ".*", "", ".*", "", Map.of());
  }

  private <T extends NamedObject> T mockNamedObject(final Class<T> clazz, final String name) {
    final T mock = mock(clazz);
    final NamedObjectKey key = new NamedObjectKey(name);
    when(mock.key()).thenReturn(key);
    when(mock.getFullName()).thenReturn(name);
    return mock;
  }
}
