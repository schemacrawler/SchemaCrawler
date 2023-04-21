/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.WithTestDatabase;

@WithTestDatabase
public class SchemaCrawlerDeepTest {

  @Test
  public void tableEquals(final Connection connection) throws Exception {

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);

    final Schema systemSchema = new SchemaReference("PUBLIC", "SYSTEM_LOBS");
    assertThat("Should not find any tables", catalog.getTables(systemSchema), empty());
    assertThat(
        "Expected no routines, since routine retrieval is turned off by default",
        catalog.getRoutines(systemSchema),
        empty());

    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    assertThat("Could not find any tables", catalog.getTables(schema), hasSize(11));
    assertThat(
        "Expected no routines, since routine retrieval is turned off by default",
        catalog.getRoutines(schema),
        empty());

    // Try negative test
    final Table table0 = (Table) catalog.getTables(schema).toArray()[0];
    assertThat("Could not find any columns", table0.getColumns(), not(empty()));

    final MutableTable table1 = new MutableTable(table0.getSchema(), "Test Table 1");
    final MutableTable table2 = new MutableTable(table0.getSchema(), "Test Table 2");
    final MutablePrimaryKey primaryKey = (MutablePrimaryKey) table0.getPrimaryKey();
    table1.setPrimaryKey(primaryKey);
    table2.setPrimaryKey(primaryKey);
    for (final Column column : table0.getColumns()) {
      table1.addColumn((MutableColumn) column);
      table2.addColumn((MutableColumn) column);
    }
    for (final Index index : table0.getIndexes()) {
      table1.addIndex((MutableIndex) index);
      table2.addIndex((MutableIndex) index);
    }
    for (final ForeignKey fk : table0.getForeignKeys()) {
      table1.addForeignKey((MutableForeignKey) fk);
      table2.addForeignKey((MutableForeignKey) fk);
    }
    for (final Trigger trigger : table0.getTriggers()) {
      table1.addTrigger((MutableTrigger) trigger);
      table2.addTrigger((MutableTrigger) trigger);
    }
    for (final Privilege privilege : table0.getPrivileges()) {
      table1.addPrivilege((MutablePrivilege) privilege);
      table2.addPrivilege((MutablePrivilege) privilege);
    }
    for (final TableConstraint tableConstraint : table0.getTableConstraints()) {
      table1.addTableConstraint(tableConstraint);
      table2.addTableConstraint(tableConstraint);
    }

    assertThat("Tables should not be equal", table1, not(equalTo(table2)));
  }
}
