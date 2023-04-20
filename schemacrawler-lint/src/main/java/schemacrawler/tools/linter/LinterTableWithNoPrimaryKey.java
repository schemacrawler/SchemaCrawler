/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;
import java.sql.Connection;
import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;

public class LinterTableWithNoPrimaryKey extends BaseLinter {

  public LinterTableWithNoPrimaryKey() {
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
