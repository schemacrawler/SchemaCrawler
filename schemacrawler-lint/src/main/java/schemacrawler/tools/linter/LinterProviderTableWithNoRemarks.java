/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.linter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableWithNoRemarks extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableWithNoRemarks() {
    super(LinterTableWithNoRemarks.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableWithNoRemarks(getPropertyName(), lintCollector);
  }
}

/**
 * Check that tables and columns) have remarks.
 *
 * <p>(Based on an idea from Michèle Barré)
 */
class LinterTableWithNoRemarks extends BaseLinter {

  LinterTableWithNoRemarks(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.low);
  }

  @Override
  public String getSummary() {
    return "should have remarks";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    if (!table.hasRemarks()) {
      addTableLint(table, getSummary());
    }

    final ArrayList<String> columnsWithNoRemarks = findColumnsWithNoRemarks(getColumns(table));
    if (!columnsWithNoRemarks.isEmpty()) {
      addTableLint(table, getSummary(), columnsWithNoRemarks);
    }
  }

  private ArrayList<String> findColumnsWithNoRemarks(final List<Column> columns) {
    final ArrayList<String> names = new ArrayList<>();
    for (final Column column : columns) {
      if (!column.hasRemarks()) {
        names.add(column.getName());
      }
    }
    return names;
  }
}
