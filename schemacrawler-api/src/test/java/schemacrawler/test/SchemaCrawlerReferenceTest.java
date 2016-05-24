/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import schemacrawler.crawl.NotLoadedException;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;

public class SchemaCrawlerReferenceTest
  extends BaseDatabaseTest
{

  @Test
  public void fkReferences()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    int fkReferenceCount = 0;
    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Collection<Table> tables = catalog.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        for (final ForeignKeyColumnReference fkColumnRef: foreignKey)
        {
          assertReferencedColumnExists(catalog,
                                       fkColumnRef.getPrimaryKeyColumn());
          assertReferencedColumnExists(catalog,
                                       fkColumnRef.getForeignKeyColumn());

          fkReferenceCount++;
        }
      }
    }

    assertEquals(26, fkReferenceCount);
  }

  @Test
  public void fkReferencesForGreppedAndFilteredTables1()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setTableInclusionRule(new RegularExpressionInclusionRule(".*\\.BOOKAUTHORS"));
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new RegularExpressionInclusionRule(".*\\.BOOKAUTHORS\\..*"));

    int fkReferenceCount = 0;
    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Collection<Table> tables = catalog.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        for (final ForeignKeyColumnReference fkColumnRef: foreignKey)
        {
          assertReferencedColumnDoesNotExist(catalog,
                                             fkColumnRef.getPrimaryKeyColumn(),
                                             true);
          assertReferencedColumnExists(catalog,
                                       fkColumnRef.getForeignKeyColumn());

          fkReferenceCount++;
        }
      }
    }

    assertEquals(2, fkReferenceCount);
  }

  @Test
  public void fkReferencesForGreppedAndFilteredTables2()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setTableInclusionRule(new RegularExpressionInclusionRule(".*\\.AUTHORS"));
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new RegularExpressionInclusionRule(".*\\.AUTHORS\\..*"));

    int fkReferenceCount = 0;
    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Collection<Table> tables = catalog.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        for (final ForeignKeyColumnReference fkColumnRef: foreignKey)
        {
          assertReferencedColumnExists(catalog,
                                       fkColumnRef.getPrimaryKeyColumn());
          assertReferencedColumnDoesNotExist(catalog,
                                             fkColumnRef.getForeignKeyColumn(),
                                             true);

          fkReferenceCount++;
        }
      }
    }

    assertEquals(1, fkReferenceCount);
  }

  @Test
  public void fkReferencesForGreppedTables1()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new RegularExpressionInclusionRule(".*\\.BOOKAUTHORS\\..*"));

    int fkReferenceCount = 0;
    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Collection<Table> tables = catalog.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        for (final ForeignKeyColumnReference fkColumnRef: foreignKey)
        {
          assertReferencedColumnDoesNotExist(catalog,
                                             fkColumnRef.getPrimaryKeyColumn(),
                                             false);
          assertReferencedColumnExists(catalog,
                                       fkColumnRef.getForeignKeyColumn());

          fkReferenceCount++;
        }
      }
    }

    assertEquals(2, fkReferenceCount);
  }

  @Test
  public void fkReferencesForGreppedTables2()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new RegularExpressionInclusionRule(".*\\.AUTHORS\\..*"));

    int fkReferenceCount = 0;
    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Collection<Table> tables = catalog.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        for (final ForeignKeyColumnReference fkColumnRef: foreignKey)
        {
          assertReferencedColumnExists(catalog,
                                       fkColumnRef.getPrimaryKeyColumn());
          assertReferencedColumnDoesNotExist(catalog,
                                             fkColumnRef.getForeignKeyColumn(),
                                             false);

          fkReferenceCount++;
        }
      }
    }

    assertEquals(1, fkReferenceCount);
  }

  private void assertReferencedColumnDoesNotExist(final Catalog catalog,
                                                  final Column column,
                                                  final boolean assertDataNotLoaded)
  {
    final Table table = column.getParent();
    assertNull("Primary key table table should not be in the database - "
               + table.getName(),
               catalog.lookupTable(table.getSchema(), table.getName())
                 .orElse(null));
    assertTrue("Column references do not match",
               column == table.lookupColumn(column.getName()).get());

    if (assertDataNotLoaded)
    {
      try
      {
        table.getTableType();
        fail("An exception should be thrown indicating that this table was not loaded from the database");
      }
      catch (final NotLoadedException e)
      {
        // Expected exception
      }
      try
      {
        column.getColumnDataType();
        fail("An exception should be thrown indicating that this table was not loaded from the database");
      }
      catch (final NotLoadedException e)
      {
        // Expected exception
      }
    }
  }

  private void assertReferencedColumnExists(final Catalog catalog,
                                            final Column column)
  {
    assertTrue(column != null);
    final Table table = column.getParent();
    assertTrue("Table references do not match - " + table.getName(),
               table == catalog.lookupTable(table.getSchema(), table.getName())
                 .get());
    assertTrue("Column references do not match",
               column == table.lookupColumn(column.getName()).get());
  }

}
