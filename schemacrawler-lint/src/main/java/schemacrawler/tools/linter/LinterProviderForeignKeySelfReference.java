/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.linter;

import static java.util.Objects.requireNonNull;
import static schemacrawler.utility.MetaDataUtility.isView;

import java.io.Serial;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderForeignKeySelfReference extends BaseLinterProvider {

  @Serial private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderForeignKeySelfReference() {
    super(LinterForeignKeySelfReference.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterForeignKeySelfReference(getPropertyName(), lintCollector);
  }
}

class LinterForeignKeySelfReference extends BaseLinter {

  LinterForeignKeySelfReference(
      final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.critical);
  }

  @Override
  public String getSummary() {
    return "foreign key self-references primary key";
  }

  @Override
  protected void lint(final Table table, final Connection connections) {
    requireNonNull(table, "No table provided");

    final List<ForeignKey> selfReferencingForeignKeys = findSelfReferencingForeignKeys(table);
    for (final ForeignKey foreignKey : selfReferencingForeignKeys) {
      addTableLint(table, getSummary(), foreignKey);
    }
  }

  private List<ForeignKey> findSelfReferencingForeignKeys(final Table table) {
    final List<ForeignKey> selfReferencingForeignKeys = new ArrayList<>();
    if (table != null && !isView(table)) {
      for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
        for (final ColumnReference columnReference : foreignKey) {
          final Column pkColumn = columnReference.getPrimaryKeyColumn();
          final Column fkColumn = columnReference.getForeignKeyColumn();
          if (pkColumn.equals(fkColumn)) {
            selfReferencingForeignKeys.add(foreignKey);
            break;
          }
        }
      }
    }
    return selfReferencingForeignKeys;
  }
}
