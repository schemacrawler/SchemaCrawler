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


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.TestDatabase;
import schemacrawler.test.utility.TestUtility;
import sf.util.Utility;

public class SchemaCrawlerTest
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerTest.class
    .getName());

  private static final String METADATA_OUTPUT = "metadata/";

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
  public void checkConstraints()
    throws Exception
  {

    final int[] tableCounts = {
        6, 5, 0, 0, 2, 0
    };
    final int[][] checkConstraintCounts = {
        {
            4, 0, 2, 3, 0, 1, 0
        }, {
            3, 0, 0, 3, 6,
        }, {}, {}, {
            4, 2
        }, {}
    };
    final String[][][] checkConstraintNames = {
        {
            {
                "CHECK_UPPERCASE_STATE",
                "SYS_CT_10032",
                "SYS_CT_10033",
                "SYS_CT_10034"
            },
            {},
            {
                "SYS_CT_10040", "SYS_CT_10041"
            },
            {
                "SYS_CT_10036", "SYS_CT_10037", "SYS_CT_10038"
            },
            {},
            {
              "SYS_CT_10030"
            },
            {}
        },
        {
            {
                "SYS_CT_10077", "SYS_CT_10078", "SYS_CT_10079"
            },
            {},
            {},
            {
                "SYS_CT_10069", "SYS_CT_10070", "SYS_CT_10071",
            },
            {
                "CHECK_UPPERCASE_STATE",
                "SYS_CT_10063",
                "SYS_CT_10064",
                "SYS_CT_10065",
                "SYS_CT_10066",
                "SYS_CT_10067",
            },
        },
        {},
        {},
        {
            {
                "SYS_CT_10053", "SYS_CT_10054", "SYS_CT_10055", "SYS_CT_10056"
            }, {
                "SYS_CT_10058", "SYS_CT_10059"
            }
        },
        {}
    };

    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setTableConstraintsSql("SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS");
    informationSchemaViews
      .setCheckConstraintsSql("SELECT * FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 6, schemas.length);
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
        final CheckConstraint[] checkConstraints = table.getCheckConstraints();
        assertEquals(String.format("Table [%d][%d] %s check constraints count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     checkConstraintCounts[schemaIdx][tableIdx],
                     checkConstraints.length);
        for (int i = 0; i < checkConstraints.length; i++)
        {
          final CheckConstraint checkConstraint = checkConstraints[i];
          assertEquals("Check constraint name does not match for table "
                           + table,
                       checkConstraintNames[schemaIdx][tableIdx][i],
                       checkConstraint.getName());
        }
      }
    }
  }

  @Test
  public void columnLookup()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    assertNotNull(database);
    final Schema schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull(schema);
    final Table table = schema.getTable("AUTHORS");
    assertNotNull(table);
    assertNull(table.getColumn(null));
    assertNull(table.getColumn(""));
    assertNull(table.getColumn("NO_COLUMN"));
    assertNotNull(table.getColumn("ID"));
  }

  @Test
  public void counts()
    throws Exception
  {

    final int[] tableCounts = {
        6, 0, 0, 2, 0,
    };
    final int[][] tableColumnCounts = {
        {
            9, 3, 3, 6, 1, 2
        }, {}, {}, {
            4, 5
        }, {},
    };
    final int[][] checkConstraints = {
        {
            4, 0, 2, 3, 0, 1, 0
        }, {}, {}, {
            4, 2
        }, {},
    };
    final int[][] indexCounts = {
        {
            3, 0, 3, 1, 0, 1,
        }, {}, {}, {
            1, 1
        }, {},
    };
    final int[][] fkCounts = {
        {
            1, 0, 2, 1, 0, 0,
        }, {}, {}, {
            1, 1
        }, {},
    };
    final int[][] exportedFkCounts = {
        {
            1, 0, 0, 1, 0, 0,
        }, {}, {}, {
            1, 0
        }, {},
    };
    final int[][] importedFkCounts = {
        {
            0, 0, 2, 0, 0, 0,
        }, {}, {}, {
            0, 1
        }, {},
    };
    final int[][] tablePrivilegesCounts = {
        {
            6, 6, 6, 6, 6, 6, 6
        }, {}, {}, {
            6, 6,
        }, {},
    };

    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setTableConstraintsSql("SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS");
    informationSchemaViews
      .setCheckConstraintsSql("SELECT * FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                ".*\\.FOR_LINT"));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 5, schemas.length);
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
        assertEquals(String.format("Table [%d][%d] %s columns count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     tableColumnCounts[schemaIdx][tableIdx],
                     table.getColumns().length);
        assertEquals(String.format("Table [%d][%d] %s check constraints count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     checkConstraints[schemaIdx][tableIdx],
                     table.getCheckConstraints().length);
        assertEquals(String.format("Table [%d][%d] %s index count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     indexCounts[schemaIdx][tableIdx],
                     table.getIndices().length);
        assertEquals(String.format("Table [%d][%d] %s foreign key count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     fkCounts[schemaIdx][tableIdx],
                     table.getForeignKeys().length);
        assertEquals(String.format("Table [%d][%d] %s exported foreign key count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     exportedFkCounts[schemaIdx][tableIdx],
                     table.getExportedForeignKeys().length);
        assertEquals(String.format("Table [%d][%d] %s imported foreign key count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     importedFkCounts[schemaIdx][tableIdx],
                     table.getImportedForeignKeys().length);
        assertEquals(String.format("Table [%d][%d] %s privileges count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     tablePrivilegesCounts[schemaIdx][tableIdx],
                     table.getPrivileges().length);
      }
    }
  }

  @Test
  public void procedureDefinitions()
    throws Exception
  {

    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setRoutinesSql("SELECT * FROM INFORMATION_SCHEMA.ROUTINES");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Schema schema = testDatabase.getSchema(schemaCrawlerOptions,
                                                 "PUBLIC.BOOKS");
    final Procedure[] procedures = schema.getProcedures();
    assertEquals("Wrong number of procedures", 1, procedures.length);
    for (final Procedure procedure: procedures)
    {
      assertFalse("Procedure definition not found, for " + procedure,
                  Utility.isBlank(procedure.getDefinition()));
    }
  }

  @Test
  public void schemaEquals()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.detailed());
    final Schema schema1 = testDatabase.getSchema(schemaCrawlerOptions,
                                                  "PUBLIC.BOOKS");
    assertTrue("Could not find any tables", schema1.getTables().length > 0);
    assertEquals("Wrong number of procedures",
                 1,
                 schema1.getProcedures().length);

    final Schema schema2 = testDatabase.getSchema(schemaCrawlerOptions,
                                                  "PUBLIC.BOOKS");

    assertEquals("Schema not not match", schema1, schema2);
    assertArrayEquals("Tables do not match",
                      schema1.getTables(),
                      schema2.getTables());
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

    final String referenceFile = "tables.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(testOutputFile)));

    final Config config = Config
      .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                ".*\\.FOR_LINT"));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 5, schemas.length);
    for (final Schema schema: schemas)
    {
      final Table[] tables = schema.getTables();
      for (final Table table: tables)
      {
        writer.println(String.format("o--> %s [%s]",
                                     table.getFullName(),
                                     table.getType()));
        final SortedMap<String, Object> tableAttributes = new TreeMap<String, Object>(table
          .getAttributes());
        for (final Entry<String, Object> tableAttribute: tableAttributes
          .entrySet())
        {
          writer.println(String.format("      ~ %s=%s",
                                       tableAttribute.getKey(),
                                       tableAttribute.getValue()));
        }
        final Column[] columns = table.getColumns();
        for (Column column: columns)
        {
          writer.println(String.format("   o--> %s [%s]",
                                       column.getFullName(),
                                       column.getType()));
          final SortedMap<String, Object> columnAttributes = new TreeMap<String, Object>(column
            .getAttributes());
          for (final Entry<String, Object> columnAttribute: columnAttributes
            .entrySet())
          {
            writer.println(String.format("          ~ %s=%s",
                                         columnAttribute.getKey(),
                                         columnAttribute.getValue()));
          }
        }
      }
    }

    writer.flush();
    writer.close();

    final List<String> failures = TestUtility
      .compareOutput(METADATA_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void tablesSort()
    throws Exception
  {

    final String[] tableNames = {
        "AUTHORS",
        "BOOKS",
        "\"Global Counts\"",
        "PUBLISHERS",
        "BOOKAUTHORS",
        "AUTHORSLIST",
    };
    final Random rnd = new Random();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForTables(false);
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                ".*\\.FOR_LINT"));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 5, schemas.length);
    final Schema schema = schemas[0];

    for (int i = 0; i < tableNames.length; i++)
    {
      final String tableName1 = tableNames[i];
      for (int j = 0; j < tableNames.length; j++)
      {
        final String tableName2 = tableNames[j];
        assertEquals(tableName1 + " <--> " + tableName2,
                     Math.signum(schema.getTable(tableName1).compareTo(schema
                       .getTable(tableName2))),
                     Math.signum(i - j),
                     1e-100);
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
      .setTriggersSql("SELECT * FROM INFORMATION_SCHEMA.TRIGGERS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final Schema schema = testDatabase.getSchema(schemaCrawlerOptions,
                                                 "PUBLIC.BOOKS");
    final Table[] tables = schema.getTables();
    boolean foundTrigger = false;
    for (final Table table: tables)
    {
      final Trigger[] triggers = table.getTriggers();
      for (final Trigger trigger: triggers)
      {
        foundTrigger = true;
        assertEquals("Triggers full name does not match",
                     "PUBLIC.BOOKS.AUTHORS.TRG_AUTHORS",
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
      .setViewsSql("SELECT * FROM INFORMATION_SCHEMA.VIEWS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setTableTypesString("VIEW");
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Schema schema = testDatabase.getSchema(schemaCrawlerOptions,
                                                 "PUBLIC.BOOKS");
    assertNotNull("Schema not found", schema);
    final View view = (View) schema.getTable("AUTHORSLIST");
    assertNotNull("View not found", view);
    assertNotNull("View definition not found", view.getDefinition());
    assertFalse("View definition not found", view.getDefinition().trim()
      .equals(""));
  }
}
