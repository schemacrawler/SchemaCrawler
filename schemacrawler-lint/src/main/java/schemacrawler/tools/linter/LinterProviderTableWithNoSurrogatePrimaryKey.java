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
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableWithNoSurrogatePrimaryKey extends BaseLinterProvider {

  @Serial private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableWithNoSurrogatePrimaryKey() {
    super(LinterTableWithNoSurrogatePrimaryKey.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableWithNoSurrogatePrimaryKey(getPropertyName(), lintCollector);
  }
}

class LinterTableWithNoSurrogatePrimaryKey extends BaseLinter {

  LinterTableWithNoSurrogatePrimaryKey(
      final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.high);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary() {
    return "primary key may not be a surrogate";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    if (hasNoSurrogatePrimaryKey(table)) {
      addTableLint(table, getSummary());
    }
  }

  private boolean hasNoSurrogatePrimaryKey(final Table table) {
    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey != null) {
      final int pkColumnCount = primaryKey.getConstrainedColumns().size();
      return pkColumnCount > 1;
    }

    return true;
  }
}
