/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import schemacrawler.schema.NamedObjectKey;

/**
 * Drops a generic word: one used in the column names of a large share of tables, such as {@code
 * created}, that is never part of a table or routine name. A word in a table or routine name is
 * never generic, which protects domain words in small schemas.
 */
final class GenericWordFilter implements UnaryOperator<Set<String>> {

  private final Names names;
  private final VocabularyThresholds thresholds;

  GenericWordFilter(final Names names, final VocabularyThresholds thresholds) {
    this.names = requireNonNull(names, "No names provided");
    this.thresholds = requireNonNull(thresholds, "No thresholds provided");
  }

  @Override
  public Set<String> apply(final Set<String> stems) {
    requireNonNull(stems, "No stems provided");
    final Set<String> kept = new HashSet<>(stems);
    kept.removeAll(genericStems());
    return Set.copyOf(kept);
  }

  /** Stems used in the columns of many tables, that are not used to name a table or routine. */
  private Set<String> genericStems() {
    final int tableCount = names.tableCount();
    if (tableCount == 0) {
      return Set.of();
    }
    final double minTables = thresholds.genericTableShare() * tableCount;
    final Set<String> namingStems = namingStems();

    final Set<String> genericStems = new HashSet<>();
    for (final Map.Entry<String, Set<NamedObjectKey>> entry : tablesByColumnStem().entrySet()) {
      final String stem = entry.getKey();
      final boolean isInManyTables = entry.getValue().size() >= minTables;
      if (isInManyTables && !namingStems.contains(stem)) {
        genericStems.add(stem);
      }
    }
    return genericStems;
  }

  /** Stems of the names of tables and routines. */
  private Set<String> namingStems() {
    final Set<String> namingStems = new HashSet<>();
    for (final NamedItem item : names.items()) {
      if (item.kind() == ObjectKind.TABLE || item.kind() == ObjectKind.ROUTINE) {
        namingStems.addAll(item.stems());
      }
    }
    return namingStems;
  }

  /** For each stem used in a column name, the tables that have such a column. */
  private Map<String, Set<NamedObjectKey>> tablesByColumnStem() {
    final Map<String, Set<NamedObjectKey>> tablesByColumnStem = new HashMap<>();
    for (final NamedItem item : names.items()) {
      if (item.kind() == ObjectKind.COLUMN) {
        for (final String stem : item.stems()) {
          final NamedObjectKey tableKey = item.parentKey();
          tablesByColumnStem.computeIfAbsent(stem, key -> new HashSet<>()).add(tableKey);
        }
      }
    }
    return tablesByColumnStem;
  }
}
