/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import schemacrawler.schema.Table;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.text.formatter.base.TableRowCountsMessageUtility;
import schemacrawler.utility.TableRowCountsUtility;

public class TableRowCountsMessageUtilityTest {

  @Test
  public void message() {
    final Table table = new LightTable("table1");

    final NullPointerException nullPointerException =
        assertThrows(
            NullPointerException.class,
            () -> TableRowCountsMessageUtility.getRowCountMessage((Number) null));
    assertThat(nullPointerException.getMessage(), is("No number provided"));

    assertThat(TableRowCountsMessageUtility.getRowCountMessage(-1), is("empty"));
    assertThat(TableRowCountsMessageUtility.getRowCountMessage(0), is("empty"));
    assertThat(TableRowCountsMessageUtility.getRowCountMessage(1), is("1 rows"));

    assertThat(TableRowCountsMessageUtility.getRowCountMessage((Table) null), is("empty"));
    assertThat(TableRowCountsMessageUtility.getRowCountMessage(table), is("empty"));

    table.setAttribute(TableRowCountsUtility.TABLE_ROW_COUNT_KEY, 1L);

    assertThat(TableRowCountsMessageUtility.getRowCountMessage(table), is("1 rows"));
  }
}
