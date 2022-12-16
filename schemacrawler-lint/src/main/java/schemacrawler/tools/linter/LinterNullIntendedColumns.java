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
package schemacrawler.tools.linter;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;

public class LinterNullIntendedColumns extends BaseLinter {

  public LinterNullIntendedColumns() {
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary() {
    return "column where NULL may be intended";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    final List<Column> nullDefaultValueMayBeIntendedColumns =
        findNullDefaultValueMayBeIntendedColumns(getColumns(table));
    for (final Column column : nullDefaultValueMayBeIntendedColumns) {
      addTableLint(table, getSummary(), column);
    }
  }

  private List<Column> findNullDefaultValueMayBeIntendedColumns(final List<Column> columns) {
    final List<Column> nullDefaultValueMayBeIntendedColumns = new ArrayList<>();
    for (final Column column : columns) {
      final String columnDefaultValue = column.getDefaultValue();
      if (!isBlank(columnDefaultValue) && columnDefaultValue.trim().equalsIgnoreCase("NULL")) {
        nullDefaultValueMayBeIntendedColumns.add(column);
      }
    }
    return nullDefaultValueMayBeIntendedColumns;
  }
}
