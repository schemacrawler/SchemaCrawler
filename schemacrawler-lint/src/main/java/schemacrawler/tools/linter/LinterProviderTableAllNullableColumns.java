/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.linter;

import static java.util.Objects.requireNonNull;
import static schemacrawler.utility.MetaDataUtility.isView;

import java.io.Serial;
import java.sql.Connection;
import java.util.Collection;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableAllNullableColumns extends BaseLinterProvider {

  @Serial private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableAllNullableColumns() {
    super(LinterTableAllNullableColumns.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableAllNullableColumns(getPropertyName(), lintCollector);
  }
}

class LinterTableAllNullableColumns extends BaseLinter {

  LinterTableAllNullableColumns(
      final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
  }

  @Override
  public String getSummary() {
    return "no non-nullable data columns";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    if (!isView(table) && hasAllNullableColumns(getColumns(table))) {
      addTableLint(table, getSummary());
    }
  }

  private boolean hasAllNullableColumns(final Collection<Column> columns) {
    boolean hasAllNullableColumns = true;
    for (final Column column : columns) {
      if (!column.isPartOfPrimaryKey() && !column.isNullable()) {
        hasAllNullableColumns = false;
        break;
      }
    }
    return hasAllNullableColumns;
  }
}
