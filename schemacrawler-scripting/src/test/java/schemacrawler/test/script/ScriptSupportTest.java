/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.script;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.test.utility.crawl.LightCatalogUtility.lightNamedObject;

import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.test.utility.crawl.LightColumn;
import schemacrawler.test.utility.crawl.LightForeignKey;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.script.ScriptSupport;

public class ScriptSupportTest {

  private final ScriptSupport scriptSupport = new ScriptSupport();

  @Test
  public void cleanFullName() {
    assertThat(scriptSupport.cleanFullName(null), is(""));

    final LightTable lightTable = new LightTable("tablename");
    assertThat(scriptSupport.cleanFullName(lightTable), is("tablename"));

    final NamedObject quotedObject = lightNamedObject(NamedObject.class, "\"schema\".\"table\"");
    assertThat(scriptSupport.cleanFullName(quotedObject), is("schema.table"));
  }

  @Test
  public void cleanName() {
    assertThat(scriptSupport.cleanName(null), is(""));

    final LightTable lightTable = new LightTable("tablename");
    assertThat(scriptSupport.cleanName(lightTable), is("tablename"));

    final NamedObject quotedObject = lightNamedObject(NamedObject.class, "\"tablename\"");
    assertThat(scriptSupport.cleanName(quotedObject), is("tablename"));
  }

  @Test
  public void columnReferences() {
    assertThat(scriptSupport.columnReferences(null).isEmpty(), is(true));

    // FK with null column references list (via mock)
    final ForeignKey foreignKeyNullReferences = mock(ForeignKey.class);
    when(foreignKeyNullReferences.getColumnReferences()).thenReturn(null);
    assertThat(scriptSupport.columnReferences(foreignKeyNullReferences).isEmpty(), is(true));

    // LightForeignKey with no column references (table-level constructor)
    final LightTable fkTable = new LightTable("fk_table");
    final LightTable pkTable = new LightTable("pk_table");
    final LightForeignKey emptyFk = new LightForeignKey("FK_NAME", fkTable, pkTable);
    assertThat(scriptSupport.columnReferences(emptyFk).isEmpty(), is(true));

    // LightForeignKey with a column reference
    final LightColumn fkColumn = fkTable.addColumn("fk_col");
    final LightColumn pkColumn = pkTable.addColumn("pk_col");
    final LightForeignKey fkWithRef = new LightForeignKey("FK_NAME", fkColumn, pkColumn);
    final List<ColumnReference> refs = scriptSupport.columnReferences(fkWithRef);
    assertThat(refs.size(), is(1));
    assertThat(refs.get(0).getForeignKeyColumn().getName(), is("fk_col"));
    assertThat(refs.get(0).getPrimaryKeyColumn().getName(), is("pk_col"));
  }

  @Test
  public void columns() {
    assertThat(scriptSupport.columns((Index) null), is(""));
    assertThat(scriptSupport.columns((PrimaryKey) null), is(""));

    // Use LightColumn (raw cast) so isColumnDataTypeKnown() returns true
    final LightTable table = new LightTable("t");
    final LightColumn col = table.addColumn("INDEX_COL");

    final Index index = mock(Index.class);
    when(index.getColumns()).thenReturn((List) List.of(col));
    assertThat(scriptSupport.columns(index), containsString("INDEX_COL"));

    // Positive test for columns(PrimaryKey)
    final LightColumn pkCol = table.addColumn("PK_COL");
    final PrimaryKey primaryKey = mock(PrimaryKey.class);
    when(primaryKey.getConstrainedColumns()).thenReturn((List) List.of(pkCol));
    assertThat(scriptSupport.columns(primaryKey), containsString("PK_COL"));
  }

  @Test
  public void columnType() {
    assertThat(scriptSupport.columnType(null), is(""));

    final Column noTypeColumn = mock(Column.class);
    when(noTypeColumn.getColumnDataType()).thenReturn(null);
    assertThat(scriptSupport.columnType(noTypeColumn), is(""));

    final LightTable table = new LightTable("t");
    final LightColumn varcharColumn = table.addDataColumn("col", "VARCHAR");
    assertThat(scriptSupport.columnType(varcharColumn), is("VARCHAR"));
  }

  @Test
  public void foreignKeyColumnsAndHasName() {
    assertThat(scriptSupport.fkColumns(null), is(""));

    // Use LightColumn (raw cast) so isColumnDataTypeKnown() returns true
    final LightTable table = new LightTable("t");
    final LightColumn fkCol = table.addColumn("FK_COL");

    final ForeignKey foreignKey = mock(ForeignKey.class);
    when(foreignKey.getConstrainedColumns()).thenReturn((List) List.of(fkCol));
    when(foreignKey.getName()).thenReturn("FK_TABLE_OTHER");

    assertThat(scriptSupport.fkColumns(foreignKey), containsString("FK_COL"));
    assertThat(scriptSupport.hasName(foreignKey), is(true));

    // System-generated FK name should return hasName = false
    final LightTable fkTable = new LightTable("fk_table");
    final LightTable pkTable = new LightTable("pk_table");
    final LightForeignKey systemFk = new LightForeignKey("SYS_C00001", fkTable, pkTable);
    assertThat(scriptSupport.hasName(systemFk), is(false));
  }

  @Test
  public void indent() {
    assertThat(scriptSupport.indent(null, 2), is(""));
    assertThat(scriptSupport.indent("x", 2), is("  x\n"));
  }

  @Test
  public void nonPrimaryIndexes() {
    assertThat(scriptSupport.nonPrimaryIndexes(null).isEmpty(), is(true));

    final Table tableWithNullIndexes = mock(Table.class);
    when(tableWithNullIndexes.getIndexes()).thenReturn(null);
    assertThat(scriptSupport.nonPrimaryIndexes(tableWithNullIndexes).isEmpty(), is(true));

    final Table tableWithNoIndexes = mock(Table.class);
    when(tableWithNoIndexes.getIndexes()).thenReturn(List.of());
    assertThat(scriptSupport.nonPrimaryIndexes(tableWithNoIndexes).isEmpty(), is(true));

    // Use LightColumn (raw cast) so isColumnDataTypeKnown() returns true for all
    // index lookups
    final LightTable lightTable = new LightTable("t");
    final LightColumn col1 = lightTable.addColumn("COL1");
    final LightColumn col2 = lightTable.addColumn("COL2");

    final Index indexNoPk = mock(Index.class);
    when(indexNoPk.getColumns()).thenReturn((List) List.of(col1));

    final Table tableWithoutPrimaryKey = mock(Table.class);
    when(tableWithoutPrimaryKey.getIndexes()).thenReturn(List.of(indexNoPk));
    when(tableWithoutPrimaryKey.hasPrimaryKey()).thenReturn(false);
    assertThat(scriptSupport.nonPrimaryIndexes(tableWithoutPrimaryKey), is(List.of(indexNoPk)));

    // pkEquivalentIndex shares the same column (COL1) as the primary key
    final PrimaryKey primaryKey = mock(PrimaryKey.class);
    when(primaryKey.getConstrainedColumns()).thenReturn((List) List.of(col1));

    final Index pkEquivalentIndex = mock(Index.class);
    when(pkEquivalentIndex.getColumns()).thenReturn((List) List.of(col1));

    final Index nonPrimaryIndex = mock(Index.class);
    when(nonPrimaryIndex.getColumns()).thenReturn((List) List.of(col2));

    final Table tableWithPrimaryKey = mock(Table.class);
    when(tableWithPrimaryKey.getIndexes()).thenReturn(List.of(pkEquivalentIndex, nonPrimaryIndex));
    when(tableWithPrimaryKey.hasPrimaryKey()).thenReturn(true);
    when(tableWithPrimaryKey.getPrimaryKey()).thenReturn(primaryKey);
    assertThat(scriptSupport.nonPrimaryIndexes(tableWithPrimaryKey), is(List.of(nonPrimaryIndex)));
  }

  @Test
  public void pkColumns() {
    assertThat(scriptSupport.pkColumns(null), is(""));

    // Use LightForeignKey with LightColumns so joinColumns works correctly
    final LightTable fkTable = new LightTable("fk_table");
    final LightTable pkTable = new LightTable("pk_table");
    final LightColumn fkCol = fkTable.addColumn("fk_col");
    final LightColumn pkCol = pkTable.addColumn("PK_COL");
    final LightForeignKey foreignKey = new LightForeignKey("FK_NAME", fkCol, pkCol);

    assertThat(scriptSupport.pkColumns(foreignKey), containsString("PK_COL"));
  }

  @Test
  public void remarks() {
    assertThat(scriptSupport.remarks(null), is(""));

    final LightTable noRemarksTable = new LightTable("t");
    assertThat(scriptSupport.remarks(noRemarksTable), is(""));

    final LightTable tableWithRemarks = new LightTable("t");
    tableWithRemarks.setRemarks("  line1\n\"line2\"  ");
    assertThat(scriptSupport.remarks(tableWithRemarks), is("line1 'line2'"));
  }

  @Test
  public void stripName() {
    assertThat(scriptSupport.stripName(null), is(""));

    final NamedObject namedObject = lightNamedObject(NamedObject.class, "abc[^\\d\\w\\-]xyz");
    assertThat(scriptSupport.stripName(namedObject), is("abcxyz"));
  }

  @Test
  public void type() {
    // getSimpleTypeName(null) returns "unknown", no NPE
    assertThat(scriptSupport.type(null), is("unknown"));

    final LightTable lightTable = new LightTable("t");
    assertThat(scriptSupport.type(lightTable), is("table"));
  }
}
