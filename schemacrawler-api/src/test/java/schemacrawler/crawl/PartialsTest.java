package schemacrawler.crawl;


import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.TableRelationshipType;

public class PartialsTest
{

  @Test
  public void tablePartial()
  {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final TablePartial table = new TablePartial(schema, "table");

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
  public void columnPartial()
  {
    final SchemaReference schema = new SchemaReference("catalog", "schema");
    final TablePartial table = new TablePartial(schema, "table");
    final ColumnPartial column = new ColumnPartial(table, "table");
    table.addColumn(column);

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
                   () -> invokeMethod(column, methodName),
                   "Testing partial method, " + methodName);
    }

    for (final String methodName : new String[] {
      "lookupPrivilege",
    })
    {
      assertThrows(InvocationTargetException.class,
                   () -> invokeMethod(column, methodName, ""),
                   "Testing partial method, " + methodName);
    }

  }

}
