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
