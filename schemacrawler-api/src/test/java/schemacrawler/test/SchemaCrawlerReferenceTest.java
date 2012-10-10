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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import schemacrawler.crawl.NotLoadedException;
import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
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
    final Database database = getDatabase(schemaCrawlerOptions);
    final Collection<Table> tables = database.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        final List<ForeignKeyColumnReference> fkColumnRefs = foreignKey
          .getColumnReferences();
        for (final ForeignKeyColumnReference fkColumnRef: fkColumnRefs)
        {
          assertReferencedColumnExists(database,
                                       fkColumnRef.getPrimaryKeyColumn());
          assertReferencedColumnExists(database,
                                       fkColumnRef.getForeignKeyColumn());

          fkReferenceCount++;
        }
      }
    }

    assertEquals(16, fkReferenceCount);
  }

  @Test
  public void fkReferencesForGreppedAndFilteredTables1()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setTableInclusionRule(new InclusionRule(".*\\.BOOKAUTHORS"));
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.BOOKAUTHORS\\..*"));

    int fkReferenceCount = 0;
    final Database database = getDatabase(schemaCrawlerOptions);
    final Collection<Table> tables = database.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        final List<ForeignKeyColumnReference> fkColumnRefs = foreignKey
          .getColumnReferences();
        for (final ForeignKeyColumnReference fkColumnRef: fkColumnRefs)
        {
          assertReferencedColumnDoesNotExist(database,
                                             fkColumnRef.getPrimaryKeyColumn(),
                                             true);
          assertReferencedColumnExists(database,
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
      .setTableInclusionRule(new InclusionRule(".*\\.AUTHORS"));
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.AUTHORS\\..*"));

    int fkReferenceCount = 0;
    final Database database = getDatabase(schemaCrawlerOptions);
    final Collection<Table> tables = database.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        final List<ForeignKeyColumnReference> fkColumnRefs = foreignKey
          .getColumnReferences();
        for (final ForeignKeyColumnReference fkColumnRef: fkColumnRefs)
        {
          assertReferencedColumnExists(database,
                                       fkColumnRef.getPrimaryKeyColumn());
          assertReferencedColumnDoesNotExist(database,
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
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.BOOKAUTHORS\\..*"));

    int fkReferenceCount = 0;
    final Database database = getDatabase(schemaCrawlerOptions);
    final Collection<Table> tables = database.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        final List<ForeignKeyColumnReference> fkColumnRefs = foreignKey
          .getColumnReferences();
        for (final ForeignKeyColumnReference fkColumnRef: fkColumnRefs)
        {
          assertReferencedColumnDoesNotExist(database,
                                             fkColumnRef.getPrimaryKeyColumn(),
                                             false);
          assertReferencedColumnExists(database,
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
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.AUTHORS\\..*"));

    int fkReferenceCount = 0;
    final Database database = getDatabase(schemaCrawlerOptions);
    final Collection<Table> tables = database.getTables();
    for (final Table table: tables)
    {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      for (final ForeignKey foreignKey: foreignKeys)
      {
        final List<ForeignKeyColumnReference> fkColumnRefs = foreignKey
          .getColumnReferences();
        for (final ForeignKeyColumnReference fkColumnRef: fkColumnRefs)
        {
          assertReferencedColumnExists(database,
                                       fkColumnRef.getPrimaryKeyColumn());
          assertReferencedColumnDoesNotExist(database,
                                             fkColumnRef.getForeignKeyColumn(),
                                             false);

          fkReferenceCount++;
        }
      }
    }

    assertEquals(1, fkReferenceCount);
  }

  private void assertReferencedColumnDoesNotExist(final Database database,
                                                  final Column column,
                                                  final boolean assertDataNotLoaded)
  {
    final Table table = column.getParent();
    assertNull("Primary key table table should not be in the database - "
                   + table.getName(),
               database.getTable(table.getSchema(), table.getName()));
    assertTrue("Column references do not match",
               column == table.getColumn(column.getName()));

    if (assertDataNotLoaded)
    {
      try
      {
        table.getType();
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

  private void assertReferencedColumnExists(final Database database,
                                            final Column column)
  {
    assertTrue(column != null);
    final Table table = column.getParent();
    assertTrue("Table references do not match - " + table.getName(),
               table == database.getTable(table.getSchema(), table.getName()));
    assertTrue("Column references do not match",
               column == table.getColumn(column.getName()));
  }

}
