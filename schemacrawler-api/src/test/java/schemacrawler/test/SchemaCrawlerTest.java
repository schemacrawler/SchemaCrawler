/*
 * SchemaCrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.TestDatabase;
import sf.util.Utility;

public class SchemaCrawlerTest
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerTest.class
    .getName());

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void columns()
    throws Exception
  {
    final String[] schemaNames = {
        "INFORMATION_SCHEMA", "PUBLIC", "SALES"
    };
    final int[] tableCounts = {
        0, 6, 2
    };
    final String[][][] columnNames = {
        {},
        {
            {
                "AUTHORS.ID",
                "AUTHORS.FIRSTNAME",
                "AUTHORS.LASTNAME",
                "AUTHORS.ADDRESS1",
                "AUTHORS.ADDRESS2",
                "AUTHORS.CITY",
                "AUTHORS.STATE",
                "AUTHORS.POSTALCODE",
                "AUTHORS.COUNTRY",
            },
            {
                "AUTHORSLIST.ID",
                "AUTHORSLIST.FIRSTNAME",
                "AUTHORSLIST.LASTNAME",
            },
            {
                "BOOKAUTHORS.BOOKID",
                "BOOKAUTHORS.AUTHORID",
                "BOOKAUTHORS.\"UPDATE\"",
            },
            {
                "BOOKS.ID",
                "BOOKS.TITLE",
                "BOOKS.DESCRIPTION",
                "BOOKS.PUBLISHERID",
                "BOOKS.PUBLICATIONDATE",
                "BOOKS.PRICE",
            },
            {
              "\"Global Counts\".\"Global Count\"",
            },
            {
                "PUBLISHERS.ID", "PUBLISHERS.PUBLISHER",
            },
        },
        {
            {
                "REGIONS.CITY",
                "REGIONS.STATE",
                "REGIONS.POSTALCODE",
                "REGIONS.COUNTRY",
            },
            {
                "SALES.POSTALCODE",
                "SALES.COUNTRY",
                "SALES.BOOKID",
                "SALES.PERIODENDDATE",
                "SALES.TOTALAMOUNT",
            },
        },
    };
    final String[][][] columnDataTypes = {
        {},
        {

            {
                "INTEGER",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
            },
            {
                "INTEGER", "VARCHAR", "VARCHAR",
            },
            {
                "INTEGER", "INTEGER", "INTEGER",
            },
            {
                "INTEGER", "VARCHAR", "VARCHAR", "INTEGER", "DATE", "FLOAT"
            },
            {
              "INTEGER",
            },
            {
                "INTEGER", "VARCHAR",
            },
        },
        {
            {
                "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR"
            }, {
                "VARCHAR", "VARCHAR", "INTEGER", "DATE", "FLOAT",
            },
        },
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();

    final Database database = testUtility.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 3, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      assertEquals("Schema name does not match", schemaNames[schemaIdx], schema
        .getName());
      final Table[] tables = schema.getTables();
      assertEquals("Table count does not match",
                   tableCounts[schemaIdx],
                   tables.length);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        final Column[] columns = table.getColumns();
        final String[] columnsNamesForTable = columnNames[schemaIdx][tableIdx];
        for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
        {
          final Column column = columns[columnIdx];
          LOGGER.log(Level.FINE, column.toString());
          assertEquals("Column full name does not match for column " + column,
                       schemaNames[schemaIdx] + "."
                           + columnsNamesForTable[columnIdx],
                       column.getFullName());
          assertEquals("Column type does not match for column " + column,
                       columnDataTypes[schemaIdx][tableIdx][columnIdx],
                       column.getType().getDatabaseSpecificTypeName());
          assertEquals("Column JDBC type does not match",
                       columnDataTypes[schemaIdx][tableIdx][columnIdx],
                       column.getType().getTypeName());
        }
      }
    }
  }

  @Test
  public void counts()
    throws Exception
  {

    final int[] tableCounts = {
        0, 6, 2
    };
    final int[][] tableColumnCounts = {
        {}, {
            9, 3, 3, 6, 1, 2
        }, {
            4, 5
        }
    };
    final int[][] checkConstraints = {
        {}, {
            0, 0, 0, 0, 0, 0, 0
        }, {
            0, 0
        }
    };
    final int[][] indexCounts = {
        {}, {
            2, 0, 3, 0, 0, 0,
        }, {
            0, 1
        }
    };
    final int[][] fkCounts = {
        {}, {
            1, 0, 2, 1, 0, 0,
        }, {
            1, 1
        }
    };
    final int[][] exportedFkCounts = {
        {}, {
            1, 0, 0, 1, 0, 0,
        }, {
            1, 0
        }
    };
    final int[][] importedFkCounts = {
        {}, {
            0, 0, 2, 0, 0, 0,
        }, {
            0, 1
        }
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Database database = testUtility.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 3, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      final Table[] tables = schema.getTables();
      assertEquals("Table count does not match",
                   tableCounts[schemaIdx],
                   tables.length);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        assertEquals(String.format("Table %s columns count does not match",
                                   table.getFullName()),
                     tableColumnCounts[schemaIdx][tableIdx],
                     table.getColumns().length);
        assertEquals(String
          .format("Table %s check constraints count does not match", table
            .getFullName()), checkConstraints[schemaIdx][tableIdx], table
          .getCheckConstraints().length);
        assertEquals(String.format("Table %s index count does not match", table
                       .getFullName()),
                     indexCounts[schemaIdx][tableIdx],
                     table.getIndices().length);
        assertEquals(String.format("Table %s foreign key count does not match",
                                   table.getFullName()),
                     fkCounts[schemaIdx][tableIdx],
                     table.getForeignKeys().length);
        assertEquals(String
          .format("Table %s exported foreign key count does not match", table
            .getFullName()), exportedFkCounts[schemaIdx][tableIdx], table
          .getExportedForeignKeys().length);
        assertEquals(String
          .format("Table %s imported foreign key count does not match", table
            .getFullName()), importedFkCounts[schemaIdx][tableIdx], table
          .getImportedForeignKeys().length);
      }
    }
  }

  @Test
  public void procedureDefinitions()
    throws Exception
  {

    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews.setRoutinesSql(Utility
      .readResourceFully("/procedure_definitions.sql"));

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    final Procedure[] procedures = schema.getProcedures();
    assertTrue("No procedures found", procedures.length > 0);
    for (final Procedure procedure: procedures)
    {
      assertNotNull("Procedure definition is null, for "
                    + procedure.getFullName(), procedure.getDefinition());
      assertFalse("Procedure definition not found, for "
                  + procedure.getFullName(), procedure.getDefinition().trim()
        .equals(""));
    }

  }

  @Test
  public void schemaEquals()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.detailed());
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
  public void tables()
    throws Exception
  {

    final String[] schemaNames = {
        "INFORMATION_SCHEMA", "PUBLIC", "SALES"
    };
    final String[][] tableNames = {
        {},
        {

            "AUTHORS",
            "AUTHORSLIST",
            "BOOKAUTHORS",
            "BOOKS",
            "\"Global Counts\"",
            "PUBLISHERS",
        },
        {
            "REGIONS", "SALES",
        }
    };
    final String[][] tableTypes = {
        {}, {
            "TABLE", "VIEW", "TABLE", "TABLE", "TABLE", "TABLE",
        }, {
            "TABLE", "TABLE",
        }
    };
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());

    final Database database = testUtility.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 3, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      assertEquals("Schema name does not match", schemaNames[schemaIdx], schema
        .getName());
      final Table[] tables = schema.getTables();
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        assertEquals("Table name does not match",
                     tableNames[schemaIdx][tableIdx],
                     table.getName());
        assertEquals("Full table name does not match",
                     schema.getName() + "." + tableNames[schemaIdx][tableIdx],
                     table.getFullName());
        assertEquals("Table type does not match",
                     tableTypes[schemaIdx][tableIdx],
                     table.getType().toString().toUpperCase(Locale.ENGLISH));
      }
    }
  }

  @Test
  public void tablesSort()
    throws Exception
  {

    final String[] tableNames = {
        "\"Global Counts\"",
        "PUBLISHERS",
        "AUTHORS",
        "BOOKS",
        "BOOKAUTHORS",
        "AUTHORSLIST",
    };
    final Random rnd = new Random();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForTables(false);

    final Database database = testUtility.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 3, schemas.length);
    final Schema schema = schemas[1];

    for (int i = 0; i < tableNames.length; i++)
    {
      final String tableName1 = tableNames[i];
      for (int j = 0; j < tableNames.length; j++)
      {
        final String tableName2 = tableNames[j];
        assertEquals(tableName1 + " <--> " + tableName2, Math.signum(schema
          .getTable(tableName1).compareTo(schema.getTable(tableName2))), Math
          .signum(i - j), 1e-100);
      }
    }

    final Table[] tables = schema.getTables();
    for (int i = 0; i < 10; i++)
    {
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        assertEquals("Table name does not match in iteration " + i,
                     tableNames[tableIdx],
                     table.getName());
      }

      // Shuffle array, and sort it again
      for (int k = tables.length; k > 1; k--)
      {
        final int i1 = k - 1;
        final int i2 = rnd.nextInt(k);
        final Table tmp = tables[i1];
        tables[i1] = tables[i2];
        tables[i2] = tmp;
      }
      Arrays.sort(tables);
    }
  }

  @Test
  public void triggers()
    throws Exception
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
                     "PUBLIC.AUTHORS.TRG_AUTHORS",
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
    throws Exception
  {
    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setViewsSql("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_VIEWS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setTableTypesString("VIEW");
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    assertNotNull("Schema not found", schema);
    final View view = (View) schema.getTable("AUTHORSLIST");
    assertNotNull("View not found", view);
    assertNotNull("View definition not found", view.getDefinition());
    assertFalse("View definition not found", view.getDefinition().trim()
      .equals(""));
  }

}
