/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Comparator.comparingInt;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    this.thresholds = requireNonNull(thresholds, "No thresholds provided");
  }

  /**
   * Ranks the kept stems. A stem is the grouping key, and one stem can have more than one word,
   * because different written words can have the same singular form: {@code order} and {@code
   * orders} have the stem {@code order}, and {@code media} and {@code mediums} have the stem {@code
   * medium}. The count for a stem is the distinct owners that use any of its words, so an owner
   * that uses two words counts once.
   *
   * @param names Names to read the words from
   * @param stems Stems to keep, and to rank
   * @return Terms sorted by count descending and then by stem, without rare words
   */
  List<VocabularyTerm> rank(final Names names, final Set<String> stems) {
    requireNonNull(names, "No names provided");
    requireNonNull(stems, "No stems provided");

    final List<VocabularyTerm> terms = new ArrayList<>();
    // The usage is sorted by stem, so that the final sort by count keeps ties in alphabetical order
    for (final Map.Entry<String, StemUsage> entry : names.usageByStem().entrySet()) {
      final String stem = entry.getKey();
      final StemUsage usage = entry.getValue();
      final int objectCount = usage.objectCount();
      if (stems.contains(stem) && objectCount >= thresholds.minObjectCount()) {
        final String word = shownWord(stem, usage.words());
        terms.add(new VocabularyTerm(word, stem, objectCount));
      }
    }
    terms.sort(comparingInt(VocabularyTerm::objectCount).reversed());
    return List.copyOf(terms);
  }

  /**
   * The word to show for a stem: the alphabetically first word that differs from the stem, or the
   * stem itself if it is the only word written. A stem can have several words, such as {@code
   * order} and {@code orders}, and the shown word is chosen without counting how often each is
   * used. It is a word that occurs in a name, which is not always true of the stem, as for {@code
   * categories} and its stem {@code category}.
   *
   * @param stem The singular form that groups the words
   * @param words The distinct words written for the stem, in alphabetical order
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
