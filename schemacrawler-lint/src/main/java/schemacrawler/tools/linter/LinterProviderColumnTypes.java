/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.linter;

import java.sql.Connection;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.Multimap;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderColumnTypes extends BaseLinterProvider {

  private static final long serialVersionUID = 7775205295917734672L;

  public LinterProviderColumnTypes() {
    super(LinterColumnTypes.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterColumnTypes(getPropertyName(), lintCollector);
  }
}

class LinterColumnTypes extends BaseLinter {

  LinterColumnTypes(final PropertyName linterName, final LintCollector lintCollector) {
    super(linterName, lintCollector);
  }

  private Multimap<String, ColumnDataType> columnTypes;

  @Override
  public String getSummary() {
    return "column with same name but different data types";
  }

  @Override
  protected void end(final Connection connection) {
    requireNonNull(columnTypes, "Not initialized");

    for (final Entry<String, List<ColumnDataType>> entry : columnTypes.entrySet()) {
      final SortedSet<ColumnDataType> currentColumnTypes = new TreeSet<>(entry.getValue());
      if (currentColumnTypes.size() > 1) {
        addCatalogLint(getSummary(), entry.getKey() + " " + currentColumnTypes);
      }
    }

    columnTypes = null;

    super.end(connection);
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");
    requireNonNull(columnTypes, "Not initialized");

    for (final Column column : getColumns(table)) {
      if (!column.isColumnDataTypeKnown()) {
        continue;
      }
      columnTypes.add(column.getName(), column.getColumnDataType());
    }
  }

  @Override
  protected void start(final Connection connection) {
    super.start(connection);

    columnTypes = new Multimap<>();
  }
}
