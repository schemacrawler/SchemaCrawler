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

  /** The objects that use a stem, and the words in which the stem is written. */
  private static final class StemUsage {

    private final Set<NamedObjectKey> owners = new HashSet<>();
    // In alphabetical order, so that the shown word does not depend on the order of
    // the names
    private final Set<String> words = new TreeSet<>();

    void add(final NamedObjectKey owner, final String word) {
      owners.add(owner);
      words.add(word);
    }

    int objectCount() {
      return owners.size();
    }

    Set<String> words() {
      return words;
    }
  }

  private final VocabularyThresholds thresholds;

  TermRanker(final VocabularyThresholds thresholds) {
    this.thresholds = thresholds;
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
    final List<VocabularyTerm> terms = new ArrayList<>();
    final Map<String, StemUsage> usageMap = collectUsage(names, stems);
    for (final Map.Entry<String, StemUsage> entry : usageMap.entrySet()) {
      final String stem = entry.getKey();
      final StemUsage usage = entry.getValue();
      if (usage.objectCount() >= thresholds.minObjectCount()) {
        terms.add(new VocabularyTerm(shownWord(stem, usage.words()), stem, usage.objectCount()));
      }
    }
    terms.sort(comparingInt(VocabularyTerm::objectCount).reversed());
    return List.copyOf(terms);
  }

  /**
   * Collects, for each kept stem, the objects that use it and the words written for it. The result
   * is sorted by stem, so that the final sort by count keeps ties in alphabetical order.
   */
  private Map<String, StemUsage> collectUsage(final Names names, final Set<String> stems) {
    final Map<String, StemUsage> usageByStem = new TreeMap<>();
    for (final NamedItem item : names.items()) {
      for (final Token token : item.tokenization().tokens()) {
        final String stem = token.stem();
        if (stems.contains(stem)) {
          usageByStem
              .computeIfAbsent(stem, key -> new StemUsage())
              .add(item.parentKey(), token.word());
        }
      }
    }
    return usageByStem;
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
