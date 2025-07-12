/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.linter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableWithQuotedNames extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableWithQuotedNames() {
    super(LinterTableWithQuotedNames.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableWithQuotedNames(getPropertyName(), lintCollector);
  }
}

class LinterTableWithQuotedNames extends BaseLinter {

  LinterTableWithQuotedNames(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
  }

  @Override
  public String getSummary() {
    return "spaces in name, or reserved word";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    final Identifiers identifiers =
        IdentifiersBuilder.builder().fromConnection(connection).toOptions();

    final String tableName = table.getName();
    if (identifiers.isToBeQuoted(tableName)) {
      addTableLint(table, getSummary());
    }

    final List<String> spacesInNamesList =
        findColumnsWithQuotedNames(getColumns(table), identifiers);
    for (final String spacesInName : spacesInNamesList) {
      addTableLint(table, getSummary(), spacesInName);
    }
  }

  private List<String> findColumnsWithQuotedNames(
      final List<Column> columns, final Identifiers identifiers) {
    final List<String> columnsWithQuotedNames = new ArrayList<>();
    for (final Column column : columns) {
      final String columnName = column.getName();
      if (identifiers.isToBeQuoted(columnName)) {
        columnsWithQuotedNames.add(columnName);
      }
    }
    return columnsWithQuotedNames;
  }
}
