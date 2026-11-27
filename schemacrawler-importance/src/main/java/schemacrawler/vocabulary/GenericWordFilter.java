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
import schemacrawler.schema.NamedObjectKey;

/**
 * Drops a generic word: one used in the column names of a large share of tables, such as {@code
 * created}, that is never part of a table or routine name. A word in a table or routine name is
 * never generic, which protects domain words in small schemas.
 */
final class GenericWordFilter implements StemFilter {

  private final Names names;
  private final VocabularyThresholds thresholds;

  GenericWordFilter(final Names names, final VocabularyThresholds thresholds) {
    this.names = requireNonNull(names, "No names provided");
    this.thresholds = requireNonNull(thresholds, "No thresholds provided");
  }

  @Override
  public Set<String> apply(final Set<String> stems) {
    final Set<String> kept = new HashSet<>(stems);
    final int tableCount = names.tableCount();
    if (tableCount == 0) {
      return kept;
    }
    final Set<String> namingStems = new HashSet<>();
    final Map<String, Set<NamedObjectKey>> tablesByColumnStem = new HashMap<>();
    for (final NamedItem item : names.items()) {
      if (item.kind() == ObjectKind.TABLE || item.kind() == ObjectKind.ROUTINE) {
        namingStems.addAll(item.stems());
      } else if (item.kind() == ObjectKind.COLUMN) {
        for (final String stem : item.stems()) {
          tablesByColumnStem.computeIfAbsent(stem, key -> new HashSet<>()).add(item.parentKey());
        }
      }
    }
    for (final Map.Entry<String, Set<NamedObjectKey>> entry : tablesByColumnStem.entrySet()) {
      final boolean isInManyTables =
          entry.getValue().size() >= thresholds.genericTableShare() * tableCount;
      if (isInManyTables && !namingStems.contains(entry.getKey())) {
        kept.remove(entry.getKey());
      }
    }
    return kept;
  }
}
