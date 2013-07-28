/*
 * SchemaCrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.NamedObjectSort;
import sf.util.Utility;

public class SchemaCrawlerTest
  extends BaseDatabaseTest
{

  private static final String METADATA_OUTPUT = "metadata/";

  @Test
  public void checkConstraints()
    throws Exception
  {
    final TestWriter out = new TestWriter();

    final InformationSchemaViews informationSchemaViews = new InformationSchemaViews();
    informationSchemaViews
      .setTableConstraintsSql("SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS");
    informationSchemaViews
      .setCheckConstraintsSql("SELECT * FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
    assertEquals("Schema count does not match", 6, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      out.println("schema: " + schema.getFullName());
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        out.println("  table: " + table.getFullName());
        final CheckConstraint[] checkConstraints = table.getCheckConstraints()
          .toArray(new CheckConstraint[0]);
        for (int i = 0; i < checkConstraints.length; i++)
        {
          final CheckConstraint checkConstraint = checkConstraints[i];
          out.println("    constraint: " + checkConstraint.getName());
        }
      }
    }

    out.close();
    out.assertEquals(TestUtility.currentMethodFullName());
  }

  @Test
  public void columnLookup()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();

    final Database database = getDatabase(schemaCrawlerOptions);
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
    final TestWriter out = new TestWriter();

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

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
    assertEquals("Schema count does not match", 5, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      out.println("schema: " + schema.getFullName());
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      Arrays.sort(tables, NamedObjectSort.alphabetical);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        out.println("  table: " + table.getFullName());
        out.println("    # columns: " + table.getColumns().size());
        out.println("    # constraints: " + table.getCheckConstraints().size());
        out.println("    # indices: " + table.getIndices().size());
        out.println("    # foreign keys: " + table.getForeignKeys().size());
        out.println("    # imported foreign keys: "
                    + table.getExportedForeignKeys().size());
        out.println("    # exported: " + table.getImportedForeignKeys().size());
        out.println("    # privileges: " + table.getPrivileges().size());
      }
    }

    out.close();
    out.assertEquals(TestUtility.currentMethodFullName());
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

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final Routine[] routines = database.getRoutines(schema)
      .toArray(new Routine[0]);
    assertEquals("Wrong number of routines", 4, routines.length);
    for (final Routine routine: routines)
    {
      assertFalse("Routine definition not found, for " + routine,
                  Utility.isBlank(routine.getDefinition()));
    }
  }

  @Test
  public void schemaEquals()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.detailed());
    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema schema1 = new SchemaReference("PUBLIC", "BOOKS");
    assertTrue("Could not find any tables",
               database.getTables(schema1).size() > 0);
    assertEquals("Wrong number of routines", 4, database.getRoutines(schema1)
      .size());

    final Schema schema2 = new SchemaReference("PUBLIC", "BOOKS");

    assertEquals("Schema not not match", schema1, schema2);
    assertEquals("Tables do not match",
                 database.getTables(schema1),
                 database.getTables(schema2));
    assertEquals("Routines do not match",
                 database.getRoutines(schema1),
                 database.getRoutines(schema2));

    // Try negative test
    final Table table1 = database.getTables(schema1).toArray(new Table[0])[0];
    final Table table2 = database.getTables(schema1).toArray(new Table[0])[1];
    assertFalse("Tables should not be equal", table1.equals(table2));

  }

  @Test
  public void synonyms()
    throws Exception
  {
    final TestWriter out = new TestWriter();

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

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull("BOOKS Schema not found", schema);
    final Synonym[] synonyms = database.getSynonyms(schema)
      .toArray(new Synonym[0]);
    assertEquals("Synonym count does not match", 6, synonyms.length);
    for (int i = 0; i < synonyms.length; i++)
    {
      final Synonym synonym = synonyms[i];
      assertNotNull(synonym);
      out.println("synonym: " + synonym.getName());
      out.println("  class: "
                  + synonym.getReferencedObject().getClass().getSimpleName());
    }

    out.close();
    out.assertEquals(TestUtility.currentMethodFullName());
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

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                  ".*\\.FOR_LINT"));

      final Database database = getDatabase(schemaCrawlerOptions);
      final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
      assertEquals("Schema count does not match", 5, schemas.length);
      for (final Schema schema: schemas)
      {
        final Table[] tables = database.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          writer.println(String.format("o--> %s [%s]",
                                       table.getFullName(),
                                       table.getTableType()));
          final SortedMap<String, Object> tableAttributes = new TreeMap<>(table.getAttributes());
          for (final Entry<String, Object> tableAttribute: tableAttributes
            .entrySet())
          {
            writer.println(String.format("      ~ %s=%s",
                                         tableAttribute.getKey(),
                                         tableAttribute.getValue()));
          }
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column: columns)
          {
            writer.println(String.format("   o--> %s [%s]",
                                         column.getFullName(),
                                         column.getColumnDataType()));
            final SortedMap<String, Object> columnAttributes = new TreeMap<>(column
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
    }

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

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
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
    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
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
    schemaCrawlerOptions.setTableTypes("VIEW");
    schemaCrawlerOptions.setInformationSchemaViews(informationSchemaViews);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final View view = (View) database.getTable(schema, "AUTHORSLIST");
    assertNotNull("View not found", view);
    assertNotNull("View definition not found", view.getDefinition());
    assertFalse("View definition not found", view.getDefinition().trim()
      .equals(""));
  }

}
