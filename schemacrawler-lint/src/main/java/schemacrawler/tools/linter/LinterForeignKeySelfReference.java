/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;

public class LinterForeignKeySelfReference extends BaseLinter {

  public LinterForeignKeySelfReference() {
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
    if (table != null && !(table instanceof View)) {
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
