/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.linter;

import static schemacrawler.tools.lint.LintUtility.listStartsWith;
import static schemacrawler.utility.MetaDataUtility.allIndexCoumnNames;
import static schemacrawler.utility.MetaDataUtility.foreignKeyColumnNames;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderForeignKeyWithNoIndexes extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

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

    final List<ForeignKey> foreignKeysWithoutIndexes = findForeignKeysWithoutIndexes(table);
    for (final ForeignKey foreignKey : foreignKeysWithoutIndexes) {
      addTableLint(table, getSummary(), foreignKey);
    }
  }

  private List<ForeignKey> findForeignKeysWithoutIndexes(final Table table) {
    final List<ForeignKey> foreignKeysWithoutIndexes = new ArrayList<>();
    if (!(table instanceof View)) {
      final Collection<List<String>> allIndexCoumns = allIndexCoumnNames(table);
      for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
        final List<String> foreignKeyColumns = foreignKeyColumnNames(foreignKey);
        boolean hasIndex = false;
        for (final List<String> indexColumns : allIndexCoumns) {
          if (listStartsWith(indexColumns, foreignKeyColumns)) {
            hasIndex = true;
            break;
          }
        }
        if (!hasIndex) {
          foreignKeysWithoutIndexes.add(foreignKey);
        }
      }
    }
    return foreignKeysWithoutIndexes;
  }
}
