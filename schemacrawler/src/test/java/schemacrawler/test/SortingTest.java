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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.SchemaCrawlerOptions;
import schemacrawler.SchemaInfoLevel;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.utility.test.TestUtility;

public class SortingTest
{

  private static TestUtility testUtility = new TestUtility();

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
    TestUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void columnSort()
  {

    final String[] sortedAlpha = new String[] {
        "CUSTOMERID", "ID", "TOTAL"
    };
    final String[] sortedNatural = new String[] {
        "ID", "CUSTOMERID", "TOTAL"
    };
    checkColumnSort(sortedAlpha, true);
    checkColumnSort(sortedNatural, false);

  }

  @Test
  public void fkSort()
  {

    final String[] sortedAlpha = new String[] {
        "FK_A_ITEM_PRODUCT", "FK_B_ITEM_INVOICE"
    };
    final String[] sortedNatural = new String[] {
        "FK_B_ITEM_INVOICE", "FK_A_ITEM_PRODUCT"
    };
    checkFkSort(sortedAlpha, true);
    checkFkSort(sortedNatural, false);

  }

  @Test
  public void indexSort()
  {

    final String[] sortedAlpha = new String[] {
        "INDEX_A_SUPPLIER", "INDEX_B_SUPPLIER"
    };
    final String[] sortedNatural = new String[] {
        "INDEX_B_SUPPLIER", "INDEX_A_SUPPLIER"
    };
    checkIndexSort(sortedAlpha, true);
    checkIndexSort(sortedNatural, false);

  }

  @SuppressWarnings("boxing")
  private void checkColumnSort(final String[] expectedValues,
                               final boolean sortAlphabetically)
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForTableColumns(sortAlphabetically);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);

    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 6, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals("INVOICE"))
      {
        final Column[] columns = table.getColumns();
        assertEquals("Column count does not match", 3, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
          final Column column = columns[i];
          assertEquals("Columns not "
                       + (sortAlphabetically? "alphabetically": "naturally")
                       + " sorted", expectedValues[i], column.getName());
        }
      }
    }
  }

  @SuppressWarnings("boxing")
  private void checkFkSort(final String[] expectedValues,
                           final boolean sortAlphabetically)
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForForeignKeys(sortAlphabetically);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);

    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 6, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals("ITEM"))
      {
        final ForeignKey[] foreignKeys = table.getForeignKeys();
        assertEquals("Foreign key count does not match", 2, foreignKeys.length);
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

  @SuppressWarnings("boxing")
  private void checkIndexSort(final String[] expectedValues,
                              final boolean sortAlphabetically)
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForIndexes(sortAlphabetically);
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final Schema schema = testUtility.getSchema(schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);

    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 6, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals("SUPPLIER"))
      {
        final Index[] indices = table.getIndices();
        assertEquals("Index count does not match", 2, indices.length);
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
