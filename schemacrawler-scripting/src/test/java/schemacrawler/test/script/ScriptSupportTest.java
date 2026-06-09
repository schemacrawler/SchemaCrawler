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
import static org.mockito.Mockito.withSettings;
import static schemacrawler.test.utility.crawl.LightCatalogUtility.lightNamedObject;

import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.ermodel.model.Relationship;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import schemacrawler.test.utility.crawl.LightColumn;
import schemacrawler.test.utility.crawl.LightColumnReference;
import schemacrawler.test.utility.crawl.LightForeignKey;
import schemacrawler.test.utility.crawl.LightPrimaryKey;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.script.ScriptSupport;

public class ScriptSupportTest {

  private final ScriptSupport support = new ScriptSupport();

  @Test
  public void cardinality() {
    // Null FK → unknown
    assertThat(support.cardinality(null), is(RelationshipCardinality.unknown));

    // FK whose table is a PartialDatabaseObject → unknown
    final Table partialTable =
        mock(Table.class, withSettings().extraInterfaces(PartialDatabaseObject.class));
    final TableReference fkWithPartialTable = mock(TableReference.class);
    when(fkWithPartialTable.getForeignKeyTable()).thenReturn(partialTable);
    assertThat(support.cardinality(fkWithPartialTable), is(RelationshipCardinality.unknown));

    // Non-unique (no PK/index on FK table) + non-optional
    // (LightForeignKey.isOptional() = false)
    // → one_many
    final LightTable pkTable = new LightTable("pk_table");
    final LightTable fkTableOneMay = new LightTable("fk_table_one_many");
    final LightColumn fkCol1 = fkTableOneMay.addColumn("fk_col");
    final LightColumn pkCol1 = pkTable.addColumn("pk_col1");
    final LightForeignKey fkOneMay = new LightForeignKey("FK_ONE_MANY", fkCol1, pkCol1);
    assertThat(support.cardinality(fkOneMay), is(RelationshipCardinality.one_many));

    // Unique (FK col matches FK-table's PK) + non-optional → one_one
    final LightTable fkTableOneOne = new LightTable("fk_table_one_one");
    final LightColumn fkCol2 = fkTableOneOne.addColumn("fk_col");
    final LightColumn pkCol2 = pkTable.addColumn("pk_col2");
    fkTableOneOne.setPrimaryKey(new LightPrimaryKey(fkCol2));
    final LightForeignKey fkOneOne = new LightForeignKey("FK_ONE_ONE", fkCol2, pkCol2);
    assertThat(support.cardinality(fkOneOne), is(RelationshipCardinality.one_one));

    // Non-unique + optional (mocked TableReference) → zero_many
    final LightTable fkTableZeroMany = new LightTable("fk_table_zero_many");
    final LightColumn fkCol3 = fkTableZeroMany.addColumn("fk_col");
    final LightColumn pkCol3 = pkTable.addColumn("pk_col3");
    final LightColumnReference colRef3 = new LightColumnReference(fkCol3, pkCol3);
    final TableReference optionalFkZeroMany = mock(TableReference.class);
    when(optionalFkZeroMany.getForeignKeyTable()).thenReturn(fkTableZeroMany);
    when(optionalFkZeroMany.isOptional()).thenReturn(true);
    when(optionalFkZeroMany.getColumnReferences()).thenReturn(List.of(colRef3));
    when(optionalFkZeroMany.key()).thenReturn(new NamedObjectKey("schema", "FK_ZERO_MANY"));
    assertThat(support.cardinality(optionalFkZeroMany), is(RelationshipCardinality.zero_many));

    // Unique (FK col matches FK-table's PK) + optional → zero_one
    final LightTable fkTableZeroOne = new LightTable("fk_table_zero_one");
    final LightColumn fkCol4 = fkTableZeroOne.addColumn("fk_col");
    final LightColumn pkCol4 = pkTable.addColumn("pk_col4");
    fkTableZeroOne.setPrimaryKey(new LightPrimaryKey(fkCol4));
    final LightColumnReference colRef4 = new LightColumnReference(fkCol4, pkCol4);
    final TableReference optionalFkZeroOne = mock(TableReference.class);
    when(optionalFkZeroOne.getForeignKeyTable()).thenReturn(fkTableZeroOne);
    when(optionalFkZeroOne.isOptional()).thenReturn(true);
    when(optionalFkZeroOne.getColumnReferences()).thenReturn(List.of(colRef4));
    when(optionalFkZeroOne.key()).thenReturn(new NamedObjectKey("schema", "FK_ZERO_ONE"));
    assertThat(support.cardinality(optionalFkZeroOne), is(RelationshipCardinality.zero_one));
  }

  @Test
  public void cardinalitySymbolForRelationship() {
    // Null relationship falls back to unknown → default Mermaid symbol
    assertThat(support.cardinalitySymbol((Relationship) null), is("}o--||"));

    final Relationship rel = mock(Relationship.class);

    when(rel.getType()).thenReturn(RelationshipCardinality.unknown);
    assertThat(support.cardinalitySymbol(rel), is("}o--||"));

    when(rel.getType()).thenReturn(RelationshipCardinality.zero_one);
    assertThat(support.cardinalitySymbol(rel), is("|o--||"));

    when(rel.getType()).thenReturn(RelationshipCardinality.zero_many);
    assertThat(support.cardinalitySymbol(rel), is("}o--||"));

    when(rel.getType()).thenReturn(RelationshipCardinality.one_one);
    assertThat(support.cardinalitySymbol(rel), is("||--||"));

    when(rel.getType()).thenReturn(RelationshipCardinality.one_many);
    assertThat(support.cardinalitySymbol(rel), is("}|--||"));

    when(rel.getType()).thenReturn(RelationshipCardinality.many_many);
    assertThat(support.cardinalitySymbol(rel), is("}o--o{"));
  }

  @Test
  public void cardinalitySymbolForTableReference() {
    // Null FK → unknown cardinality → default Mermaid symbol
    assertThat(support.cardinalitySymbol((TableReference) null), is("}o--||"));

    // Non-unique, non-optional FK → one_many
    final LightTable pkTableSym = new LightTable("pk_table_sym");
    final LightTable fkTableOneMay = new LightTable("fk_table_sym_one_many");
    final LightColumn fkCol1 = fkTableOneMay.addColumn("fk_col");
    final LightColumn pkCol1 = pkTableSym.addColumn("pk_col1");
    final LightForeignKey fkOneMay = new LightForeignKey("FK_SYM_ONE_MANY", fkCol1, pkCol1);
    assertThat(support.cardinalitySymbol(fkOneMay), is("}|--||"));

    // Unique (FK col = PK), non-optional FK → one_one
    final LightTable fkTableOneOne = new LightTable("fk_table_sym_one_one");
    final LightColumn fkCol2 = fkTableOneOne.addColumn("fk_col");
    final LightColumn pkCol2 = pkTableSym.addColumn("pk_col2");
    fkTableOneOne.setPrimaryKey(new LightPrimaryKey(fkCol2));
    final LightForeignKey fkOneOne = new LightForeignKey("FK_SYM_ONE_ONE", fkCol2, pkCol2);
    assertThat(support.cardinalitySymbol(fkOneOne), is("||--||"));
  }

  @Test
  public void cleanFullName() {
    assertThat(support.cleanFullName(null), is(""));

    final LightTable lightTable = new LightTable("tablename");
    assertThat(support.cleanFullName(lightTable), is("tablename"));

    final NamedObject quotedObject = lightNamedObject(NamedObject.class, "\"schema\".\"table\"");
    assertThat(support.cleanFullName(quotedObject), is("schema.table"));
  }

  @Test
  public void cleanName() {
    assertThat(support.cleanName(null), is(""));

    final LightTable lightTable = new LightTable("tablename");
    assertThat(support.cleanName(lightTable), is("tablename"));

    final NamedObject quotedObject = lightNamedObject(NamedObject.class, "\"tablename\"");
    assertThat(support.cleanName(quotedObject), is("tablename"));
  }

  @Test
  public void columnReferences() {
    assertThat(support.columnReferences(null).isEmpty(), is(true));

    // FK with null column references list (via mock)
    final ForeignKey foreignKeyNullReferences = mock(ForeignKey.class);
    when(foreignKeyNullReferences.getColumnReferences()).thenReturn(null);
    assertThat(support.columnReferences(foreignKeyNullReferences).isEmpty(), is(true));

    // LightForeignKey with no column references (table-level constructor)
    final LightTable fkTable = new LightTable("fk_table");
    final LightTable pkTable = new LightTable("pk_table");
    final LightForeignKey emptyFk = new LightForeignKey("FK_NAME", fkTable, pkTable);
    assertThat(support.columnReferences(emptyFk).isEmpty(), is(true));

    // LightForeignKey with a column reference
    final LightColumn fkColumn = fkTable.addColumn("fk_col");
    final LightColumn pkColumn = pkTable.addColumn("pk_col");
    final LightForeignKey fkWithRef = new LightForeignKey("FK_NAME", fkColumn, pkColumn);
    final List<ColumnReference> refs = support.columnReferences(fkWithRef);
    assertThat(refs.size(), is(1));
    assertThat(refs.get(0).getForeignKeyColumn().getName(), is("fk_col"));
    assertThat(refs.get(0).getPrimaryKeyColumn().getName(), is("pk_col"));
  }

  @Test
  public void columns() {
    assertThat(support.columns((Index) null), is(""));
    assertThat(support.columns((PrimaryKey) null), is(""));

    // Use LightColumn (raw cast) so isColumnDataTypeKnown() returns true
    final LightTable table = new LightTable("t");
    final LightColumn col = table.addColumn("INDEX_COL");

    final Index index = mock(Index.class);
    when(index.getColumns()).thenReturn((List) List.of(col));
    assertThat(support.columns(index), containsString("INDEX_COL"));

    // Positive test for columns(PrimaryKey)
    final LightColumn pkCol = table.addColumn("PK_COL");
    final PrimaryKey primaryKey = new LightPrimaryKey(pkCol);
    assertThat(support.columns(primaryKey), containsString("PK_COL"));
  }

  @Test
  public void columnType() {
    assertThat(support.columnType(null), is(""));

    final Column noTypeColumn = mock(Column.class);
    when(noTypeColumn.getColumnDataType()).thenReturn(null);
    assertThat(support.columnType(noTypeColumn), is(""));

    final LightTable table = new LightTable("t");
    final LightColumn varcharColumn = table.addDataColumn("col", "VARCHAR");
    assertThat(support.columnType(varcharColumn), is("VARCHAR"));
  }

  @Test
  public void foreignKeyColumns() {
    assertThat(support.fkColumns(null), is(""));

    // Use LightColumn (raw cast) so isColumnDataTypeKnown() returns true
    final LightTable table = new LightTable("t");
    final LightColumn fkCol = table.addColumn("FK_COL");

    final ForeignKey foreignKey = mock(ForeignKey.class);
    when(foreignKey.getConstrainedColumns()).thenReturn((List) List.of(fkCol));
    when(foreignKey.getName()).thenReturn("FK_TABLE_OTHER");

    assertThat(support.fkColumns(foreignKey), containsString("FK_COL"));

    // System-generated FK name should return hasName = false
    final LightTable fkTable = new LightTable("fk_table");
    final LightTable pkTable = new LightTable("pk_table");
    final LightForeignKey systemFk = new LightForeignKey("SYS_C00001", fkTable, pkTable);
    assertThat(support.hasName(systemFk), is(false));
  }

  @Test
  public void hasName() {

    final ForeignKey fk = mock(ForeignKey.class);
    when(fk.getName()).thenReturn("FK_TABLE_OTHER");

    assertThat(support.hasName(fk), is(true));

    final LightTable table = new LightTable("PK_TABLE");
    final PrimaryKey pk = new LightPrimaryKey(table.addColumn("PKCOL"));

    assertThat(support.hasName(pk), is(true));
  }

  @Test
  public void indent() {
    assertThat(support.indent(null, 2), is(""));
    assertThat(support.indent("x", 2), is("  x\n"));
  }

  @Test
  public void isToMany() {
    // Null FK → unknown cardinality → not to-many
    assertThat(support.isToMany(null), is(false));

    final LightTable pkTableToMany = new LightTable("pk_table_to_many");

    // Non-unique, non-optional → one_many → to-many
    final LightTable fkTableOneMay = new LightTable("fk_table_to_many_one_many");
    final LightColumn fkCol1 = fkTableOneMay.addColumn("fk_col");
    final LightColumn pkCol1 = pkTableToMany.addColumn("pk_col1");
    final LightForeignKey fkOneMay = new LightForeignKey("FK_TO_MANY_ONE_MANY", fkCol1, pkCol1);
    assertThat(support.isToMany(fkOneMay), is(true));

    // Non-unique, optional → zero_many → to-many
    final LightTable fkTableZeroMany = new LightTable("fk_table_to_many_zero_many");
    final LightColumn fkCol2 = fkTableZeroMany.addColumn("fk_col");
    final LightColumn pkCol2 = pkTableToMany.addColumn("pk_col2");
    final LightColumnReference colRef2 = new LightColumnReference(fkCol2, pkCol2);
    final TableReference optionalFkZeroMany = mock(TableReference.class);
    when(optionalFkZeroMany.getForeignKeyTable()).thenReturn(fkTableZeroMany);
    when(optionalFkZeroMany.isOptional()).thenReturn(true);
    when(optionalFkZeroMany.getColumnReferences()).thenReturn(List.of(colRef2));
    when(optionalFkZeroMany.key()).thenReturn(new NamedObjectKey("schema", "FK_TO_MANY_ZERO_MANY"));
    assertThat(support.isToMany(optionalFkZeroMany), is(true));

    // Unique (FK col = PK), non-optional → one_one → not to-many
    final LightTable fkTableOneOne = new LightTable("fk_table_to_many_one_one");
    final LightColumn fkCol3 = fkTableOneOne.addColumn("fk_col");
    final LightColumn pkCol3 = pkTableToMany.addColumn("pk_col3");
    fkTableOneOne.setPrimaryKey(new LightPrimaryKey(fkCol3));
    final LightForeignKey fkOneOne = new LightForeignKey("FK_TO_MANY_ONE_ONE", fkCol3, pkCol3);
    assertThat(support.isToMany(fkOneOne), is(false));

    // Unique (FK col = PK), optional → zero_one → not to-many
    final LightTable fkTableZeroOne = new LightTable("fk_table_to_many_zero_one");
    final LightColumn fkCol4 = fkTableZeroOne.addColumn("fk_col");
    final LightColumn pkCol4 = pkTableToMany.addColumn("pk_col4");
    fkTableZeroOne.setPrimaryKey(new LightPrimaryKey(fkCol4));
    final LightColumnReference colRef4 = new LightColumnReference(fkCol4, pkCol4);
    final TableReference optionalFkZeroOne = mock(TableReference.class);
    when(optionalFkZeroOne.getForeignKeyTable()).thenReturn(fkTableZeroOne);
    when(optionalFkZeroOne.isOptional()).thenReturn(true);
    when(optionalFkZeroOne.getColumnReferences()).thenReturn(List.of(colRef4));
    when(optionalFkZeroOne.key()).thenReturn(new NamedObjectKey("schema", "FK_TO_MANY_ZERO_ONE"));
    assertThat(support.isToMany(optionalFkZeroOne), is(false));
  }

  @Test
  public void nonPrimaryIndexes() {
    assertThat(support.nonPrimaryIndexes(null).isEmpty(), is(true));

    final Table tableWithNullIndexes = mock(Table.class);
    when(tableWithNullIndexes.getIndexes()).thenReturn(null);
    assertThat(support.nonPrimaryIndexes(tableWithNullIndexes).isEmpty(), is(true));

    final Table tableWithNoIndexes = new LightTable("TABLE1");
    assertThat(support.nonPrimaryIndexes(tableWithNoIndexes).isEmpty(), is(true));

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
    assertThat(support.nonPrimaryIndexes(tableWithoutPrimaryKey), is(List.of(indexNoPk)));

    // pkEquivalentIndex shares the same column (COL1) as the primary key
    final PrimaryKey primaryKey = new LightPrimaryKey(col1);

    final Index pkEquivalentIndex = mock(Index.class);
    when(pkEquivalentIndex.getColumns()).thenReturn((List) List.of(col1));

    final Index nonPrimaryIndex = mock(Index.class);
    when(nonPrimaryIndex.getColumns()).thenReturn((List) List.of(col2));

    final Table tableWithPrimaryKey = mock(Table.class);
    when(tableWithPrimaryKey.getIndexes()).thenReturn(List.of(pkEquivalentIndex, nonPrimaryIndex));
    when(tableWithPrimaryKey.hasPrimaryKey()).thenReturn(true);
    when(tableWithPrimaryKey.getPrimaryKey()).thenReturn(primaryKey);
    assertThat(support.nonPrimaryIndexes(tableWithPrimaryKey), is(List.of(nonPrimaryIndex)));
  }

  @Test
  public void pkColumns() {
    assertThat(support.pkColumns(null), is(""));

    // Use LightForeignKey with LightColumns so joinColumns works correctly
    final LightTable fkTable = new LightTable("fk_table");
    final LightTable pkTable = new LightTable("pk_table");
    final LightColumn fkCol = fkTable.addColumn("fk_col");
    final LightColumn pkCol = pkTable.addColumn("PK_COL");
    final LightForeignKey foreignKey = new LightForeignKey("FK_NAME", fkCol, pkCol);

    assertThat(support.pkColumns(foreignKey), containsString("PK_COL"));
  }

  @Test
  public void remarks() {
    assertThat(support.remarks(null), is(""));

    final LightTable noRemarksTable = new LightTable("t");
    assertThat(support.remarks(noRemarksTable), is(""));

    final LightTable tableWithRemarks = new LightTable("t");
    tableWithRemarks.setRemarks("  line1\n\"line2\"  ");
    assertThat(support.remarks(tableWithRemarks), is("line1 'line2'"));
  }

  @Test
  public void stripName() {
    assertThat(support.stripName(null), is(""));

    final NamedObject namedObject = lightNamedObject(NamedObject.class, "abc[^\\d\\w\\-]xyz");
    assertThat(support.stripName(namedObject), is("abcxyz"));
  }

  @Test
  public void type() {
    // getSimpleTypeName(null) returns "unknown", no NPE
    assertThat(support.type(null), is("unknown"));

    final LightTable lightTable = new LightTable("t");
    assertThat(support.type(lightTable), is("table"));
  }
}
