/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.crawl.InformationSchemaViews;
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
import sf.util.Utilities;
import dbconnector.datasource.PropertiesDataSourceException;
import dbconnector.test.TestUtility;

public class SchemaCrawlerTest
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerTest.class
    .getName());

  private static TestUtility testUtility = new TestUtility();

  @AfterClass
  public static void afterAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void columns()
  {

    final String[][] columnNames = {
        {
            "CUSTOMER.ID",
            "CUSTOMER.FIRSTNAME",
            "CUSTOMER.LASTNAME",
            "CUSTOMER.STREET",
            "CUSTOMER.CITY"
        },
        {
            "CUSTOMERLIST.ID",
            "CUSTOMERLIST.FIRSTNAME",
            "CUSTOMERLIST.LASTNAME",
        },
        {
            "INVOICE.ID", "INVOICE.CUSTOMERID", "INVOICE.TOTAL"
        },
        {
            "ITEM.INVOICEID",
            "ITEM.ITEM",
            "ITEM.PRODUCTID",
            "ITEM.QUANTITY",
            "ITEM.COST"
        },
        {
            "PRODUCT.ID", "PRODUCT.NAME", "PRODUCT.PRICE"
        },
        {
            "SUPPLIER.SUPPLIER_ID", "SUPPLIER.SUPPLIER_NAME"
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
    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.basic,
                                                  schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
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
        assertEquals("Column name does not match",
                     columnsNamesForTable[columnIdx],
                     table.getName() + "." + column.getName());
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

    // Set up information schema properties
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
    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.maximum,
                                                  schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Procedure[] procedures = schema.getProcedures();
    // assertEquals("Procedure count does not match", 173,
    // procedures.length);
    for (final Procedure procedure: procedures)
    {
      if (Utilities.isBlank(procedure.getDefinition()))
      {
        fail("Procedure definition not found");
      }
    }

  }

  @Test
  public void tableCount()
  {
    LOGGER.log(Level.FINE, testUtility.getDataSource().toString());
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    LOGGER.log(Level.FINE, schemaCrawlerOptions.toString());
    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.minimum,
                                                  schemaCrawlerOptions);
    final Table[] tables = schema.getTables();
    final int numTables = tables.length;
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Table count does not match", 6, numTables);

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
    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.minimum,
                                                  schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
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
        .getType().toString().toUpperCase());
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
    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.maximum,
                                                  schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Table[] tables = schema.getTables();
    boolean foundTrigger = false;
    for (final Table table: tables)
    {
      final Trigger[] triggers = table.getTriggers();
      for (final Trigger trigger: triggers)
      {
        foundTrigger = true;
        assertEquals("Triggers full name does not match",
                     "CUSTOMER.SCTRIGGER",
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

    // Set up information schema properties
    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setViewsSql("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_VIEWS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.maximum,
                                                  schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 6, tables.length);
    boolean foundView = false;
    for (final Table table: tables)
    {
      if (table.getType() == TableType.view)
      {
        foundView = true;
        final View view = (View) table;
        if (Utilities.isBlank(view.getDefinition()))
        {
          fail("View definition not found");
        }
      }
    }
    if (!foundView)
    {
      fail("No views found");
    }

  }
}
