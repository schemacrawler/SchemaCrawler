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

package schemacrawler.loader.weakassociations;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Table;
import us.fatehi.utility.PrefixMatches;

final class TableMatchKeys {

  private static PrefixMatches analyzeTables(final List<Table> tables) {
    final List<String> tableNames = new ArrayList<>();
    for (final Table table : tables) {
      tableNames.add(table.getName());
    }
    return new PrefixMatches(tableNames, "_");
  }

  private final PrefixMatches matchKeysForTable;

  TableMatchKeys(final List<Table> tables) {
    requireNonNull(tables, "No tables provided");

    matchKeysForTable = analyzeTables(tables);
  }

  public List<String> get(final Table table) {
    if (table == null) {
      return null;
    }
    return matchKeysForTable.get(table.getName());
  }

  @Override
  public String toString() {
    return matchKeysForTable.toString();
  }
}
