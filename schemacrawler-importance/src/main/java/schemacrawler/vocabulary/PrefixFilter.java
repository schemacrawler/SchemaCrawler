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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Drops a word that is the first segment of a large share of the names of one object kind, such as
 * {@code proc} in {@code proc_x}. Such words describe the kind of object and say nothing about the
 * domain.
 */
final class PrefixFilter implements UnaryOperator<Set<String>> {

  private final Names names;
  private final VocabularyThresholds thresholds;

  PrefixFilter(final Names names, final VocabularyThresholds thresholds) {
    this.names = requireNonNull(names, "No names provided");
    this.thresholds = requireNonNull(thresholds, "No thresholds provided");
  }

  @Override
  public Set<String> apply(final Set<String> stems) {
    requireNonNull(stems, "No stems provided");
    final Set<String> kept = new HashSet<>(stems);
    for (final ObjectKind kind : ObjectKind.values()) {
      final List<NamedItem> kindItems = names.itemsOfKind(kind);
      if (kindItems.size() < thresholds.minNamesForPrefixCheck()) {
        continue;
      }
      final Map<String, Integer> prefixCounts = countPrefixes(kindItems);
      for (final Map.Entry<String, Integer> prefixCount : prefixCounts.entrySet()) {
        if (prefixCount.getValue() >= thresholds.prefixShare() * kindItems.size()) {
          kept.remove(prefixCount.getKey());
        }
      }
    }
    return Set.copyOf(kept);
  }

  /** Counts the names that start with each word. */
  private Map<String, Integer> countPrefixes(final List<NamedItem> kindItems) {
    final Map<String, Integer> prefixCounts = new HashMap<>();
    for (final NamedItem item : kindItems) {
      final Set<String> prefixes = new HashSet<>();
      for (final Token token : item.tokenization().tokens()) {
        if (token.isFirstSegment()) {
          prefixes.add(token.stem());
        }
      }
      for (final String prefix : prefixes) {
        prefixCounts.merge(prefix, 1, Integer::sum);
      }
    }
    return prefixCounts;
  }
}
