/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.linter;

import java.sql.Connection;
import static java.util.Objects.requireNonNull;
import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableWithPrimaryKeyNotFirst extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableWithPrimaryKeyNotFirst() {
    super(LinterTableWithPrimaryKeyNotFirst.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableWithPrimaryKeyNotFirst(getPropertyName(), lintCollector);
  }
}

class LinterTableWithPrimaryKeyNotFirst extends BaseLinter {

  LinterTableWithPrimaryKeyNotFirst(
      final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.low);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary() {
    return "primary key not first";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey == null) {
      return;
    }

    for (final TableConstraintColumn pkColumn : primaryKey.getConstrainedColumns()) {
      if (pkColumn.getTableConstraintOrdinalPosition() != pkColumn.getOrdinalPosition()) {
        addTableLint(table, getSummary());
        break;
      }
    }
  }
}
