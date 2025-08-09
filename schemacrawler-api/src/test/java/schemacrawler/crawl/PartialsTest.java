/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schemacrawler.SchemaReference;

public class PartialsTest {

  @Test
  public void columnPartial() {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final TablePartial table = new TablePartial(schema, "table");
    final Column column = new MutableColumn(table, "column");
    column.setAttribute("some_attribute", "some_value");

    final ColumnPartial columnPartial = new ColumnPartial(column);
    table.addColumn(columnPartial);

    final ColumnPartial columnReferenced = new ColumnPartial(table, "other_column");
    columnPartial.setReferencedColumn(columnReferenced);
    table.addColumn(columnReferenced);

    for (final String methodName :
        new String[] {
          "getColumnDataType",
          "getDecimalDigits",
          "getDefaultValue",
          "getOrdinalPosition",
          "getPrivileges",
          "getSize",
          "getType",
          "getWidth",
          "isAutoIncremented",
          "isGenerated",
          "isHidden",
          "isNullable",
          "isPartOfForeignKey",
          "isPartOfIndex",
          "isPartOfPrimaryKey",
          "isPartOfUniqueIndex",
        }) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(columnPartial, methodName),
          "Testing partial method, " + methodName);
    }

    for (final String methodName :
        new String[] {
          "lookupPrivilege",
        }) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(columnPartial, methodName, ""),
          "Testing partial method, " + methodName);
    }

    assertThat(columnPartial.getFullName(), is(column.getFullName()));
    assertThat(columnPartial.getSchema(), is(column.getSchema()));
    assertThat(columnPartial.getAttributes(), is(column.getAttributes()));

    assertThat(columnPartial.getParent(), is(table));
    assertThat(columnPartial.getReferencedColumn(), is(columnReferenced));
  }

  @Test
  public void columnReference() {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final TablePartial table = new TablePartial(schema, "table");

    final ColumnPartial columnPartial = new ColumnPartial(table, "column");
    table.addColumn(columnPartial);

    final ColumnPointer columnReference = new ColumnPointer(columnPartial);

    assertThat(columnReference.get(), is(columnPartial));
  }

  @Test
  public void functionPartial() {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final MutableFunction function = new MutableFunction(schema, "function", null);
    final FunctionPartial functionPartial = new FunctionPartial(function);
    final FunctionPointer functionReference = new FunctionPointer(function);

    assertThat(functionReference.get(), is(function));

    assertThat(functionPartial.getRoutineType(), is(RoutineType.function));
    assertThat(functionPartial.getRoutineType(), is(functionPartial.getType()));

    for (final String methodName :
        new String[] {
          "getDefinition",
          "getRoutineBodyType",
          "getSpecificName",
          "hasDefinition",
          "getParameters",
          "getReturnType",
        }) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(functionPartial, methodName),
          "Testing partial method, " + methodName);
    }

    for (final String methodName :
        new String[] {
          "lookupParameter",
        }) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(functionPartial, methodName, ""),
          "Testing partial method, " + methodName);
    }
  }

  @Test
  public void procedurePartial() {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final MutableProcedure procedure = new MutableProcedure(schema, "procedure", null);
    final ProcedurePartial procedurePartial = new ProcedurePartial(procedure);
    final ProcedurePointer procedureReference = new ProcedurePointer(procedure);

    assertThat(procedureReference.get(), is(procedure));

    assertThat(procedurePartial.getRoutineType(), is(RoutineType.procedure));
    assertThat(procedurePartial.getRoutineType(), is(procedurePartial.getType()));

    for (final String methodName :
        new String[] {
          "getDefinition",
          "getRoutineBodyType",
          "getSpecificName",
          "hasDefinition",
          "getParameters",
          "getReturnType",
        }) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(procedurePartial, methodName),
          "Testing partial method, " + methodName);
    }

    for (final String methodName :
        new String[] {
          "lookupParameter",
        }) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(procedurePartial, methodName, ""),
          "Testing partial method, " + methodName);
    }
  }

  @Test
  public void tablePartial() {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final TablePartial table = new TablePartial(schema, "table");

    final ColumnPartial column = new ColumnPartial(table, "column");
    table.addColumn(column);

    assertThat(table.lookupColumn("column"), isPresentAndIs(column));
    assertThat(table.lookupColumn("unknown_column"), isEmpty());

    final ForeignKey foreignKey =
        new MutableForeignKey("fk", new ImmutableColumnReference(1, column, column));
    table.addForeignKey(foreignKey);

    assertThat(table.lookupForeignKey("fk"), isPresentAndIs(foreignKey));
    assertThat(table.lookupForeignKey("unknown_fk"), isEmpty());

    for (final String methodName :
        new String[] {
          "getColumns",
          "getDefinition",
          "getExportedForeignKeys",
          "getForeignKeys",
          "getHiddenColumns",
          "getImportedForeignKeys",
          "getIndexes",
          "getPrimaryKey",
          "getPrivileges",
          "getTableConstraints",
          "getTableType",
          "getTriggers",
          "getType",
          "hasDefinition",
          "hasPrimaryKey",
        }) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(table, methodName),
          "Testing partial method, " + methodName);
    }

    for (final String methodName :
        new String[] {"lookupIndex", "lookupPrivilege", "lookupTrigger"}) {
      assertThrows(
          InvocationTargetException.class,
          () -> invokeMethod(table, methodName, ""),
          "Testing partial method, " + methodName);
    }

    assertThrows(
        InvocationTargetException.class,
        () -> invokeMethod(table, "getRelatedTables", TableRelationshipType.none),
        "Testing partial method, getRelatedTables");
  }

  @Test
  public void tablePartialAttributes() {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final Table table = new MutableTable(schema, "table");
    table.setAttribute("some_attribute", "some value");
    final TablePartial tablePartial = new TablePartial(table);

    assertThat(tablePartial.getFullName(), is(table.getFullName()));
    assertThat(tablePartial.getName(), is(table.getName()));
    assertThat(tablePartial.getSchema(), is(table.getSchema()));
    assertThat(tablePartial.getAttributes(), is(table.getAttributes()));
  }
}
