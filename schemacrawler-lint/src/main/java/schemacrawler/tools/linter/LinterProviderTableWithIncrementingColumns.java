/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Comparator.naturalOrder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.convertForComparison;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.Multimap;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableWithIncrementingColumns extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableWithIncrementingColumns() {
    super(LinterTableWithIncrementingColumns.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableWithIncrementingColumns(getPropertyName(), lintCollector);
  }
}

class LinterTableWithIncrementingColumns extends BaseLinter {

  private static class IncrementingColumn {
    private final int columnIncrement;
    private final Column column;

    IncrementingColumn(final String columnIncrement, final Column column) {
      int columnInc = -1;
      try {
        columnInc = Integer.parseInt(columnIncrement);
      } catch (final NumberFormatException e) {
        columnInc = -1;
      }
      this.columnIncrement = columnInc;

      this.column = column;
    }

    public Column getColumn() {
      return column;
    }

    public int getColumnIncrement() {
      return columnIncrement;
    }
  }

  LinterTableWithIncrementingColumns(
      final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
  }

  @Override
  public String getSummary() {
    return "incrementing columns";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    final Multimap<String, IncrementingColumn> incrementingColumns =
        findIncrementingColumns(getColumns(table));
    for (final List<IncrementingColumn> incrementingColumnsList : incrementingColumns.values()) {
      addIncrementingColumnsLints(table, incrementingColumnsList);
    }
  }

  private void addIncrementingColumnsLints(
      final Table table, final List<IncrementingColumn> incrementingColumnsList) {

    int minIncrement = Integer.MAX_VALUE;
    int maxIncrement = Integer.MIN_VALUE;
    final ArrayList<Column> incrementingColumns = new ArrayList<>(incrementingColumnsList.size());
    for (int i = 0; i < incrementingColumnsList.size(); i++) {
      final IncrementingColumn incrementingColumn = incrementingColumnsList.get(i);
      incrementingColumns.add(i, incrementingColumn.getColumn());

      minIncrement = Math.min(minIncrement, incrementingColumn.getColumnIncrement());
      maxIncrement = Math.max(maxIncrement, incrementingColumn.getColumnIncrement());
    }
    incrementingColumns.sort(naturalOrder());
    addTableLint(table, getSummary(), incrementingColumns);

    // Check for increments that are not consecutive
    if (maxIncrement - minIncrement + 1 != incrementingColumnsList.size()) {
      addTableLint(table, "incrementing columns are not consecutive", incrementingColumns);
    }

    // Check for consistent column data-types
    final ColumnDataType columnDataType = incrementingColumns.get(0).getColumnDataType();
    final int columnSize = incrementingColumns.get(0).getSize();
    for (int i = 1; i < incrementingColumns.size(); i++) {
      if (!columnDataType.equals(incrementingColumns.get(i).getColumnDataType())
          || columnSize != incrementingColumns.get(i).getSize()) {
        addTableLint(
            table, "incrementing columns don't have the same data-type", incrementingColumns);
        break;
      }
    }
  }

  private Multimap<String, IncrementingColumn> findIncrementingColumns(final List<Column> columns) {
    if (columns == null || columns.size() <= 1) {
      return new Multimap<>();
    }

    final Pattern pattern = Pattern.compile("(.*[^0-9])([0-9]+)");

    final Map<String, Integer> incrementingColumnsMap = new HashMap<>();
    for (final Column column : columns) {
      final String columnName = convertForComparison(column.getName());
      incrementingColumnsMap.put(columnName, 1);
      final Matcher matcher = pattern.matcher(columnName);
      if (matcher.matches()) {
        final String columnNameBase = matcher.group(1);
        if (incrementingColumnsMap.containsKey(columnNameBase)) {
          incrementingColumnsMap.put(
              columnNameBase, incrementingColumnsMap.get(columnNameBase) + 1);
        } else {
          incrementingColumnsMap.put(columnNameBase, 1);
        }
      }
    }

    // Remove columns that have a count of 1 (removing from the map iterator removes from the map)
    for (final Iterator<Entry<String, Integer>> columnCounts =
            incrementingColumnsMap.entrySet().iterator();
        columnCounts.hasNext(); ) {
      final Entry<String, Integer> columnCount = columnCounts.next();
      if (columnCount.getValue() == 1) {
        columnCounts.remove();
      }
    }

    final Multimap<String, IncrementingColumn> incrementingColumns = new Multimap<>();

    for (final Column column : columns) {
      final String columnName = convertForComparison(column.getName());
      if (incrementingColumnsMap.containsKey(columnName)) {
        incrementingColumns.add(columnName, new IncrementingColumn("0", column));
      }
      final Matcher matcher = pattern.matcher(columnName);
      if (matcher.matches()) {
        final String columnNameBase = matcher.group(1);
        final String columnIncrement = matcher.group(2);
        if (incrementingColumnsMap.containsKey(columnNameBase)) {
          incrementingColumns.add(columnNameBase, new IncrementingColumn(columnIncrement, column));
        }
      }
    }

    return incrementingColumns;
  }
}
