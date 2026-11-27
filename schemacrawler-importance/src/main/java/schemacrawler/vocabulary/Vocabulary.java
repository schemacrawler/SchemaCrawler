/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * The immutable vocabulary of a catalog: the words used in the names of its objects, ranked by the
 * number of objects that use them, and whether the vocabulary is trusted enough to show. A
 * vocabulary is built once, and may be shared between threads. It is serializable, and holds only
 * terms and a flag, with no reference to the catalog it was built from.
 */
public final class Vocabulary implements Serializable {

  @Serial private static final long serialVersionUID = 7260418395620871934L;

  private final List<VocabularyTerm> rankedTerms;
  private final boolean trusted;

  /**
   * Creates a vocabulary.
   *
   * @param rankedTerms Terms in rank order, which are copied
   * @param trusted Whether the terms should be shown
   */
  public Vocabulary(final List<VocabularyTerm> rankedTerms, final boolean trusted) {
    this.rankedTerms = List.copyOf(requireNonNull(rankedTerms, "No terms provided"));
    this.trusted = trusted;
  }

  /** Whether the vocabulary is good enough to show to a reader. */
  public boolean isTrusted() {
    return trusted;
  }

  /**
   * Returns the highest-ranked terms, up to the limit, in rank order. The result is empty when the
   * vocabulary is not trusted. The returned list cannot be modified.
   *
   * @param limit Maximum number of terms to return
   * @return Top terms, or an empty list
   */
  public List<VocabularyTerm> topTerms(final int limit) {
    if (!trusted || limit <= 0) {
      return List.of();
    }
    final List<VocabularyTerm> terms = rankedTerms();
    return terms.subList(0, Math.min(limit, terms.size()));
  }

  /** The ranked terms, whether or not the vocabulary is trusted. */
  private List<VocabularyTerm> rankedTerms() {
    return rankedTerms;
  }
}
