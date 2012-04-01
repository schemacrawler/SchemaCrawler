/*
 * SchemaCrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.TestDatabase;

public class SchemaCrawlerGrepTest
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerGrepTest.class.getName());

  private static TestDatabase testDatabase = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testDatabase.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testDatabase.startMemoryDatabase();
  }

  @Test
  public void grepColumnsAndIncludeChildTables()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.BOOKAUTHORS\\..*", ""));

    Database database;
    Schema schema;
    Table table;

    database = testDatabase.getDatabase(schemaCrawlerOptions);
    schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull("Schema PUBLIC.BOOKS not found", schema);
    assertEquals(1, schema.getTables().length);
    table = schema.getTable("BOOKAUTHORS");
    assertNotNull("Table BOOKAUTHORS not found", table);

    schemaCrawlerOptions.setParentTableFilterDepth(1);
    database = testDatabase.getDatabase(schemaCrawlerOptions);
    schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull("Schema PUBLIC.BOOKS not found", schema);
    assertEquals(3, schema.getTables().length);
    table = schema.getTable("BOOKAUTHORS");
    assertNotNull("Table BOOKAUTHORS not found", table);
    table = schema.getTable("BOOKS");
    assertNotNull("Table BOOKS not found", table);
    table = schema.getTable("AUTHORS");
    assertNotNull("Table AUTHORS not found", table);

  }

  @Test
  public void grepCombined()
    throws Exception
  {
    final String[] schemaNames = {
        "BOOKS",
        "FOR_LINT",
        "INFORMATION_SCHEMA",
        "PUBLIC",
        "\"PUBLISHER SALES\"",
        "SYSTEM_LOBS"
    };
    final int[] tableCounts = {
        2, 0, 0, 0, 1, 0
    };
    final String[][][] columnNames = {
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
                "BOOKAUTHORS.BOOKID",
                "BOOKAUTHORS.AUTHORID",
                "BOOKAUTHORS.\"UPDATE\"",
            },
        },
        {},
        {},
        {},
        {
          {
              "SALES.POSTALCODE",
              "SALES.COUNTRY",
              "SALES.BOOKID",
              "SALES.PERIODENDDATE",
              "SALES.TOTALAMOUNT",
          },
        },
        {},
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\..*\\.BOOKID", ""));
    schemaCrawlerOptions
      .setGrepDefinitionInclusionRule(new InclusionRule(".*book author.*", ""));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      assertEquals("Schema name does not match",
                   "PUBLIC." + schemaNames[schemaIdx],
                   schema.getName());
      final Table[] tables = schema.getTables();
      assertEquals("Table count does not match for schema " + schema,
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
          assertEquals("Column full name does not match",
                       "PUBLIC." + schemaNames[schemaIdx] + "."
                           + columnsNamesForTable[columnIdx],
                       column.getFullName());
        }
      }
    }
  }

  @Test
  public void grepDefinitions()
    throws Exception
  {
    final String[] schemaNames = {
        "BOOKS",
        "FOR_LINT",
        "INFORMATION_SCHEMA",
        "PUBLIC",
        "\"PUBLISHER SALES\"",
        "SYSTEM_LOBS"
    };
    final int[] tableCounts = {
        1, 0, 0, 0, 0, 0
    };
    final String[][][] columnNames = {
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
        },
        {},
        {},
        {},
        {},
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepDefinitionInclusionRule(new InclusionRule(".*book author.*", ""));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      assertEquals("Schema name does not match",
                   "PUBLIC." + schemaNames[schemaIdx],
                   schema.getName());
      final Table[] tables = schema.getTables();
      assertEquals("Table count does not match for schema " + schema,
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
          assertEquals("Column full name does not match",
                       "PUBLIC." + schemaNames[schemaIdx] + "."
                           + columnsNamesForTable[columnIdx],
                       column.getFullName());
        }
      }
    }
  }

  @Test
  public void grepProcedures()
    throws Exception
  {
    final String[] schemaNames = {
        "BOOKS",
        "FOR_LINT",
        "INFORMATION_SCHEMA",
        "PUBLIC",
        "\"PUBLISHER SALES\"",
        "SYSTEM_LOBS"
    };
    final int[] procedureCounts = {
        0, 0, 0, 0, 0, 3
    };
    final String[][][] columnNames = {
        {},
        {},
        {},
        {},
        {},
        {
            {
                "ALLOC_BLOCKS.B_COUNT",
                "ALLOC_BLOCKS.B_OFFSET",
                "ALLOC_BLOCKS.L_ID",
            },
            {
                "CONVERT_BLOCK.B_ADDR",
                "CONVERT_BLOCK.B_COUNT",
                "CONVERT_BLOCK.B_OFFSET",
                "CONVERT_BLOCK.L_ID",
            },
            {
                "CREATE_EMPTY_BLOCK.B_ADDR", "CREATE_EMPTY_BLOCK.B_COUNT",
            },
        },
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepProcedureColumnInclusionRule(new InclusionRule(".*\\.B_COUNT", ""));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      assertEquals("Schema name does not match",
                   "PUBLIC." + schemaNames[schemaIdx],
                   schema.getName());
      final Procedure[] procedures = schema.getProcedures();
      assertEquals("Procedure count does not match for schema " + schema,
                   procedureCounts[schemaIdx],
                   procedures.length);
      for (int procedureIdx = 0; procedureIdx < procedures.length; procedureIdx++)
      {
        final Procedure procedure = procedures[procedureIdx];
        final ProcedureColumn[] columns = procedure.getColumns();
        final String[] columnsNamesForProcedure = columnNames[schemaIdx][procedureIdx];
        for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
        {
          final ProcedureColumn column = columns[columnIdx];
          LOGGER.log(Level.FINE, column.toString());
          assertEquals("Procedure column full name does not match",
                       "PUBLIC." + schemaNames[schemaIdx] + "."
                           + columnsNamesForProcedure[columnIdx],
                       column.getFullName());
        }
      }
    }
  }

  @Test
  public void grepColumns()
    throws Exception
  {
    final String[] schemaNames = {
        "BOOKS",
        "FOR_LINT",
        "INFORMATION_SCHEMA",
        "PUBLIC",
        "\"PUBLISHER SALES\"",
        "SYSTEM_LOBS"
    };
    final int[] tableCounts = {
        1, 0, 0, 0, 1, 0
    };
    final String[][][] columnNames = {
        {
          {
              "BOOKAUTHORS.BOOKID",
              "BOOKAUTHORS.AUTHORID",
              "BOOKAUTHORS.\"UPDATE\"",
          },
        },
        {},
        {},
        {},
        {
          {
              "SALES.POSTALCODE",
              "SALES.COUNTRY",
              "SALES.BOOKID",
              "SALES.PERIODENDDATE",
              "SALES.TOTALAMOUNT",
          },
        },
        {},
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\..*\\.BOOKID", ""));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      assertEquals("Schema name does not match",
                   "PUBLIC." + schemaNames[schemaIdx],
                   schema.getName());
      final Table[] tables = schema.getTables();
      assertEquals("Table count does not match for schema " + schema,
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
          assertEquals("Column full name does not match",
                       "PUBLIC." + schemaNames[schemaIdx] + "."
                           + columnsNamesForTable[columnIdx],
                       column.getFullName());
        }
      }
    }
  }

}
