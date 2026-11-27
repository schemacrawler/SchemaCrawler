/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

/**
 * Every threshold used to build a vocabulary, in one place. {@link #DEFAULT} holds conservative
 * values, chosen so that the vocabulary is left out when in doubt. They have not been validated on
 * real databases.
 *
 * @param minObjectCount Words used by fewer distinct objects than this are dropped
 * @param prefixShare A word that starts at least this share of the names of one object kind is a
 *     prefix
 * @param minNamesForPrefixCheck Object kinds with fewer names than this are not checked for
 *     prefixes
 * @param genericTableShare A word in the column names of at least this share of tables is generic,
 *     unless it is also in a table or routine name
 * @param minTermCount Fewer surviving words than this means the vocabulary is not trusted
 * @param minTableCoverage The share of tables that must contain a surviving word for the vocabulary
 *     to be trusted
 */
record VocabularyThresholds(
    int minObjectCount,
    double prefixShare,
    int minNamesForPrefixCheck,
    double genericTableShare,
    int minTermCount,
    double minTableCoverage) {

  static final VocabularyThresholds DEFAULT = new VocabularyThresholds(3, 0.3, 10, 0.25, 10, 0.5);

  VocabularyThresholds {
    requireAtLeastOne(minObjectCount, "minObjectCount");
    requireShare(prefixShare, "prefixShare");
    requireAtLeastOne(minNamesForPrefixCheck, "minNamesForPrefixCheck");
    requireShare(genericTableShare, "genericTableShare");
    requireAtLeastOne(minTermCount, "minTermCount");
    requireShare(minTableCoverage, "minTableCoverage");
  }

  private static void requireAtLeastOne(final int value, final String name) {
    if (value < 1) {
      throw new IllegalArgumentException(String.format("%s must be at least 1: %d", name, value));
    }
  }

  private static void requireShare(final double value, final String name) {
    if (!(value >= 0 && value <= 1)) {
      throw new IllegalArgumentException(
          String.format("%s must be between 0 and 1: %s", name, value));
    }
  }
}
