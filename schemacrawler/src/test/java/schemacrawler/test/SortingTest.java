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

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import dbconnector.datasource.PropertiesDataSourceException;
import dbconnector.test.TestUtility;

public class SortingTest
{

  private static final Logger LOGGER = Logger.getLogger(SortingTest.class
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
  public void indexSort()
  {

    final String[] sortedAlphaIndexes = new String[] {
        "INDEX_A_SUPPLIER", "INDEX_B_SUPPLIER"
    };
    final String[] sortedNonAlphaIndexes = new String[] {
        "INDEX_B_SUPPLIER", "INDEX_A_SUPPLIER"
    };
    checkIndexSort(sortedAlphaIndexes, true);
    checkIndexSort(sortedNonAlphaIndexes, false);

  }

  private void checkIndexSort(final String[] sortedIndexes,
                              boolean sortAlphbetically)
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForIndexes(sortAlphbetically);

    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.maximum,
                                                  schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);

    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 6, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals("SUPPLIER"))
      {
        Index[] indices = table.getIndices();
        assertEquals("Index count does not match", 2, indices.length);
        for (int i = 0; i < indices.length; i++)
        {
          Index index = indices[i];
          assertEquals("Indexes not "
                       + (sortAlphbetically? "alphabetically": "naturally")
                       + " sorted", sortedIndexes[i], index.getName());
        }
      }
    }
  }

  @Test
  public void fkSort()
  {

    final String[] sortedAlphaFks = new String[] {
        "FK_A_ITEM_PRODUCT", "FK_B_ITEM_INVOICE"
    };
    final String[] sortedNonAlphaFks = new String[] {
        "FK_B_ITEM_INVOICE", "FK_A_ITEM_PRODUCT"
    };
    checkFkSort(sortedAlphaFks, true);
    checkFkSort(sortedNonAlphaFks, false);

  }

  private void checkFkSort(final String[] sortedFks, boolean sortAlphbetically)
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setAlphabeticalSortForIndexes(sortAlphbetically);

    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.maximum,
                                                  schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);

    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 6, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals("ITEM"))
      {
        ForeignKey[] foreignKeys = table.getForeignKeys();
        assertEquals("Foreign key count does not match", 2, foreignKeys.length);
        for (int i = 0; i < foreignKeys.length; i++)
        {
          ForeignKey foreignKey = foreignKeys[i];
          assertEquals("Foreign keys not "
                       + (sortAlphbetically? "alphabetically": "naturally")
                       + " sorted", sortedFks[i], foreignKey.getName());
        }
      }
    }
  }
}
