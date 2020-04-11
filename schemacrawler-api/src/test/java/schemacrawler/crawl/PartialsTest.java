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
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;

public class PartialsTest
{

  @Test
  public void tablePartial()
  {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final TablePartial table = new TablePartial(schema, "table");

    final ColumnPartial column = new ColumnPartial(table, "column");
    table.addColumn(column);

    assertThat(table.lookupColumn("column"), isPresentAndIs(column));
    assertThat(table.lookupColumn("unknown_column"), isEmpty());

    for (final String methodName : new String[] {
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
      })
    {
      assertThrows(InvocationTargetException.class,
                   () -> invokeMethod(table, methodName),
                   "Testing partial method, " + methodName);
    }

    for (final String methodName : new String[] {
      "lookupIndex", "lookupPrivilege", "lookupTrigger"
    })
    {
      assertThrows(InvocationTargetException.class,
                   () -> invokeMethod(table, methodName, ""),
                   "Testing partial method, " + methodName);
    }

    assertThrows(InvocationTargetException.class,
                 () -> invokeMethod(table, "getRelatedTables", TableRelationshipType.none),
                 "Testing partial method, getRelatedTables");

  }

  @Test
  public void tablePartialAttributes()
  {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final Table table = new MutableTable(schema, "table");
    table.setAttribute("some_attribute", "some value");
    final TablePartial tablePartial = new TablePartial(table);

    assertThat(tablePartial.getFullName(), is(table.getFullName()));
    assertThat(tablePartial.getName(), is(table.getName()));
    assertThat(tablePartial.getSchema(), is(table.getSchema()));
    assertThat(tablePartial.getAttributes(), is(table.getAttributes()));
  }

  @Test
  public void columnReference()
  {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final TablePartial table = new TablePartial(schema, "table");

    final ColumnPartial columnPartial = new ColumnPartial(table, "column");
    table.addColumn(columnPartial);

    final ColumnReference columnReference = new ColumnReference(columnPartial);

    assertThat(columnReference.get(), is(columnPartial));
  }

  @Test
  public void columnPartial()
  {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final TablePartial table = new TablePartial(schema, "table");
    final Column column = new MutableColumn(table, "column");
    column.setAttribute("some_attribute", "some_value");

    final ColumnPartial columnPartial = new ColumnPartial(column);
    table.addColumn(columnPartial);

    final ColumnPartial columnReferenced = new ColumnPartial(table, "other_column");
    columnPartial.setReferencedColumn(columnReferenced);
    table.addColumn(columnReferenced);

    for (final String methodName : new String[] {
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
      })
    {
      assertThrows(InvocationTargetException.class,
                   () -> invokeMethod(columnPartial, methodName),
                   "Testing partial method, " + methodName);
    }

    for (final String methodName : new String[] {
      "lookupPrivilege",
      })
    {
      assertThrows(InvocationTargetException.class,
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
  public void functionPartial()
  {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final MutableFunction function = new MutableFunction(schema, "function");
    final FunctionPartial functionPartial = new FunctionPartial(function);
    final FunctionReference functionReference = new FunctionReference(function);

    assertThat(functionReference.get(), is(function));

    assertThat(functionPartial.getRoutineType(), is(RoutineType.function));
    assertThat(functionPartial.getRoutineType(), is(functionPartial.getType()));

    for (final String methodName : new String[] {
      "getDefinition", "getRoutineBodyType", "getSpecificName", "hasDefinition", "getParameters", "getReturnType",
      })
    {
      assertThrows(InvocationTargetException.class,
                   () -> invokeMethod(functionPartial, methodName),
                   "Testing partial method, " + methodName);
    }

    for (final String methodName : new String[] {
      "lookupParameter",
      })
    {
      assertThrows(InvocationTargetException.class,
                   () -> invokeMethod(functionPartial, methodName, ""),
                   "Testing partial method, " + methodName);
    }

  }

  @Test
  public void procedurePartial()
  {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final MutableProcedure procedure = new MutableProcedure(schema, "procedure");
    final ProcedurePartial procedurePartial = new ProcedurePartial(procedure);
    final ProcedureReference procedureReference = new ProcedureReference(procedure);

    assertThat(procedureReference.get(), is(procedure));

    assertThat(procedurePartial.getRoutineType(), is(RoutineType.procedure));
    assertThat(procedurePartial.getRoutineType(), is(procedurePartial.getType()));

    for (final String methodName : new String[] {
      "getDefinition", "getRoutineBodyType", "getSpecificName", "hasDefinition", "getParameters", "getReturnType",
      })
    {
      assertThrows(InvocationTargetException.class,
                   () -> invokeMethod(procedurePartial, methodName),
                   "Testing partial method, " + methodName);
    }

    for (final String methodName : new String[] {
      "lookupParameter",
      })
    {
      assertThrows(InvocationTargetException.class,
                   () -> invokeMethod(procedurePartial, methodName, ""),
                   "Testing partial method, " + methodName);
    }

  }

}
