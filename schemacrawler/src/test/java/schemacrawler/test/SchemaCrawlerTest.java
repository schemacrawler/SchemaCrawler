/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.test;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Column;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.test.TestUtility;

public class SchemaCrawlerTest
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerTest.class
    .getName());

  private static TestUtility testUtility = new TestUtility();

  @AfterClass
  public static void afterAllTests()
    throws ClassNotFoundException
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws ClassNotFoundException
  {
    TestUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void columns()
  {

    final String[][] columnNames = {
        {
            "PUBLIC.CUSTOMER.ID",
            "PUBLIC.CUSTOMER.FIRSTNAME",
            "PUBLIC.CUSTOMER.LASTNAME",
            "PUBLIC.CUSTOMER.STREET",
            "PUBLIC.CUSTOMER.CITY"
        },
        {
            "PUBLIC.CUSTOMERLIST.ID",
            "PUBLIC.CUSTOMERLIST.FIRSTNAME",
            "PUBLIC.CUSTOMERLIST.LASTNAME",
        },
        {
            "PUBLIC.INVOICE.ID",
            "PUBLIC.INVOICE.CUSTOMERID",
            "PUBLIC.INVOICE.TOTAL"
        },
        {
            "PUBLIC.ITEM.INVOICEID",
            "PUBLIC.ITEM.ITEM",
            "PUBLIC.ITEM.PRODUCTID",
            "PUBLIC.ITEM.QUANTITY",
            "PUBLIC.ITEM.COST"
        },
        {
            "PUBLIC.PRODUCT.ID", "PUBLIC.PRODUCT.NAME", "PUBLIC.PRODUCT.PRICE"
        },
        {
            "PUBLIC.SUPPLIER.SUPPLIER_ID", "PUBLIC.SUPPLIER.SUPPLIER_NAME"
        }
    };

    final String[][] columnDataTypes = {
        {
            "INTEGER", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR"
        }, {
            "INTEGER", "VARCHAR", "VARCHAR"
        }, {
            "INTEGER", "INTEGER", "FLOAT"
        }, {
            "INTEGER", "INTEGER", "INTEGER", "INTEGER", "FLOAT"
        }, {
            "INTEGER", "VARCHAR", "FLOAT"
        }, {
            "INTEGER", "VARCHAR"
        }
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 6, tables.length);
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
                     columnsNamesForTable[columnIdx],
                     column.getFullName());
        assertEquals("Column type does not match",
                     columnDataTypes[tableIdx][columnIdx],
                     column.getType().getDatabaseSpecificTypeName());
        assertEquals("Column JDBC type does not match",
                     columnDataTypes[tableIdx][columnIdx],
                     column.getType().getTypeName());
      }
    }

  }

  @Test
  public void procedureDefinitions()
  {

    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setRoutinesSql("SELECT " + "PROCEDURE_CAT AS ROUTINE_CATALOG, "
                      + "PROCEDURE_SCHEM AS ROUTINE_SCHEMA, "
                      + "PROCEDURE_NAME AS ROUTINE_NAME, "
                      + "\'EXTERNAL\' AS ROUTINE_BODY, "
                      + "SPECIFIC_NAME  AS ROUTINE_DEFINITION "
                      + "FROM INFORMATION_SCHEMA.SYSTEM_PROCEDURES");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    final Procedure[] procedures = schema.getProcedures();
    assertTrue("No procedures found", procedures.length > 0);
    for (final Procedure procedure: procedures)
    {
      assertNotNull("Procedure definition not found", procedure.getDefinition());
      assertFalse("Procedure definition not found", procedure.getDefinition()
        .trim().equals(""));
    }

  }

  @Test
  public void schemaEquals()
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.verbose());
    final Schema schema1 = testUtility
      .getSchema(schemaCrawlerOptions, "PUBLIC");
    assertTrue("Could not find any tables", schema1.getTables().length > 0);
    assertTrue("Could not find any procedures",
               schema1.getProcedures().length > 0);

    final Schema schema2 = testUtility
      .getSchema(schemaCrawlerOptions, "PUBLIC");

    assertEquals("Schema not not match", schema1, schema2);
    assertArrayEquals("Tables do not match", schema1.getTables(), schema2
      .getTables());
    assertArrayEquals("Procedures do not match",
                      schema1.getProcedures(),
                      schema2.getProcedures());

    // Try negative test
    final Table table1 = schema1.getTables()[0];
    final Table table2 = schema1.getTables()[1];
    assertFalse("Tables should not be equal", table1.equals(table2));

  }

  @Test
  public void tableCount()
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.minimum());
    final Schema publicSchema = testUtility.getSchema(schemaCrawlerOptions,
                                                      "PUBLIC");
    final Table[] tables = publicSchema.getTables();
    final int numTables = tables.length;
    assertNotNull("Could not obtain schema", publicSchema);
    assertEquals("Table count does not match", 6, numTables);

    assertEquals("Table count does not match", 2, testUtility
      .getSchema(schemaCrawlerOptions, "SCHEMACRAWLER").getTables().length);
  }

  @Test
  public void tableNames()
  {

    final String schemaName = "PUBLIC";
    final String[] tableNames = {
        "CUSTOMER", "CUSTOMERLIST", "INVOICE", "ITEM", "PRODUCT", "SUPPLIER"
    };
    final String[] tableTypes = {
        "TABLE", "VIEW", "TABLE", "TABLE", "TABLE", "TABLE"
    };
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.minimum());
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 6, tables.length);
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      assertEquals("Table name does not match", tableNames[tableIdx], table
        .getName());
      assertEquals("Full table name does not match",
                   schemaName + "." + tableNames[tableIdx],
                   table.getFullName());
      assertEquals("Table type does not match", tableTypes[tableIdx], table
        .getType().toString().toUpperCase(Locale.ENGLISH));
    }
  }

  @Test
  public void triggers()
  {

    // Set up information schema properties
    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setTriggersSql("SELECT "
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
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    final Table[] tables = schema.getTables();
    boolean foundTrigger = false;
    for (final Table table: tables)
    {
      final Trigger[] triggers = table.getTriggers();
      for (final Trigger trigger: triggers)
      {
        foundTrigger = true;
        assertEquals("Triggers full name does not match",
                     "PUBLIC.CUSTOMER.SCTRIGGER",
                     trigger.getFullName());
        assertEquals("Trigger EventManipulationType does not match",
                     EventManipulationType.delete,
                     trigger.getEventManipulationType());
      }
    }
    assertTrue("No triggers found", foundTrigger);
  }

  @Test
  public void viewDefinitions()
  {
    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setViewsSql("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_VIEWS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setTableTypesString("VIEW");
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    final View view = (View) schema.getTable("CUSTOMERLIST");
    assertNotNull("View not found", view);
    assertNotNull("View definition not found", view.getDefinition());
    assertFalse("View definition not found", view.getDefinition().trim()
      .equals(""));
  }

}
