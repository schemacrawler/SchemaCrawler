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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.TestDatabase;

public class SortingTest
{

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
    throws Exception
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
  public void columnSort()
    throws Exception
  {

    final String[] sortedAlpha = new String[] {
        "ADDRESS1",
        "ADDRESS2",
        "CITY",
        "COUNTRY",
        "FIRSTNAME",
        "ID",
        "LASTNAME",
        "POSTALCODE",
        "STATE",
    };
    final String[] sortedNatural = new String[] {
        "ID",
        "FIRSTNAME",
        "LASTNAME",
        "ADDRESS1",
        "ADDRESS2",
        "CITY",
        "STATE",
        "POSTALCODE",
        "COUNTRY",
    };
    checkColumnSort("AUTHORS", sortedAlpha, true);
    checkColumnSort("AUTHORS", sortedNatural, false);

  }

  @Test
  public void fkSort()
    throws Exception
  {

    final String[] sortedAlpha = new String[] {
        "FK_Y_BOOK", "FK_Z_AUTHOR",
    };
    final String[] sortedNatural = new String[] {
        "FK_Z_AUTHOR", "FK_Y_BOOK",
    };
    checkFkSort("BOOKAUTHORS", sortedAlpha, true);
    checkFkSort("BOOKAUTHORS", sortedNatural, false);

  }

  @Test
  public void indexSort()
    throws Exception
  {

    final String[] sortedAlpha = new String[] {
        "IDX_A_AUTHORS", "IDX_B_AUTHORS"
    };
    final String[] sortedNatural = new String[] {
        "IDX_B_AUTHORS", "IDX_A_AUTHORS"
    };
    checkIndexSort("AUTHORS", sortedAlpha, true);
    checkIndexSort("AUTHORS", sortedNatural, false);

  }

  @SuppressWarnings("boxing")
  private void checkColumnSort(String tableName,
                               final String[] expectedValues,
                               final boolean sortAlphabetically)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForTableColumns(sortAlphabetically);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    assertNotNull("Schema not found", schema);

    final Table table = schema.getTable(tableName);
    assertNotNull("Table " + tableName + " not found", table);
    if (table.getName().equals(tableName))
    {
      final Column[] columns = table.getColumns();
      assertEquals("Column count does not match",
                   expectedValues.length,
                   columns.length);
      for (int i = 0; i < columns.length; i++)
      {
        final Column column = columns[i];
        assertEquals("Columns not "
                     + (sortAlphabetically? "alphabetically": "naturally")
                     + " sorted", expectedValues[i], column.getName());
      }
    }
  }

  @SuppressWarnings("boxing")
  private void checkFkSort(String tableName,
                           final String[] expectedValues,
                           final boolean sortAlphabetically)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForForeignKeys(sortAlphabetically);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    assertNotNull("Schema not found", schema);

    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 5, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals(tableName))
      {
        final ForeignKey[] foreignKeys = table.getForeignKeys();
        assertEquals("Foreign key count does not match",
                     expectedValues.length,
                     foreignKeys.length);
        for (int i = 0; i < foreignKeys.length; i++)
        {
          final ForeignKey foreignKey = foreignKeys[i];
          assertEquals("Foreign keys not "
                       + (sortAlphabetically? "alphabetically": "naturally")
                       + " sorted", expectedValues[i], foreignKey.getName());
        }
      }
    }
  }

  private void checkIndexSort(String tableName,
                              final String[] expectedValues,
                              final boolean sortAlphabetically)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForIndexes(sortAlphabetically);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    assertNotNull("Schema not found", schema);

    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 5, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals(tableName))
      {
        final Index[] indices = table.getIndices();
        assertEquals("Index count does not match",
                     expectedValues.length,
                     indices.length);
        for (int i = 0; i < indices.length; i++)
        {
          final Index index = indices[i];
          assertEquals("Indexes not "
                       + (sortAlphabetically? "alphabetically": "naturally")
                       + " sorted", expectedValues[i], index.getName());
        }
      }
    }
  }

}
