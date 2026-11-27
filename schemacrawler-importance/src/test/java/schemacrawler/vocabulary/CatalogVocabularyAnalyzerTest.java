/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;

/** Tests the whole analysis through the public {@link Vocabulary}. */
public class CatalogVocabularyAnalyzerTest {

  private static final String[] WORDS = {
    "alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel", "india", "juliet"
  };

  /** Lets a small catalog be trusted, so that its terms can be seen. */
  private static final VocabularyThresholds LENIENT =
      new VocabularyThresholds(3, 0.3, 10, 0.25, 1, 0);

  private static List<String> stems(final Vocabulary vocabulary) {
    return vocabulary.topTerms(100).stream().map(VocabularyTerm::stem).toList();
  }

  /** Adds three routines for each word, so that each word has three objects. */
  private static TestCatalogBuilder withWordRoutines(
      final TestCatalogBuilder builder, final String... words) {
    for (final String word : words) {
      for (int i = 0; i < 3; i++) {
        builder.routine("get" + i + "_" + word);
      }
    }
    return builder;
  }

  @Test
  public void smallCatalogGivesRankedTerms() {
    final Catalog catalog =
        new TestCatalogBuilder()
            .table("customer", "customer_id", "customer_name")
            .table("order", "order_id", "customer_id")
            .table("invoice", "invoice_id", "order_id", "customer_id")
            .table("shipment", "shipment_id", "order_id")
            .build();

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(catalog, LENIENT);

    // "invoice" and "shipment" are each used by only 1 table, and "name" is generic
    assertThat(stems(vocabulary), contains("customer", "order"));
    // Each word is counted once for each table that uses it in its name or in a column name
    assertThat(vocabulary.topTerms(10).get(0).objectCount(), is(3));
    assertThat(vocabulary.topTerms(10).get(0).word(), is("customer"));
    assertThat(vocabulary.topTerms(10).get(1).objectCount(), is(3));
  }

  @Test
  public void triggersSequencesAndSynonymsContributeWords() {
    final Catalog catalog =
        new TestCatalogBuilder()
            .table("unit1")
            .triggers("audit_orders_trigger")
            .sequence("audit_number_seq")
            .synonym("audit_alias")
            .build();

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(catalog, LENIENT);

    assertThat(stems(vocabulary), contains("audit"));
    assertThat(vocabulary.topTerms(10).get(0).objectCount(), is(3));
  }

  @Test
  public void emptyCatalog() {
    final Vocabulary vocabulary =
        CatalogVocabularyAnalyzer.analyze(new TestCatalogBuilder().build());

    assertThat(vocabulary.isTrusted(), is(false));
    assertThat(vocabulary.topTerms(40), is(empty()));
  }

  @Test
  public void defaultThresholdsAreUsed() {
    final Catalog catalog = new TestCatalogBuilder().table("customer", "customer_id").build();

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(catalog);

    assertThat(vocabulary.isTrusted(), is(false));
  }

  // Trust

  @Test
  public void notTrustedWhenTooFewWords() {
    final TestCatalogBuilder builder = new TestCatalogBuilder();
    withWordRoutines(builder, Arrays.copyOf(WORDS, 9));
    // Both tables are covered, but only 9 words survive
    builder.table("t1", "alpha_data").table("t2", "bravo_data");

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(builder.build());

    assertThat(vocabulary.isTrusted(), is(false));
    assertThat(vocabulary.topTerms(40), is(empty()));
    // With a lower threshold the same 9 words are shown
    final Vocabulary lenient =
        CatalogVocabularyAnalyzer.analyze(
            builder.build(), new VocabularyThresholds(3, 0.3, 10, 0.25, 9, 0.5));
    assertThat(lenient.topTerms(40), hasSize(9));
  }

  @Test
  public void notTrustedWhenCoverageIsLow() {
    final TestCatalogBuilder builder = new TestCatalogBuilder();
    withWordRoutines(builder, WORDS);
    // Enough words survive, but only 1 of 4 tables has one of them
    builder.table("t1", "alpha_data").table("t2").table("t3").table("t4");

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(builder.build());

    assertThat(vocabulary.isTrusted(), is(false));
    assertThat(vocabulary.topTerms(40), is(empty()));
  }

  @Test
  public void trustedWhenEnoughWordsCoverEnoughTables() {
    final TestCatalogBuilder builder = new TestCatalogBuilder();
    withWordRoutines(builder, WORDS);
    // Exactly half of the 4 tables are covered
    builder.table("t1", "alpha_data").table("t2", "bravo_data").table("t3").table("t4");

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(builder.build());

    assertThat(vocabulary.isTrusted(), is(true));
    assertThat(vocabulary.topTerms(40), hasSize(10));
    assertThat(vocabulary.topTerms(3), hasSize(3));
  }

  // Synthetic catalogs

  @Test
  public void naturalLanguageSchemaIsTrusted() {
    final String[] words = {
      "customer",
      "order",
      "invoice",
      "product",
      "shipment",
      "payment",
      "address",
      "supplier",
      "warehouse",
      "category",
      "contract",
      "employee"
    };
    final TestCatalogBuilder builder = new TestCatalogBuilder();
    for (int i = 0; i < words.length; i++) {
      final String next = words[(i + 1) % words.length];
      final String afterNext = words[(i + 2) % words.length];
      builder.table(words[i] + "_record", words[i] + "_id", next + "_id", afterNext + "_id");
    }

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(builder.build());

    assertThat(vocabulary.isTrusted(), is(true));
    assertThat(vocabulary.topTerms(40).size() >= 12, is(true));
  }

  @Test
  public void codeStyleSchemaIsNotTrusted() {
    final TestCatalogBuilder builder = new TestCatalogBuilder();
    for (final String table : new String[] {"VBAK", "VBAP", "VBRK", "KNA1", "KNVV", "MARA"}) {
      builder.table(table, "VBELN", "KUNNR", "MATNR", "ERDAT", "ERNAM", "WAERK");
    }

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(builder.build(), LENIENT);

    // Nothing is credited, so even lenient thresholds find no words
    assertThat(stems(vocabulary), is(empty()));
    assertThat(CatalogVocabularyAnalyzer.analyze(builder.build()).isTrusted(), is(false));
  }

  @Test
  public void singleWordSchemaIsNotTrusted() {
    final TestCatalogBuilder builder = new TestCatalogBuilder();
    for (final String table :
        new String[] {
          "customers",
          "orders",
          "invoices",
          "products",
          "shipments",
          "payments",
          "suppliers",
          "warehouses",
          "categories",
          "contracts",
          "employees",
          "addresses"
        }) {
      builder.table(table, "name", "email", "phone", "street");
    }

    final Vocabulary vocabulary = CatalogVocabularyAnalyzer.analyze(builder.build());

    assertThat(vocabulary.isTrusted(), is(false));
    assertThat(vocabulary.topTerms(40), is(empty()));
  }

  // Logging

  @Test
  public void decisionIsLogged() {
    final Logger logger = Logger.getLogger(CatalogVocabularyAnalyzer.class.getName());
    final List<String> messages = new ArrayList<>();
    final Handler handler =
        new Handler() {
          @Override
          public void publish(final LogRecord logRecord) {
            messages.add(logRecord.getLevel() + ": " + logRecord.getMessage());
          }

          @Override
          public void flush() {}

          @Override
          public void close() {}
        };
    final Level originalLevel = logger.getLevel();
    logger.setLevel(Level.INFO);
    logger.addHandler(handler);
    try {
      final TestCatalogBuilder builder = new TestCatalogBuilder();
      withWordRoutines(builder, WORDS);
      builder.table("t1", "alpha_data").table("t2");

      CatalogVocabularyAnalyzer.analyze(builder.build());
    } finally {
      logger.removeHandler(handler);
      logger.setLevel(originalLevel);
    }

    assertThat(messages, hasSize(1));
    assertThat(messages.get(0), containsString("INFO"));
    assertThat(messages.get(0), containsString("table coverage"));
    assertThat(messages.get(0), containsString("<10> words kept"));
    assertThat(messages.get(0), containsString("vocabulary trusted"));
  }
}
