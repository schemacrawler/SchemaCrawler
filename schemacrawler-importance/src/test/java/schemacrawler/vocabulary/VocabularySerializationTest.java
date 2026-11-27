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
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;

/** Proves that the vocabulary model can be serialized and read back. */
public class VocabularySerializationTest {

  /**
   * Only the model's own types and plain JDK types may appear in the stream. A reference to a
   * catalog object, a key or any other type fails the read.
   */
  private static final ObjectInputFilter MODEL_ONLY =
      ObjectInputFilter.Config.createFilter(
          "schemacrawler.vocabulary.*;java.util.*;java.lang.*;!*");

  private static <T> T roundTrip(final T object, final Class<T> type)
      throws IOException, ClassNotFoundException {
    final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (final ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(object);
    }
    try (final ObjectInputStream in =
        new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      in.setObjectInputFilter(MODEL_ONLY);
      return type.cast(in.readObject());
    }
  }

  private static List<VocabularyTerm> words(final int count) {
    final List<VocabularyTerm> terms = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      terms.add(new VocabularyTerm("words" + i, "word" + i, 100 - i));
    }
    return terms;
  }

  @Test
  public void termSurvivesARoundTrip() throws Exception {
    final VocabularyTerm term = new VocabularyTerm("orders", "order", 7);

    final VocabularyTerm copy = roundTrip(term, VocabularyTerm.class);

    assertThat(copy, is(term));
    assertThat(copy.word(), is("orders"));
    assertThat(copy.stem(), is("order"));
    assertThat(copy.objectCount(), is(7));
  }

  @Test
  public void trustedVocabularySurvivesARoundTrip() throws Exception {
    final Vocabulary vocabulary = new Vocabulary(words(60), true);

    final Vocabulary copy = roundTrip(vocabulary, Vocabulary.class);

    assertThat(copy.isTrusted(), is(true));
    assertThat(copy.topTerms(100), hasSize(60));
    assertThat(copy.topTerms(100), is(vocabulary.topTerms(100)));
    assertThat(copy.topTerms(5), is(vocabulary.topTerms(5)));
  }

  @Test
  public void untrustedVocabularyStillShowsNothingAfterARoundTrip() throws Exception {
    final Vocabulary vocabulary = new Vocabulary(words(60), false);

    final Vocabulary copy = roundTrip(vocabulary, Vocabulary.class);

    assertThat(copy.isTrusted(), is(false));
    assertThat(copy.topTerms(40), is(empty()));
  }

  @Test
  public void copyIsNotTheSameObjectAndStaysImmutable() throws Exception {
    final Vocabulary vocabulary = new Vocabulary(words(10), true);

    final Vocabulary copy = roundTrip(vocabulary, Vocabulary.class);

    assertThat(copy == vocabulary, is(false));
    assertThat(copy.topTerms(10) == vocabulary.topTerms(10), is(false));
    assertThrows(UnsupportedOperationException.class, () -> copy.topTerms(10).clear());
    assertThrows(
        UnsupportedOperationException.class,
        () -> copy.topTerms(1).add(new VocabularyTerm("word", "word", 1)));
  }

  @Test
  public void vocabularyAnalyzedFromACatalogSurvivesARoundTrip() throws Exception {
    final TestCatalogBuilder builder = new TestCatalogBuilder();
    for (final String word : new String[] {"alpha", "bravo", "charlie", "delta", "echo"}) {
      for (int i = 0; i < 3; i++) {
        builder.routine("get" + i + "_" + word);
      }
    }
    final Catalog catalog = builder.build();
    // Lenient thresholds, so that the small catalog gives a trusted vocabulary
    final VocabularyThresholds lenient = new VocabularyThresholds(3, 0.3, 10, 0.25, 1, 0);
    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(catalog, lenient);
    assertThat(vocabulary.topTerms(10), is(not(empty())));

    final Vocabulary copy = roundTrip(vocabulary, Vocabulary.class);

    assertThat(copy.isTrusted(), is(vocabulary.isTrusted()));
    assertThat(copy.topTerms(10), is(vocabulary.topTerms(10)));
  }
}
