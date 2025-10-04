/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.linter;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.sql.Connection;
import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableWithNoPrimaryKey extends BaseLinterProvider {

  @Serial private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableWithNoPrimaryKey() {
    super(LinterTableWithNoPrimaryKey.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableWithNoPrimaryKey(getPropertyName(), lintCollector);
  }
}

class LinterTableWithNoPrimaryKey extends BaseLinter {

  LinterTableWithNoPrimaryKey(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.high);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary() {
    return "no primary key";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    if (hasNoPrimaryKey(table)) {
      addTableLint(table, getSummary());
    }
  }

  private boolean hasNoPrimaryKey(final Table table) {
    if (table.getPrimaryKey() == null) {
      boolean hasDataColumn = false;
      for (final Column column : getColumns(table)) {
        if (!column.isPartOfForeignKey()) {
          hasDataColumn = true;
          break;
        }
      }
      return hasDataColumn;
    }

    return false;
  }
}
