/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.counts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.Table;
import schemacrawler.test.utility.crawl.LightTable;

public class TableRowCountsUtilityTest {

  @Test
  public void add() {
    final Table table = new LightTable("table1");

    TableRowCountsUtility.addRowCountToTable(null, 0);
    assertThat(TableRowCountsUtility.hasRowCount(null), is(false));

    TableRowCountsUtility.addRowCountToTable(table, 1);
    assertThat(TableRowCountsUtility.hasRowCount(table), is(true));
    assertThat(TableRowCountsUtility.getRowCount(table), is(1L));

    TableRowCountsUtility.addRowCountToTable(table, 0);
    assertThat(TableRowCountsUtility.hasRowCount(table), is(true));
    assertThat(TableRowCountsUtility.getRowCount(table), is(0L));

    TableRowCountsUtility.addRowCountToTable(table, -1);
    assertThat(TableRowCountsUtility.hasRowCount(table), is(false));
    assertThat(TableRowCountsUtility.getRowCount(table), is(-1L));
  }

  @Test
  public void message() {
    final Table table = new LightTable("table1");

    final NullPointerException nullPointerException =
        assertThrows(
            NullPointerException.class,
            () -> TableRowCountsUtility.getRowCountMessage((Number) null));
    assertThat(nullPointerException.getMessage(), is("No number provided"));

    assertThat(TableRowCountsUtility.getRowCountMessage(-1), is("empty"));
    assertThat(TableRowCountsUtility.getRowCountMessage(0), is("empty"));
    assertThat(TableRowCountsUtility.getRowCountMessage(1), is("1 rows"));

    assertThat(TableRowCountsUtility.getRowCountMessage((Table) null), is("empty"));
    assertThat(TableRowCountsUtility.getRowCountMessage(table), is("empty"));
    TableRowCountsUtility.addRowCountToTable(table, 1);
    assertThat(TableRowCountsUtility.getRowCountMessage(table), is("1 rows"));
  }
}
