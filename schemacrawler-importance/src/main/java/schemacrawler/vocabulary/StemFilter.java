/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A step that narrows the set of word stems that are kept. A filter is built on the names of one
 * catalog, and is applied to the stems kept so far. It returns a new set and does not modify its
 * argument, so filters can be chained with {@code andThen}.
 */
sealed interface StemFilter extends UnaryOperator<Set<String>>
    permits CreditFilter, PrefixFilter, GenericWordFilter {

  static Function<Set<String>, Set<String>> filters(
      final Names names, final VocabularyThresholds thresholds) {
    return new CreditFilter(names)
        .andThen(new PrefixFilter(names, thresholds))
        .andThen(new GenericWordFilter(names, thresholds));
  }
}
