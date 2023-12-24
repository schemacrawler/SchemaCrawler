/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.loader.weakassociations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.Column;
import schemacrawler.test.utility.crawl.LightColumn;
import schemacrawler.test.utility.crawl.LightTable;

public class ColumnMatchKeysMapTest {

  @Test
  public void columnMatchKeysMap_mixed_suffixes() {

    final LightTable table1 = new LightTable("Table1");
    table1.addColumn("EntityId");
    final LightTable table2 = new LightTable("Table2");
    table2.addColumn("Entity_Id");
    final LightTable table3 = new LightTable("Table3");
    table3.addColumn("Entity_ID");
    final LightTable table4 = new LightTable("Table4");
    table4.addColumn("EntityID");
    final LightTable table5 = new LightTable("Table5");
    table5.addColumn("NonEntity");

    final ColumnMatchKeysMap columnMatchKeysMap =
        new ColumnMatchKeysMap(Arrays.asList(table1, table2, table3, table4, table5));

    assertThat(
        columnMatchKeysMap.toString(),
        is(
            "{nonentity=[Table5.NonEntity], "
                + "entity=[Table1.EntityId, Table2.Entity_Id, Table3.Entity_ID, Table4.EntityID]}"));

    assertThat(columnMatchKeysMap.containsKey("entity"), is(true));
    assertThat(
        columnMatchKeysMap.get("entity").stream()
            .map(Column::getFullName)
            .collect(Collectors.toSet()),
        containsInAnyOrder(
            "Table1.EntityId", "Table2.Entity_Id", "Table3.Entity_ID", "Table4.EntityID"));

    final LightColumn column = new LightColumn(table4, "EntityID");
    assertThat(columnMatchKeysMap.containsKey(column), is(true));
    assertThat(
        columnMatchKeysMap.get(column).stream().collect(Collectors.toSet()),
        containsInAnyOrder("entity"));
  }

  @Test
  public void id() {

    final LightTable table1 = new LightTable("Table1");
    table1.addColumn("id");
    final LightTable table2 = new LightTable("Table2");
    table2.addColumn("_id");
    final LightTable table3 = new LightTable("Table3");
    table3.addColumn("ID");
    final LightTable table4 = new LightTable("Table4");
    table4.addColumn("_ID");

    final ColumnMatchKeysMap columnMatchKeysMap =
        new ColumnMatchKeysMap(Arrays.asList(table1, table2, table3, table4));

    assertThat(columnMatchKeysMap.toString(), is("{}"));

    assertThat(columnMatchKeysMap.containsKey("entity"), is(false));
    assertThat(columnMatchKeysMap.containsKey(""), is(false));

    final LightColumn column = new LightColumn(table4, "EntityID");
    assertThat(columnMatchKeysMap.containsKey(column), is(false));
  }
}
