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

import java.util.Collection;
import java.util.List;

import org.junit.Test;

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
          final Table pkTable = fkColumnRef.getPrimaryKeyColumn().getParent();
          assertTrue("Primary key table references do not match - "
                         + pkTable.getName(),
                     pkTable == database.getTable(pkTable.getSchema(),
                                                  pkTable.getName()));

          final Table fkTable = fkColumnRef.getForeignKeyColumn().getParent();
          assertTrue("Foreign key table references do not match - "
                         + fkTable.getName(),
                     fkTable == database.getTable(fkTable.getSchema(),
                                                  fkTable.getName()));

          fkReferenceCount++;
        }
      }
    }

    assertEquals(16, fkReferenceCount);
  }

  @Test
  public void fkReferencesForGreppedTables()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.BOOKAUTHORS\\..*", ""));

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
          final Table pkTable = fkColumnRef.getPrimaryKeyColumn().getParent();
          assertNull("Primary key table table should not be in the database - "
                         + pkTable.getName(),
                     database.getTable(pkTable.getSchema(), pkTable.getName()));

          final Table fkTable = fkColumnRef.getForeignKeyColumn().getParent();
          assertTrue("Foreign key table references do not match - "
                         + fkTable.getName(),
                     fkTable == database.getTable(fkTable.getSchema(),
                                                  fkTable.getName()));

          fkReferenceCount++;
        }
      }
    }

    assertEquals(2, fkReferenceCount);
  }

}
