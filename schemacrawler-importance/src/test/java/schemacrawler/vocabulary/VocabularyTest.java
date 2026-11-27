/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

public class VocabularyTest {

  private static List<VocabularyTerm> words(final int count) {
    final List<VocabularyTerm> terms = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      terms.add(new VocabularyTerm("word" + i, "word" + i, 100 - i));
    }
    return terms;
  }

  @Test
  public void vocabularyIsImmutable() {
    final List<VocabularyTerm> terms = new ArrayList<>();
    terms.add(new VocabularyTerm("order", "order", 5));
    terms.add(new VocabularyTerm("customer", "customer", 3));
    final Vocabulary vocabulary = new Vocabulary(terms, true);

    terms.clear();

    assertThat(vocabulary.topTerms(10), hasSize(2));
    assertThrows(UnsupportedOperationException.class, () -> vocabulary.topTerms(10).clear());
    assertThrows(
        UnsupportedOperationException.class,
        () -> vocabulary.topTerms(1).add(new VocabularyTerm("word", "word", 1)));
  }

  @Test
  public void topTermsLimit() {
    final Vocabulary vocabulary = new Vocabulary(words(60), true);

    final List<VocabularyTerm> top = vocabulary.topTerms(40);

    assertThat(top, hasSize(40));
    assertThat(top.get(0).stem(), is("word0"));
    assertThat(top.get(39).stem(), is("word39"));
    assertThat(vocabulary.topTerms(0), is(empty()));
    assertThat(vocabulary.topTerms(100), hasSize(60));
  }

  @Test
  public void untrustedVocabularyHasNoTerms() {
    final Vocabulary vocabulary = new Vocabulary(words(60), false);

    assertThat(vocabulary.isTrusted(), is(false));
    assertThat(vocabulary.topTerms(40), is(empty()));
  }

  @Test
  public void concurrentUse() throws Exception {
    final Vocabulary vocabulary = new Vocabulary(words(20), true);
    final List<VocabularyTerm> expected = vocabulary.topTerms(10);

    final ExecutorService executor = Executors.newFixedThreadPool(8);
    try {
      final List<Future<Boolean>> results = new ArrayList<>();
      for (int thread = 0; thread < 8; thread++) {
        results.add(
            executor.submit(
                () -> {
                  for (int i = 0; i < 1000; i++) {
                    if (!vocabulary.topTerms(10).equals(expected)) {
                      return false;
                    }
                  }
                  return true;
                }));
      }
      for (final Future<Boolean> result : results) {
        assertThat(result.get(), is(true));
      }
    } finally {
      executor.shutdownNow();
    }
  }
}
