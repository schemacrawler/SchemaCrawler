/*
 * SchemaCrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseDatabaseTest;

public class SchemaCrawlerReferenceTest
  extends BaseDatabaseTest
{

  @Test
  public void fkReferences()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

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

    assertEquals(19, fkReferenceCount);
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
               catalog.getTable(table.getSchema(), table.getName()));
    assertTrue("Column references do not match",
               column == table.getColumn(column.getName()));

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
               table == catalog.getTable(table.getSchema(), table.getName()));
    assertTrue("Column references do not match",
               column == table.getColumn(column.getName()));
  }

}
