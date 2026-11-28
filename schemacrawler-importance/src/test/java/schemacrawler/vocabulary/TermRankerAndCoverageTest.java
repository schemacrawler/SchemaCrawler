/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.NamedObjectKey;

/** Tests ranking and table coverage on hand-made names, with no catalog. */
public class TermRankerAndCoverageTest {

  private static final VocabularyThresholds THRESHOLDS = VocabularyThresholds.DEFAULT;

  private static void addRoutines(final NamesFixture fixture, final String word, final int count) {
    for (int i = 0; i < count; i++) {
      fixture.add(ObjectKind.ROUTINE, "get" + i + "_" + word);
    }
  }

  private static List<VocabularyTerm> rank(final Names names) {
    return new TermRanker(THRESHOLDS).rank(names, names.allStems());
  }

  private static List<String> stems(final List<VocabularyTerm> terms) {
    return terms.stream().map(VocabularyTerm::stem).toList();
  }

  // Ranking

  @Test
  public void ranksByObjectCountAndDropsRareWords() {
    final NamesFixture fixture = new NamesFixture();
    addRoutines(fixture, "order", 12);
    addRoutines(fixture, "invoice", 5);
    addRoutines(fixture, "warehouse", 1);

    final List<VocabularyTerm> terms = rank(fixture.build());

    assertThat(stems(terms), contains("order", "invoice"));
    assertThat(terms.get(0).objectCount(), is(12));
    assertThat(terms.get(1).objectCount(), is(5));
  }

  @Test
  public void tiesAreOrderedAlphabetically() {
    final NamesFixture fixture = new NamesFixture();
    addRoutines(fixture, "zebra", 3);
    addRoutines(fixture, "apple", 3);
    addRoutines(fixture, "mango", 3);
    final Names names = fixture.build();

    assertThat(stems(rank(names)), contains("apple", "mango", "zebra"));
    assertThat(stems(rank(names)), is(stems(rank(names))));
  }

  @Test
  public void wordsInManyColumnsOfOneTableCountOnce() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "unit1");
    for (int i = 0; i < 10; i++) {
      fixture.add(ObjectKind.COLUMN, table, "customer_part" + i);
    }
    final Names names = fixture.build();

    assertThat(rank(names), is(empty()));
    final VocabularyThresholds lenient = new VocabularyThresholds(1, 0.3, 10, 0.25, 10, 0.5);
    final List<VocabularyTerm> terms = new TermRanker(lenient).rank(names, Set.of("customer"));
    assertThat(terms.get(0).objectCount(), is(1));
  }

  @Test
  public void parametersCountForTheirRoutine() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey routine = fixture.add(ObjectKind.ROUTINE, "refresh_customer");
    fixture.add(ObjectKind.PARAMETER, routine, "customer_one");
    fixture.add(ObjectKind.PARAMETER, routine, "customer_two");
    final Names names = fixture.build();

    final VocabularyThresholds lenient = new VocabularyThresholds(1, 0.3, 10, 0.25, 10, 0.5);
    final List<VocabularyTerm> terms = new TermRanker(lenient).rank(names, Set.of("customer"));

    assertThat(terms.get(0).objectCount(), is(1));
  }

  @Test
  public void tablesAndRoutinesCountTogether() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey first = fixture.add(ObjectKind.TABLE, "unit1");
    fixture.add(ObjectKind.COLUMN, first, "id_customer");
    final NamedObjectKey second = fixture.add(ObjectKind.TABLE, "unit2");
    fixture.add(ObjectKind.COLUMN, second, "id_customer");
    final NamedObjectKey routine = fixture.add(ObjectKind.ROUTINE, "refresh_data");
    fixture.add(ObjectKind.PARAMETER, routine, "id_customer");

    final List<VocabularyTerm> terms = rank(fixture.build());

    assertThat(stems(terms), contains("customer"));
    assertThat(terms.get(0).objectCount(), is(3));
  }

  @Test
  public void topLevelObjectsOfEveryKindCountOnce() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.SEQUENCE, "audit_number");
    fixture.add(ObjectKind.SYNONYM, "audit_alias");
    fixture.add(ObjectKind.ROUTINE, "audit_refresh");

    final List<VocabularyTerm> terms = rank(fixture.build());

    assertThat(stems(terms), contains("audit"));
    assertThat(terms.get(0).objectCount(), is(3));
  }

  private static VocabularyTerm termFor(final List<VocabularyTerm> terms, final String stem) {
    return terms.stream().filter(term -> term.stem().equals(stem)).findFirst().orElseThrow();
  }

  @Test
  public void pluralIsShownWhenItIsWritten() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.ROUTINE, "get1_orders");
    fixture.add(ObjectKind.ROUTINE, "get2_orders");
    fixture.add(ObjectKind.ROUTINE, "get3_order");

    final VocabularyTerm term = termFor(rank(fixture.build()), "order");

    assertThat(term.word(), is("orders"));
    assertThat(term.objectCount(), is(3));
  }

  @Test
  public void pluralIsShownEvenWhenFewerOwnersUseIt() {
    // One table is named "orders" and two others have a column "order_id"
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.TABLE, "orders");
    final NamedObjectKey invoice = fixture.add(ObjectKind.TABLE, "invoice");
    fixture.add(ObjectKind.COLUMN, invoice, "order_id");
    final NamedObjectKey shipment = fixture.add(ObjectKind.TABLE, "shipment");
    fixture.add(ObjectKind.COLUMN, shipment, "order_id");

    final List<VocabularyTerm> terms = rank(fixture.build());

    assertThat(stems(terms), contains("order"));
    assertThat(terms.get(0).word(), is("orders"));
    assertThat(terms.get(0).stem(), is("order"));
    // The count is the owners using either word, and each owner counts once
    assertThat(terms.get(0).objectCount(), is(3));
  }

  @Test
  public void stemIsShownWhenOnlyTheSingularIsWritten() {
    final NamesFixture fixture = new NamesFixture();
    for (int i = 0; i < 3; i++) {
      final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "unit" + i);
      fixture.add(ObjectKind.COLUMN, table, "order_id");
    }

    final VocabularyTerm term = termFor(rank(fixture.build()), "order");

    assertThat(term.word(), is("order"));
    assertThat(term.stem(), is("order"));
    assertThat(term.objectCount(), is(3));
  }

  @Test
  public void irregularPluralIsShownBesideItsStem() {
    final NamesFixture fixture = new NamesFixture();
    addRoutines(fixture, "categories", 3);

    final VocabularyTerm term = termFor(rank(fixture.build()), "category");

    assertThat(term.word(), is("categories"));
    assertThat(term.stem(), is("category"));
  }

  @Test
  public void firstOfSeveralWordsIsShown() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.ROUTINE, "get1_media");
    fixture.add(ObjectKind.ROUTINE, "get2_media");
    fixture.add(ObjectKind.ROUTINE, "get3_mediums");

    final VocabularyTerm term = termFor(rank(fixture.build()), "medium");

    assertThat(term.word(), is("media"));
    assertThat(term.objectCount(), is(3));
  }

  @Test
  public void onlyGivenStemsAreRanked() {
    final NamesFixture fixture = new NamesFixture();
    addRoutines(fixture, "order", 3);
    addRoutines(fixture, "invoice", 3);
    final Names names = fixture.build();

    final List<VocabularyTerm> terms = new TermRanker(THRESHOLDS).rank(names, Set.of("invoice"));

    assertThat(stems(terms), contains("invoice"));
  }

  @Test
  public void minimumObjectCountBoundary() {
    final NamesFixture fixture = new NamesFixture();
    addRoutines(fixture, "order", 2);
    final Names names = fixture.build();

    assertThat(rank(names).isEmpty(), is(true));
    final VocabularyThresholds lower = new VocabularyThresholds(2, 0.3, 10, 0.25, 10, 0.5);
    assertThat(new TermRanker(lower).rank(names, names.allStems()).size(), is(1));
  }

  // Coverage

  private static VocabularyTerm term(final String stem) {
    return new VocabularyTerm(stem, stem, 3);
  }

  @Test
  public void coverageCountsTablesWithATermInTheirNameOrColumns() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.TABLE, "customer_record");
    final NamedObjectKey second = fixture.add(ObjectKind.TABLE, "unit1");
    fixture.add(ObjectKind.COLUMN, second, "order_id");
    fixture.add(ObjectKind.TABLE, "unit2");
    fixture.add(ObjectKind.TABLE, "unit3");

    final double coverage =
        TableCoverage.of(fixture.build(), List.of(term("customer"), term("order")));

    assertThat(coverage, closeTo(0.5, 1e-9));
  }

  @Test
  public void coverageCountsTablesWithATermInAContainedObjectName() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey withTrigger = fixture.add(ObjectKind.TABLE, "unit1");
    fixture.add(ObjectKind.TRIGGER, withTrigger, "audit_customer");
    final NamedObjectKey withIndex = fixture.add(ObjectKind.TABLE, "unit2");
    fixture.add(ObjectKind.INDEX, withIndex, "idx_customer");
    final NamedObjectKey withConstraint = fixture.add(ObjectKind.TABLE, "unit3");
    fixture.add(ObjectKind.CONSTRAINT, withConstraint, "fk_customer");
    fixture.add(ObjectKind.TABLE, "unit4");

    assertThat(TableCoverage.of(fixture.build(), List.of(term("customer"))), closeTo(0.75, 1e-9));
  }

  @Test
  public void coverageIgnoresRoutinesSequencesAndSynonyms() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.TABLE, "unit1");
    fixture.add(ObjectKind.ROUTINE, "refresh_customer");
    fixture.add(ObjectKind.SEQUENCE, "customer_seq");
    fixture.add(ObjectKind.SYNONYM, "customer_alias");

    assertThat(TableCoverage.of(fixture.build(), List.of(term("customer"))), is(0.0));
  }

  @Test
  public void coverageCountsATableOnceForManyWords() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "customer_order");
    fixture.add(ObjectKind.COLUMN, table, "customer_id");

    assertThat(
        TableCoverage.of(fixture.build(), List.of(term("customer"), term("order"))), is(1.0));
  }

  @Test
  public void coverageIsZeroWithoutTablesOrTerms() {
    final NamesFixture noTables = new NamesFixture();
    noTables.add(ObjectKind.ROUTINE, "refresh_customer");
    assertThat(TableCoverage.of(noTables.build(), List.of(term("customer"))), is(0.0));

    final NamesFixture withTable = new NamesFixture();
    withTable.add(ObjectKind.TABLE, "customer_record");
    assertThat(TableCoverage.of(withTable.build(), List.of()), is(0.0));
  }
}
