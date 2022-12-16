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
package schemacrawler.loader.weakassociations;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.Table;
import schemacrawler.test.utility.crawl.LightTable;

public class TableMatchKeysTest {

  @Test
  public void tableMatchKeys_boundaries() {
    TableMatchKeys matchkeys;
    List<String> withoutPrefix;

    // 1.
    assertThrows(NullPointerException.class, () -> new TableMatchKeys(null));

    // 2.
    matchkeys = new TableMatchKeys(Collections.emptyList());
    assertThat(matchkeys.toString(), is("{}"));

    withoutPrefix = matchkeys.get(new LightTable("table0"));
    assertThat(withoutPrefix, containsInAnyOrder("table0"));

    // 3.
    matchkeys = new TableMatchKeys(tables("table1"));
    assertThat(matchkeys.toString(), is("{table1=[table1]}"));

    withoutPrefix = matchkeys.get(new LightTable("table0"));
    assertThat(withoutPrefix, containsInAnyOrder("table0"));

    withoutPrefix = matchkeys.get(new LightTable("table1"));
    assertThat(withoutPrefix, containsInAnyOrder("table1"));

    withoutPrefix = matchkeys.get(null);
    assertThat(withoutPrefix, is(nullValue()));
  }

  @Test
  public void tableMatchKeys_mixed_prefixes() {
    List<String> withoutPrefix;

    final TableMatchKeys matchkeys =
        new TableMatchKeys(tables("vap_old_table1", "vap_old_table2", "vap_table3"));

    withoutPrefix = matchkeys.get(new LightTable("table0"));
    assertThat(withoutPrefix, containsInAnyOrder("table0"));

    withoutPrefix = matchkeys.get(new LightTable("vap_old_table1"));
    assertThat(withoutPrefix, containsInAnyOrder("table1", "old_table1", "vap_old_table1"));

    withoutPrefix = matchkeys.get(new LightTable("vap_old_table2"));
    assertThat(withoutPrefix, containsInAnyOrder("table2", "old_table2", "vap_old_table2"));

    withoutPrefix = matchkeys.get(new LightTable("vap_table3"));
    assertThat(withoutPrefix, containsInAnyOrder("table3", "vap_table3"));
  }

  private List<Table> tables(final String... tableNames) {
    requireNonNull(tableNames);

    final List<Table> tables = new ArrayList<>();
    for (final String tableName : tableNames) {
      tables.add(new LightTable(tableName));
    }
    return tables;
  }
}
