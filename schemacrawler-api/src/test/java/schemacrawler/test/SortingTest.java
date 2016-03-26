/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.utility.NamedObjectSort;

public class SortingTest
  extends BaseDatabaseTest
{

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
                                                "STATE", };
    final String[] sortedNatural = new String[] {
                                                  "ID",
                                                  "FIRSTNAME",
                                                  "LASTNAME",
                                                  "ADDRESS1",
                                                  "ADDRESS2",
                                                  "CITY",
                                                  "STATE",
                                                  "POSTALCODE",
                                                  "COUNTRY", };
    checkColumnSort("AUTHORS", sortedAlpha, true);
    checkColumnSort("AUTHORS", sortedNatural, false);

  }

  @Test
  public void fkSort()
    throws Exception
  {

    final String[] sortedAlpha = new String[] { "FK_Y_BOOK", "FK_Z_AUTHOR", };
    final String[] sortedNatural = new String[] { "FK_Z_AUTHOR", "FK_Y_BOOK", };
    checkFkSort("BOOKAUTHORS", sortedAlpha, true);
    checkFkSort("BOOKAUTHORS", sortedNatural, false);

  }

  @Test
  public void indexSort()
    throws Exception
  {

    final String[] sortedAlpha = new String[] {
                                                "IDX_A_AUTHORS",
                                                "IDX_B_AUTHORS",
                                                "SYS_IDX_PK_AUTHORS_10097", };
    final String[] sortedNatural = new String[] {
                                                  "SYS_IDX_PK_AUTHORS_10097",
                                                  "IDX_B_AUTHORS",
                                                  "IDX_A_AUTHORS", };
    checkIndexSort("AUTHORS", sortedAlpha, true);
    checkIndexSort("AUTHORS", sortedNatural, false);

  }

  @SuppressWarnings("boxing")
  private void checkColumnSort(final String tableName,
                               final String[] expectedValues,
                               final boolean sortAlphabetically)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    assertNotNull("Schema not found", schema);

    final Table table = catalog.lookupTable(schema, tableName).orElse(null);
    assertNotNull("Table " + tableName + " not found", table);
    if (table.getName().equals(tableName))
    {
      final Column[] columns = table.getColumns().toArray(new Column[0]);
      Arrays.sort(columns,
                  NamedObjectSort.getNamedObjectSort(sortAlphabetically));
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
  private void checkFkSort(final String tableName,
                           final String[] expectedValues,
                           final boolean sortAlphabetically)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    assertNotNull("Schema not found", schema);

    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
    assertEquals("Table count does not match", 6, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals(tableName))
      {
        final ForeignKey[] foreignKeys = table.getForeignKeys()
          .toArray(new ForeignKey[0]);
        Arrays.sort(foreignKeys,
                    NamedObjectSort.getNamedObjectSort(sortAlphabetically));
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

  private void checkIndexSort(final String tableName,
                              final String[] expectedValues,
                              final boolean sortAlphabetically)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
    assertEquals("Table count does not match", 6, tables.length);
    for (final Table table: tables)
    {
      if (table.getName().equals(tableName))
      {
        final Index[] indexes = table.getIndexes().toArray(new Index[0]);
        Arrays.sort(indexes,
                    NamedObjectSort.getNamedObjectSort(sortAlphabetically));
        assertEquals("Index count does not match for table " + table,
                     expectedValues.length,
                     indexes.length);
        for (int i = 0; i < indexes.length; i++)
        {
          final Index index = indexes[i];
          assertEquals("Indexes not "
                       + (sortAlphabetically? "alphabetically": "naturally")
                       + " sorted  for table " + table,
                       expectedValues[i],
                       index.getName());
        }
      }
    }
  }

}
