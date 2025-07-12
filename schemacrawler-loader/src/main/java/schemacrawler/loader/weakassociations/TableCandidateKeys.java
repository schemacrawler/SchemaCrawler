/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.loader.weakassociations;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import schemacrawler.schema.Column;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintColumn;

final class TableCandidateKeys implements Iterable<Column> {

  private final Table table;
  private final Set<Column> tableKeys;

  TableCandidateKeys(final Table table) {
    this.table = requireNonNull(table, "No table provided");
    tableKeys = new HashSet<>();
    listTableKeys(table);
  }

  @Override
  public Iterator<Column> iterator() {
    return tableKeys.iterator();
  }

  @Override
  public String toString() {
    return String.format("%s: %s", table, tableKeys);
  }

  private void addColumnFromIndex(final Table table, final Index index) {
    final IndexColumn indexColumn = index.getColumns().get(0);
    table.lookupColumn(indexColumn.getName()).ifPresent(column -> tableKeys.add(column));
  }

  private void addColumnFromPrimaryKey(final Table table, final PrimaryKey primaryKey) {
    final TableConstraintColumn tableConstraintColumn = primaryKey.getConstrainedColumns().get(0);
    table.lookupColumn(tableConstraintColumn.getName()).ifPresent(column -> tableKeys.add(column));
  }

  private void listTableKeys(final Table table) {
    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey != null && primaryKey.getConstrainedColumns().size() == 1) {
      addColumnFromPrimaryKey(table, primaryKey);
    }

    for (final Index index : table.getIndexes()) {
      if (index != null && index.isUnique() && index.getColumns().size() == 1) {
        addColumnFromIndex(table, index);
      }
    }
  }
}
