/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.filter;

import static java.util.Objects.requireNonNull;
import java.util.function.Predicate;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableTypes;
import schemacrawler.schemacrawler.LimitOptions;

public class TableTypesFilter implements Predicate<Table> {

  private final TableTypes tableTypes;

  public TableTypesFilter() {
    tableTypes = TableTypes.includeAll();
  }

  public TableTypesFilter(final LimitOptions options) {
    requireNonNull(options, "No limit options provided");
    tableTypes = options.getTableTypes();
  }

  public TableTypesFilter(final String... tableTypesFiltered) {
    tableTypes = TableTypes.from(tableTypesFiltered);
  }

  /**
   * Check for table limiting rules.
   *
   * @param table Table to check
   * @return Whether the table should be included
   */
  @Override
  public boolean test(final Table table) {
    return tableTypes.lookupTableType(table.getTableType().getTableType()).isPresent();
  }
}
