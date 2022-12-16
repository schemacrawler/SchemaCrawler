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
import static schemacrawler.tools.lint.LintUtility.listStartsWith;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.utility.MetaDataUtility;

public class LinterRedundantIndexes extends BaseLinter {

  public LinterRedundantIndexes() {
    setSeverity(LintSeverity.high);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary() {
    return "redundant index";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    final Set<Index> redundantIndexes = findRedundantIndexes(table.getIndexes());
    for (final Index index : redundantIndexes) {
      addTableLint(table, getSummary(), index);
    }
  }

  private Set<Index> findRedundantIndexes(final Collection<Index> indexes) {
    final Set<Index> redundantIndexes = new HashSet<>();

    if (indexes == null || indexes.isEmpty()) {
      return redundantIndexes;
    }

    final Map<Index, List<String>> indexColumns = new HashMap<>(indexes.size());
    for (final Index index : indexes) {
      indexColumns.put(index, MetaDataUtility.columnNames(index));
    }

    for (final Entry<Index, List<String>> indexColumnEntry1 : indexColumns.entrySet()) {
      for (final Entry<Index, List<String>> indexColumnEntry2 : indexColumns.entrySet()) {
        if (!indexColumnEntry1.equals(indexColumnEntry2)) {
          if (listStartsWith(indexColumnEntry1.getValue(), indexColumnEntry2.getValue())) {
            redundantIndexes.add(indexColumnEntry2.getKey());
          }
        }
      }
    }
    return redundantIndexes;
  }
}
