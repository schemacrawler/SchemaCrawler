/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Comparator.comparingInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import schemacrawler.schema.NamedObjectKey;

/**
 * Counts the distinct top-level objects using each kept word, drops rare words, and sorts by count
 * descending and then alphabetically by stem. A word is used by a table or routine when it is in
 * the name of the table or routine, or in the name of anything it contains, such as a column or a
 * parameter. A word that is used in many columns of one table still counts once.
 *
 * <p>Each term shows one word for its stem: the alphabetically first word that differs from the
 * stem, which is usually the plural, or the stem itself when that is the only word written. The
 * shown word always occurs in a name.
 */
final class TermRanker {

  private final VocabularyThresholds thresholds;

  TermRanker(final VocabularyThresholds thresholds) {
    this.thresholds = thresholds;
  }

  List<VocabularyTerm> rank(final Names names, final Set<String> stems) {
    // Sorted by stem, so the final sort by count keeps ties in alphabetical order
    final Map<String, Set<NamedObjectKey>> ownersByStem = new TreeMap<>();
    // For each stem, the words written for it, in alphabetical order
    final Map<String, Set<String>> wordsByStem = new HashMap<>();
    for (final NamedItem item : names.items()) {
      for (final Token token : item.tokenization().tokens()) {
        if (!stems.contains(token.stem())) {
          continue;
        }
        ownersByStem.computeIfAbsent(token.stem(), key -> new HashSet<>()).add(item.parentKey());
        wordsByStem.computeIfAbsent(token.stem(), key -> new TreeSet<>()).add(token.word());
      }
    }

    final List<VocabularyTerm> terms = new ArrayList<>();
    for (final Map.Entry<String, Set<NamedObjectKey>> entry : ownersByStem.entrySet()) {
      final int objectCount = entry.getValue().size();
      if (objectCount < thresholds.minObjectCount()) {
        continue;
      }
      final String word = shownWord(entry.getKey(), wordsByStem.get(entry.getKey()));
      terms.add(new VocabularyTerm(word, entry.getKey(), objectCount));
    }
    terms.sort(comparingInt(VocabularyTerm::objectCount).reversed());
    return List.copyOf(terms);
  }

  /**
   * The word to show for a stem: the alphabetically first word that differs from the stem, or the
   * stem itself if it is the only word written.
   */
  private String shownWord(final String stem, final Set<String> words) {
    for (final String word : words) {
      if (!word.equals(stem)) {
        return word;
      }
    }
    return stem;
  }
}
