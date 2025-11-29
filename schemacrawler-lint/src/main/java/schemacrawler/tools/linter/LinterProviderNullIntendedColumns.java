/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.linter;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Serial;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderNullIntendedColumns extends BaseLinterProvider {

  @Serial private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderNullIntendedColumns() {
    super(LinterNullIntendedColumns.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterNullIntendedColumns(getPropertyName(), lintCollector);
  }
}

class LinterNullIntendedColumns extends BaseLinter {

  LinterNullIntendedColumns(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
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
      if (!isBlank(columnDefaultValue) && "NULL".equalsIgnoreCase(columnDefaultValue.strip())) {
        nullDefaultValueMayBeIntendedColumns.add(column);
      }
    }
    return nullDefaultValueMayBeIntendedColumns;
  }
}
