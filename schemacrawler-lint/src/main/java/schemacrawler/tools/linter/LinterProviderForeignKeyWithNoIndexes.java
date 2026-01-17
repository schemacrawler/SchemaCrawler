/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.linter;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.sql.Connection;
import schemacrawler.ermodel.utility.EntityModelUtility;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import us.fatehi.utility.OptionalBoolean;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderForeignKeyWithNoIndexes extends BaseLinterProvider {

  @Serial private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderForeignKeyWithNoIndexes() {
    super(LinterForeignKeyWithNoIndexes.class.getName());
  }

  @Override
  public BaseLinter newLinter(final LintCollector lintCollector) {
    return new LinterForeignKeyWithNoIndexes(getPropertyName(), lintCollector);
  }
}

class LinterForeignKeyWithNoIndexes extends BaseLinter {

  public LinterForeignKeyWithNoIndexes(
      final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.low);
  }

  @Override
  public String getSummary() {
    return "foreign key with no index";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    if (table instanceof PartialDatabaseObject) {
      return;
    }

    for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
      if (EntityModelUtility.coveredByIndex(foreignKey) == OptionalBoolean.false_value) {
        addTableLint(table, getSummary(), foreignKey);
      }
    }
  }
}
