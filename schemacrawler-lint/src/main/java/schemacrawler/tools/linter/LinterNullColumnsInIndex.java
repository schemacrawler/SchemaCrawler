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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import schemacrawler.crawl.NotLoadedException;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;

public class LinterNullColumnsInIndex extends BaseLinter {

  @Override
  public String getSummary() {
    return "unique index with nullable columns";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    final List<Index> nullableColumnsInUniqueIndex =
        findNullableColumnsInUniqueIndex(table.getIndexes());
    for (final Index index : nullableColumnsInUniqueIndex) {
      addTableLint(table, getSummary(), index);
    }
  }

  private List<Index> findNullableColumnsInUniqueIndex(final Collection<Index> indexes) {
    final List<Index> nullableColumnsInUniqueIndex = new ArrayList<>();
    for (final Index index : indexes) {
      if (index.isUnique()) {
        for (final IndexColumn indexColumn : index) {
          try {
            if (indexColumn.isNullable() && !indexColumn.isGenerated()) {
              nullableColumnsInUniqueIndex.add(index);
              break;
            }
          } catch (final NotLoadedException e) {
            // The column may be partial for index pseudo-columns
            continue;
          }
        }
      }
    }
    return nullableColumnsInUniqueIndex;
  }
}
