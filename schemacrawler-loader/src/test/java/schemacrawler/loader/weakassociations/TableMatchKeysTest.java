/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import schemacrawler.schema.Table;

public class TableMatchKeysTest {

  @Test
  public void tableMatchKeys_boundaries() {
    TableMatchKeys matchkeys;
    List<String> withoutPrefix;

    assertThrows(NullPointerException.class, () -> new TableMatchKeys(null));

    matchkeys = new TableMatchKeys(Collections.emptyList());
    assertThat(matchkeys.toString(), is("{}"));

    withoutPrefix = matchkeys.get(new LightTable("table0"));
    assertThat(withoutPrefix, is(nullValue()));
  }

  @Test
  public void tableMatchKeys_mixed_prefixes() {
    TableMatchKeys matchkeys;
    List<String> withoutPrefix;

    matchkeys = new TableMatchKeys(tables("vap_old_table1", "vap_old_table2", "vap_table3"));
    assertThat(
        matchkeys.toString(),
        is(
            "{"
                + "vap_old_table2=[old_table2, vap_old_table2], "
                + "vap_old_table1=[old_table1, vap_old_table1], "
                + "vap_table3=[table3, vap_table3]"
                + "}"));

    withoutPrefix = matchkeys.get(new LightTable("table0"));
    assertThat(withoutPrefix, is(nullValue()));

    withoutPrefix = matchkeys.get(new LightTable("vap_old_table1"));
    assertThat(withoutPrefix, containsInAnyOrder("old_table1", "vap_old_table1"));

    withoutPrefix = matchkeys.get(new LightTable("vap_old_table2"));
    assertThat(withoutPrefix, containsInAnyOrder("old_table2", "vap_old_table2"));

    withoutPrefix = matchkeys.get(new LightTable("vap_table3"));
    assertThat(withoutPrefix, containsInAnyOrder("table3", "vap_table3"));
  }

  @Test
  public void tableMatchKeys_no_prefix() {
    TableMatchKeys matchkeys;
    List<String> withoutPrefix;

    matchkeys = new TableMatchKeys(tables("table1", "table2"));
    assertThat(matchkeys.toString(), is("{table2=[table2], table1=[table1]}"));

    withoutPrefix = matchkeys.get(new LightTable("table0"));
    assertThat(withoutPrefix, is(nullValue()));

    withoutPrefix = matchkeys.get(new LightTable("table1"));
    assertThat(withoutPrefix, containsInAnyOrder("table1"));

    withoutPrefix = matchkeys.get(new LightTable("table2"));
    assertThat(withoutPrefix, containsInAnyOrder("table2"));
  }

  @Test
  @Disabled
  public void tableMatchKeys_same_prefixes() {
    TableMatchKeys matchkeys;
    List<String> withoutPrefix;

    matchkeys = new TableMatchKeys(tables("vap_old_table1", "vap_old_table2", "vap_old_table3"));
    assertThat(matchkeys.toString(), is("{}"));

    withoutPrefix = matchkeys.get(new LightTable("table0"));
    assertThat(withoutPrefix, is(empty()));

    withoutPrefix = matchkeys.get(new LightTable("vap_old_table1"));
    assertThat(withoutPrefix, containsInAnyOrder("old_table1"));

    withoutPrefix = matchkeys.get(new LightTable("vap_old_table2"));
    assertThat(withoutPrefix, containsInAnyOrder("old_table2"));

    withoutPrefix = matchkeys.get(new LightTable("vap_old_table3"));
    assertThat(withoutPrefix, containsInAnyOrder("old_table3"));
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
