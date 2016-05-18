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
package schemacrawler.crawl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;

public class SchemaCrawlerDeepTest
  extends BaseDatabaseTest
{

  @Test
  public void tableEquals()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();

    final Catalog catalog = getCatalog(schemaCrawlerOptions);

    final Schema systemSchema = new SchemaReference("PUBLIC", "SYSTEM_LOBS");
    assertTrue("Should not find any tables",
               catalog.getTables(systemSchema).size() == 0);
    assertEquals("Could not find all routines",
                 10,
                 catalog.getRoutines(systemSchema).size());

    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    assertEquals("Could not find any tables",
                 6,
                 catalog.getTables(schema).size());
    assertEquals("Wrong number of routines",
                 4,
                 catalog.getRoutines(schema).size());

    // Try negative test
    final Table table0 = (Table) catalog.getTables(schema).toArray()[0];
    assertTrue("Could not find any columns", table0.getColumns().size() > 0);

    final MutableTable table1 = new MutableTable(table0.getSchema(),
                                                 "Test Table 1");
    final MutableTable table2 = new MutableTable(table0.getSchema(),
                                                 "Test Table 2");
    final MutablePrimaryKey primaryKey = (MutablePrimaryKey) table0
      .getPrimaryKey();
    table1.setPrimaryKey(primaryKey);
    table2.setPrimaryKey(primaryKey);
    for (final Column column: table0.getColumns())
    {
      table1.addColumn((MutableColumn) column);
      table2.addColumn((MutableColumn) column);
    }
    for (final Index index: table0.getIndexes())
    {
      table1.addIndex((MutableIndex) index);
      table2.addIndex((MutableIndex) index);
    }
    for (final ForeignKey fk: table0.getForeignKeys())
    {
      table1.addForeignKey((MutableForeignKey) fk);
      table2.addForeignKey((MutableForeignKey) fk);
    }
    for (final Trigger trigger: table0.getTriggers())
    {
      table1.addTrigger((MutableTrigger) trigger);
      table2.addTrigger((MutableTrigger) trigger);
    }
    for (final Privilege privilege: table0.getPrivileges())
    {
      table1.addPrivilege((MutablePrivilege) privilege);
      table2.addPrivilege((MutablePrivilege) privilege);
    }
    for (final TableConstraint tableConstraint: table0.getTableConstraints())
    {
      table1.addTableConstraint((MutableDependantTableConstraint) tableConstraint);
      table2.addTableConstraint((MutableDependantTableConstraint) tableConstraint);
    }

    assertFalse("Tables should not be equal", table1.equals(table2));

  }
}
