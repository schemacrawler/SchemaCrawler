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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Synonym;
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
import schemacrawler.utility.NamedObjectSort;
import sf.util.Utility;

public class SchemaCrawlerTest
{

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
                "SYS_CT_10027",
                "SYS_CT_10028",
                "SYS_CT_10029"
            },
            {},
            {
                "SYS_CT_10035", "SYS_CT_10036"
            },
            {
                "SYS_CT_10031", "SYS_CT_10032", "SYS_CT_10033"
            },
            {},
            {
              "SYS_CT_10025"
            },
            {}
        },
        {
            {
                "SYS_CT_10070", "SYS_CT_10071", "SYS_CT_10072"
            },
            {},
            {},
            {
                "SYS_CT_10062", "SYS_CT_10063", "SYS_CT_10064",
            },
            {
                "CHECK_UPPERCASE_STATE",
                "SYS_CT_10056",
                "SYS_CT_10057",
                "SYS_CT_10058",
                "SYS_CT_10059",
                "SYS_CT_10060",
            },
        },
        {},
        {},
        {
            {
                "SYS_CT_10046", "SYS_CT_10047", "SYS_CT_10048", "SYS_CT_10049"
            }, {
                "SYS_CT_10051", "SYS_CT_10052"
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
    final Schema[] schemas = (Schema[]) database.getSchemas()
      .toArray(new Schema[0]);
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      assertEquals("Table count does not match",
                   tableCounts[schemaIdx],
                   tables.length);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        final CheckConstraint[] checkConstraints = table.getCheckConstraints()
          .toArray(new CheckConstraint[0]);
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
    final Table table = database.getTable(schema, "AUTHORS");
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
    final Schema[] schemas = (Schema[]) database.getSchemas()
      .toArray(new Schema[0]);
    assertEquals("Schema count does not match", 5, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      assertEquals("Table count does not match",
                   tableCounts[schemaIdx],
                   tables.length);
      Arrays.sort(tables, NamedObjectSort.alphabetical);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        assertEquals(String.format("Table [%d][%d] %s columns count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     tableColumnCounts[schemaIdx][tableIdx],
                     table.getColumns().size());
        assertEquals(String.format("Table [%d][%d] %s check constraints count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     checkConstraints[schemaIdx][tableIdx],
                     table.getCheckConstraints().size());
        assertEquals(String.format("Table [%d][%d] %s index count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     indexCounts[schemaIdx][tableIdx],
                     table.getIndices().size());
        assertEquals(String.format("Table [%d][%d] %s foreign key count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     fkCounts[schemaIdx][tableIdx],
                     table.getForeignKeys().size());
        assertEquals(String.format("Table [%d][%d] %s exported foreign key count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     exportedFkCounts[schemaIdx][tableIdx],
                     table.getExportedForeignKeys().size());
        assertEquals(String.format("Table [%d][%d] %s imported foreign key count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     importedFkCounts[schemaIdx][tableIdx],
                     table.getImportedForeignKeys().size());
        assertEquals(String.format("Table [%d][%d] %s privileges count does not match",
                                   schemaIdx,
                                   tableIdx,
                                   table.getFullName()),
                     tablePrivilegesCounts[schemaIdx][tableIdx],
                     table.getPrivileges().size());
      }
    }
  }

  @Test
  public void routineDefinitions()
    throws Exception
  {

    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setRoutinesSql("SELECT * FROM INFORMATION_SCHEMA.ROUTINES");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema schema = testDatabase.getSchema(schemaCrawlerOptions,
                                                 "PUBLIC.BOOKS");
    final Routine[] routines = database.getRoutines(schema)
      .toArray(new Routine[0]);
    assertEquals("Wrong number of routines", 2, routines.length);
    for (final Routine routine: routines)
    {
      assertFalse("Routine definition not found, for " + routine,
                  Utility.isBlank(((Procedure) routine).getDefinition()));
    }
  }

  @Test
  public void schemaEquals()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.detailed());
    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema schema1 = testDatabase.getSchema(schemaCrawlerOptions,
                                                  "PUBLIC.BOOKS");
    assertTrue("Could not find any tables",
               database.getTables(schema1).size() > 0);
    assertEquals("Wrong number of routines", 2, database.getRoutines(schema1)
      .size());

    final Schema schema2 = testDatabase.getSchema(schemaCrawlerOptions,
                                                  "PUBLIC.BOOKS");

    assertEquals("Schema not not match", schema1, schema2);
    assertEquals("Tables do not match",
                 database.getTables(schema1),
                 database.getTables(schema2));
    assertEquals("Routines do not match",
                 database.getRoutines(schema1),
                 database.getRoutines(schema2));

    // Try negative test
    final Table table1 = (Table) database.getTables(schema1)
      .toArray(new Table[0])[0];
    final Table table2 = (Table) database.getTables(schema1)
      .toArray(new Table[0])[1];
    assertFalse("Tables should not be equal", table1.equals(table2));

  }

  @Test
  public void synonyms()
    throws Exception
  {
    final String[] classes = {
        "MutableTable", "MutableTable", "MutableTable", "", "", "",
    };
    final String[] synonymNames = {
        "AUTHORS",
        "BOOKAUTHORS",
        "BOOKS",
        "\"Global Counts\"",
        "No_Columns",
        "PUBLICATIONS",
    };

    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setSynonymSql("SELECT LIMIT 1 3                                  \n"
                     + "  TABLE_CATALOG AS SYNONYM_CATALOG,             \n"
                     + "  TABLE_SCHEMA AS SYNONYM_SCHEMA,               \n"
                     + "  TABLE_NAME AS SYNONYM_NAME,                   \n"
                     + "  TABLE_CATALOG AS REFERENCED_OBJECT_CATALOG,   \n"
                     + "  TABLE_SCHEMA AS REFERENCED_OBJECT_SCHEMA,     \n"
                     + "  TABLE_NAME AS REFERENCED_OBJECT_NAME          \n"
                     + "FROM                                            \n"
                     + "  INFORMATION_SCHEMA.TABLES                     \n"
                     + "WHERE                                           \n"
                     + "  TABLE_SCHEMA = 'BOOKS'                        \n"
                     + "UNION                                           \n"
                     + "SELECT LIMIT 1 3                                \n"
                     + "  ROUTINE_CATALOG AS SYNONYM_CATALOG,           \n"
                     + "  ROUTINE_SCHEMA AS SYNONYM_SCHEMA,             \n"
                     + "  ROUTINE_NAME AS SYNONYM_NAME,                 \n"
                     + "  ROUTINE_CATALOG AS REFERENCED_OBJECT_CATALOG, \n"
                     + "  ROUTINE_SCHEMA AS REFERENCED_OBJECT_SCHEMA,   \n"
                     + "  ROUTINE_NAME AS REFERENCED_OBJECT_NAME        \n"
                     + "FROM                                            \n"
                     + "  INFORMATION_SCHEMA.ROUTINES                   \n"
                     + "WHERE                                           \n"
                     + "  ROUTINE_SCHEMA = 'BOOKS'                      \n"
                     + "UNION                                           \n"
                     + "SELECT LIMIT 1 3                                \n"
                     + "  'PUBLIC' AS SYNONYM_CATALOG,                  \n"
                     + "  'BOOKS' AS SYNONYM_SCHEMA,                    \n"
                     + "  TABLE_NAME AS SYNONYM_NAME,                   \n"
                     + "  TABLE_CATALOG AS REFERENCED_OBJECT_CATALOG,   \n"
                     + "  TABLE_SCHEMA AS REFERENCED_OBJECT_SCHEMA,     \n"
                     + "  TABLE_NAME + '1' AS REFERENCED_OBJECT_NAME    \n"
                     + "FROM                                            \n"
                     + "  INFORMATION_SCHEMA.TABLES                     \n"
                     + "WHERE                                           \n"
                     + "  TABLE_SCHEMA != 'BOOKS'                       ");

    final SchemaInfoLevel minimum = SchemaInfoLevel.minimum();
    minimum.setRetrieveSynonymInformation(true);

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(minimum);
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSynonymInclusionRule(InclusionRule.INCLUDE_ALL);

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull("BOOKS Schema not found", schema);
    final Synonym[] synonyms = database.getSynonyms(schema)
      .toArray(new Synonym[0]);
    assertEquals("Synonym count does not match", 6, synonyms.length);
    for (int i = 0; i < synonyms.length; i++)
    {
      final Synonym synonym = synonyms[i];
      assertNotNull(synonym);
      assertEquals("Wrong referenced object class - "
                       + synonym.getReferencedObject().getClass(),
                   classes[i],
                   synonym.getReferencedObject().getClass().getSimpleName());
      assertEquals("", synonymNames[i], synonym.getName());
    }
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
    final Schema[] schemas = (Schema[]) database.getSchemas()
      .toArray(new Schema[0]);
    assertEquals("Schema count does not match", 5, schemas.length);
    for (final Schema schema: schemas)
    {
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      Arrays.sort(tables, NamedObjectSort.alphabetical);
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
        final Column[] columns = table.getColumns().toArray(new Column[0]);
        for (final Column column: columns)
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
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                ".*\\.FOR_LINT"));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = (Schema[]) database.getSchemas()
      .toArray(new Schema[0]);
    assertEquals("Schema count does not match", 5, schemas.length);
    final Schema schema = schemas[0];

    for (int i = 0; i < tableNames.length; i++)
    {
      final String tableName1 = tableNames[i];
      for (int j = 0; j < tableNames.length; j++)
      {
        final String tableName2 = tableNames[j];
        assertEquals(tableName1 + " <--> " + tableName2,
                     Math.signum(database.getTable(schema, tableName1)
                       .compareTo(database.getTable(schema, tableName2))),
                     Math.signum(i - j),
                     1e-100);
      }
    }

    final Table[] tables = database.getTables(schema).toArray(new Table[0]);
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
    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema schema = testDatabase.getSchema(schemaCrawlerOptions,
                                                 "PUBLIC.BOOKS");
    final Table[] tables = database.getTables(schema).toArray(new Table[0]);
    boolean foundTrigger = false;
    for (final Table table: tables)
    {
      for (final Trigger trigger: table.getTriggers())
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

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema schema = testDatabase.getSchema(schemaCrawlerOptions,
                                                 "PUBLIC.BOOKS");
    assertNotNull("Schema not found", schema);
    final View view = (View) database.getTable(schema, "AUTHORSLIST");
    assertNotNull("View not found", view);
    assertNotNull("View definition not found", view.getDefinition());
    assertFalse("View definition not found", view.getDefinition().trim()
      .equals(""));
  }
}
