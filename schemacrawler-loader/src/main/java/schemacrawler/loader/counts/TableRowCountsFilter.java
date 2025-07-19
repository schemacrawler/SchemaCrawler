/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.counts;

import java.util.function.Predicate;
import schemacrawler.schema.Table;

public class TableRowCountsFilter implements Predicate<Table> {

  private final boolean noEmptyTables;

  public TableRowCountsFilter(final boolean noEmptyTables) {
    this.noEmptyTables = noEmptyTables;
  }

  /**
   * Check for table limiting rules.
   *
   * @param table Table to check
   * @return Whether the table should be included
   */
  @Override
  public boolean test(final Table table) {
    final boolean hideTable;
    if (noEmptyTables) {
      final long count = TableRowCountsUtility.getRowCount(table);
      hideTable = count == 0;
    } else {
      hideTable = false;
    }

    return !hideTable;
  }
}
