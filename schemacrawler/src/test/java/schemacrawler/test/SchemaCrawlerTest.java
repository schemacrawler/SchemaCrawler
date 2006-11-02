/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.test;


import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestSuite;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Column;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.test.util.TestBase;
import sf.util.Utilities;

public class SchemaCrawlerTest
  extends TestBase
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerTest.class
      .getName());

  public static Test suite()
  {
    return new TestSuite(SchemaCrawlerTest.class);
  }

  public SchemaCrawlerTest(final String name)
  {
    super(name);
  }

  public void testTableCount()
  {
    LOGGER.log(Level.FINE, dataSource.toString());
    LOGGER.log(Level.FINE, "schemapattern="
        + dataSource.getSourceProperties().getProperty("schemapattern"));
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    LOGGER.log(Level.FINE, schemaCrawlerOptions.toString());
    final Schema schema = SchemaCrawler.getSchema(dataSource, null,
        SchemaInfoLevel.MINIMUM, schemaCrawlerOptions);
    final Table[] tables = schema.getTables();
    final int numTables = tables.length;
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Table count does not match", 5, numTables);

  }

  public void testTableNames()
  {

    final String schemaName = "PUBLIC";
    final String[] tableNames =
    { "CUSTOMER", "CUSTOMERLIST", "INVOICE", "ITEM", "PRODUCT" };
    final String[] tableTypes =
    { "TABLE", "VIEW", "TABLE", "TABLE", "TABLE" };
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final Schema schema = SchemaCrawler.getSchema(dataSource, null,
        SchemaInfoLevel.MINIMUM, schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 5, tables.length);
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      assertEquals("Table name does not match", tableNames[tableIdx], table
          .getName());
      assertEquals("Full table name does not match", schemaName + "."
          + tableNames[tableIdx], table.getFullName());
      assertEquals("Table type does not match", tableTypes[tableIdx], table
          .getType().toString());
    }

  }

  public void testProcedureDefinitions()
  {

    // Set up information schema properties
    Properties informationSchemaProperties = new Properties();
    informationSchemaProperties.setProperty(
        "select.INFORMATION_SCHEMA.ROUTINES", "SELECT "
            + "PROCEDURE_CAT AS ROUTINE_CATALOG, "
            + "PROCEDURE_SCHEM AS ROUTINE_SCHEMA, "
            + "PROCEDURE_NAME AS ROUTINE_NAME, "
            + "\'EXTERNAL\' AS ROUTINE_BODY, "
            + "SPECIFIC_NAME  AS ROUTINE_DEFINITION "
            + "FROM INFORMATION_SCHEMA.SYSTEM_PROCEDURES");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);
    final Schema schema = SchemaCrawler.getSchema(dataSource,
        informationSchemaProperties, SchemaInfoLevel.MAXIMUM,
        schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Procedure[] procedures = schema.getProcedures();
    // assertEquals("Procedure count does not match", 173, procedures.length);
    for (int i = 0; i < procedures.length; i++)
    {
      final Procedure procedure = procedures[i];
      if (Utilities.isBlank(procedure.getDefinition()))
      {
        fail("Procedure definition not found");
      }
    }

  }

  public void testTriggers()
  {

    // Set up information schema properties
    Properties informationSchemaProperties = new Properties();
    informationSchemaProperties
        .setProperty(
            "select.INFORMATION_SCHEMA.TRIGGERS",
            "SELECT "
                + "TRIGGER_CAT AS TRIGGER_CATALOG, "
                + "TRIGGER_SCHEM AS TRIGGER_SCHEMA, "
                + "TRIGGER_NAME, "
                + "TRIGGERING_EVENT AS EVENT_MANIPULATION, "
                + "TABLE_CAT AS EVENT_OBJECT_CATALOG, "
                + "TABLE_SCHEM AS EVENT_OBJECT_SCHEMA, "
                + "TABLE_NAME AS EVENT_OBJECT_TABLE, "
                + "1 AS ACTION_ORDER, "
                + "WHEN_CLAUSE AS ACTION_CONDITION, "
                + "REFERENCING_NAMES AS ACTION_ORIENTATION, "
                + "DESCRIPTION AS ACTION_STATEMENT, "
                + "CASE WHEN TRIGGER_TYPE LIKE \'BEFORE%\' THEN \'BEFORE\' ELSE \'\' END AS CONDITION_TIMING, "
                + "TRIGGER_BODY AS DEFINITION "
                + "FROM INFORMATION_SCHEMA.SYSTEM_TRIGGERS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);
    final Schema schema = SchemaCrawler.getSchema(dataSource,
        informationSchemaProperties, SchemaInfoLevel.MAXIMUM,
        schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 5, tables.length);
    boolean foundTrigger = false;
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      final Trigger[] triggers = table.getTriggers();
      for (int i = 0; i < triggers.length; i++)
      {
        foundTrigger = true;
        final Trigger trigger = triggers[i];
        assertEquals("Triggers full name does not match", "CUSTOMER.SCTRIGGER", trigger
            .getFullName());
        assertEquals("Trigger EventManipulationType does not match",
            EventManipulationType.DELETE, trigger.getEventManipulationType());
      }
    }
    assertTrue("No triggers found", foundTrigger);
  }

  public void testViewDefinitions()
  {

    // Set up information schema properties
    Properties informationSchemaProperties = new Properties();
    informationSchemaProperties.setProperty("select.INFORMATION_SCHEMA.VIEWS",
        "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_VIEWS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final Schema schema = SchemaCrawler.getSchema(dataSource,
        informationSchemaProperties, SchemaInfoLevel.MAXIMUM,
        schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 5, tables.length);
    boolean foundView = false;
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      if (table.getType() == TableType.VIEW)
      {
        foundView = true;
        View view = (View) table;
        if (Utilities.isBlank(view.getDefinition()))
        {
          fail("View definition not found");
        }
      }
    }

  }

  public void testColumns()
  {

    final String schemaName = "PUBLIC";
    final String[][] columnNames =
    {
     { "CUSTOMER.ID", "CUSTOMER.FIRSTNAME", "CUSTOMER.LASTNAME",
      "CUSTOMER.STREET", "CUSTOMER.CITY" },
     { "CUSTOMERLIST.ID", "CUSTOMERLIST.FIRSTNAME", "CUSTOMERLIST.LASTNAME", },
     { "INVOICE.ID", "INVOICE.CUSTOMERID", "INVOICE.TOTAL" },
     { "ITEM.INVOICEID", "ITEM.ITEM", "ITEM.PRODUCTID", "ITEM.QUANTITY",
      "ITEM.COST" },
     { "PRODUCT.ID", "PRODUCT.NAME", "PRODUCT.PRICE" } };

    final String[][] columnDataTypes =
    {
    { "INTEGER", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR" },
    { "INTEGER", "VARCHAR", "VARCHAR" },
    { "INTEGER", "INTEGER", "DECIMAL" },
    { "INTEGER", "INTEGER", "INTEGER", "INTEGER", "DECIMAL" },
    { "INTEGER", "VARCHAR", "DECIMAL" } };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final Schema schema = SchemaCrawler.getSchema(dataSource, null,
        SchemaInfoLevel.BASIC, schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 5, tables.length);
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      final Column[] columns = table.getColumns();
      final String[] columnsNamesForTable = columnNames[tableIdx];
      for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
      {
        final Column column = columns[columnIdx];
        LOGGER.log(Level.FINE, column.toString());
        assertEquals("Column full name does not match",
            columnsNamesForTable[columnIdx], column.getFullName());
        assertEquals("Column name does not match",
            columnsNamesForTable[columnIdx], table.getName() + "."
                + column.getName());
        assertEquals("Column type does not match",
            columnDataTypes[tableIdx][columnIdx], column.getType()
                .getDatabaseSpecificTypeName());
        assertEquals("Column JDBC type does not match",
            columnDataTypes[tableIdx][columnIdx], column.getType()
                .getTypeName());
      }
    }

  }

}
