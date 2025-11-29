/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
