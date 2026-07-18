/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.renderer;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;

final class ScribeRelationsIndex {

  private final List<ForeignKey> allForeignKeys;
  private final Map<NamedObjectKey, List<ForeignKey>> childForeignKeysByTable;
  private final Map<NamedObjectKey, List<ForeignKey>> parentForeignKeysByTable;
  private final Map<NamedObjectKey, List<Table>> referencedTablesByTable;
  private final Map<NamedObjectKey, List<Table>> referencingTablesByTable;

  ScribeRelationsIndex(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    final Map<NamedObjectKey, List<ForeignKey>> child = new HashMap<>();
    final Map<NamedObjectKey, List<ForeignKey>> parent = new HashMap<>();
    final Map<NamedObjectKey, List<Table>> referenced = new HashMap<>();
    final Map<NamedObjectKey, List<Table>> referencing = new HashMap<>();
    final Map<NamedObjectKey, ForeignKey> deduplicatedForeignKeys = new LinkedHashMap<>();

    for (final Table table : catalog.getTables()) {
      final List<ForeignKey> imported = List.copyOf(table.getImportedForeignKeys());
      final List<ForeignKey> exported = List.copyOf(table.getExportedForeignKeys());
      child.put(table.key(), imported);
      parent.put(table.key(), exported);

      final List<Table> referencedTablesForTable = new ArrayList<>();
      for (final ForeignKey foreignKey : imported) {
        referencedTablesForTable.add(foreignKey.getPrimaryKeyTable());
        deduplicatedForeignKeys.putIfAbsent(foreignKey.key(), foreignKey);
      }
      referenced.put(table.key(), List.copyOf(referencedTablesForTable));

      final List<Table> referencingTablesForTable = new ArrayList<>();
      for (final ForeignKey foreignKey : exported) {
        referencingTablesForTable.add(foreignKey.getForeignKeyTable());
        deduplicatedForeignKeys.putIfAbsent(foreignKey.key(), foreignKey);
      }
      referencing.put(table.key(), List.copyOf(referencingTablesForTable));
    }

    childForeignKeysByTable = Map.copyOf(child);
    parentForeignKeysByTable = Map.copyOf(parent);
    referencedTablesByTable = Map.copyOf(referenced);
    referencingTablesByTable = Map.copyOf(referencing);
    allForeignKeys = List.copyOf(deduplicatedForeignKeys.values());
  }

  Collection<ForeignKey> childForeignKeys(final Table table) {
    return lookup(childForeignKeysByTable, table);
  }

  int foreignKeyCount() {
    return allForeignKeys.size();
  }

  Collection<ForeignKey> parentForeignKeys(final Table table) {
    return lookup(parentForeignKeysByTable, table);
  }

  Collection<Table> referencedTables(final Table table) {
    return lookup(referencedTablesByTable, table);
  }

  Collection<Table> referencingTables(final Table table) {
    return lookup(referencingTablesByTable, table);
  }

  private <T> Collection<T> lookup(final Map<NamedObjectKey, List<T>> map, final Table table) {
    if (table == null) {
      return List.of();
    }
    return map.getOrDefault(table.key(), List.of());
  }
}
