/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import us.fatehi.utility.string.StringFormat;

/**
 * Analyzes the names of the objects in a catalog to find its {@link Vocabulary}, with no dictionary
 * and no knowledge of any language. The steps run in this order, and each narrows the set of words
 * that are kept:
 *
 * <ol>
 *   <li>Credit: keep a word only if it appears inside a name with two or more segments.
 *   <li>Prefixes: drop a word that starts a large share of the names of one kind of object.
 *   <li>Generic: drop a word used in the columns of many tables that is never in a table or routine
 *       name.
 *   <li>Rank: count the distinct objects using each remaining word, and drop rare words.
 *   <li>Trust: trust the result only if enough words survive and they cover enough tables.
 * </ol>
 */
public final class CatalogVocabularyAnalyzer {

  private static final Logger LOGGER = Logger.getLogger(CatalogVocabularyAnalyzer.class.getName());

  /**
   * Analyzes the names in a catalog.
   *
   * @param catalog Catalog to read the object names from
   * @return Vocabulary, which may not be trusted
   */
  public static Vocabulary analyze(final Catalog catalog) {
    return analyze(catalog, VocabularyThresholds.DEFAULT);
  }

  public static Vocabulary analyze(final Catalog catalog, final VocabularyThresholds thresholds) {
    requireNonNull(catalog, "No catalog provided");
    requireNonNull(thresholds, "No thresholds provided");
    final Names names = CatalogNames.read(catalog);
    final CatalogVocabularyAnalyzer analyzer = new CatalogVocabularyAnalyzer(names, thresholds);
    return analyzer.vocabulary();
  }

  private final Names names;
  private final VocabularyThresholds thresholds;

  private CatalogVocabularyAnalyzer(final Names names, final VocabularyThresholds thresholds) {
    this.names = requireNonNull(names, "No names provided");
    this.thresholds = requireNonNull(thresholds, "No thresholds provided");
  }

  private Vocabulary vocabulary() {
    final UnaryOperator<Set<String>> stemFilters =
        set ->
            new CreditFilter(names)
                .andThen(new PrefixFilter(names, thresholds))
                .andThen(new GenericWordFilter(names, thresholds))
                .apply(set);
    final Set<String> stems = stemFilters.apply(names.allStems());

    final List<VocabularyTerm> terms = new TermRanker(thresholds).rank(names, stems);

    final double coverage = TableCoverage.of(names, terms);
    final boolean trusted =
        terms.size() >= thresholds.minTermCount() && coverage >= thresholds.minTableCoverage();

    LOGGER.log(
        Level.INFO,
        new StringFormat(
            "Schema vocabulary: table coverage <%.2f>, <%d> words kept, vocabulary %s",
            coverage, terms.size(), trusted ? "trusted" : "not trusted"));

    return new Vocabulary(terms, trusted);
  }
}
