/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.linter;

import java.io.Serial;
import java.sql.Connection;
import java.util.Collection;
import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableWithNoIndexes extends BaseLinterProvider {

  @Serial private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableWithNoIndexes() {
    super(LinterTableWithNoIndexes.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableWithNoIndexes(getPropertyName(), lintCollector);
  }
}

class LinterTableWithNoIndexes extends BaseLinter {

  LinterTableWithNoIndexes(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary() {
    return "no indexes";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    if (table != null) {
      final Collection<Index> indexes = table.getIndexes();
      if (table.getPrimaryKey() == null && indexes.isEmpty()) {
        addTableLint(table, getSummary());
      }
    }
  }
}
