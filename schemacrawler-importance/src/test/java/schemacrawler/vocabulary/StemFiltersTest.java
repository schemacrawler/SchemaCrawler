/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.NamedObjectKey;

/** Tests the credit, prefix and generic steps on hand-made names, with no catalog. */
public class StemFiltersTest {

  private static final VocabularyThresholds THRESHOLDS = VocabularyThresholds.DEFAULT;

  private static Set<String> apply(final Function<Names, StemFilter> filter, final Names names) {
    return filter.apply(names).apply(names.allStems());
  }

  // Credit

  @Test
  public void creditKeepsWordsInLongerNames() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "customer");
    fixture.add(ObjectKind.COLUMN, table, "customer_id");

    assertThat(apply(CreditFilter::new, fixture.build()), containsInAnyOrder("customer"));
  }

  @Test
  public void creditDropsCodesUsedOnlyAsWholeNames() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "VBAK");
    fixture.add(ObjectKind.TABLE, "KNVV");
    fixture.add(ObjectKind.COLUMN, table, "VBELN");
    fixture.add(ObjectKind.COLUMN, table, "KUNNR");

    assertThat(apply(CreditFilter::new, fixture.build()), is(empty()));
  }

  @Test
  public void creditDropsWordFromLoneWordWithDigit() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "t1");
    fixture.add(ObjectKind.COLUMN, table, "address1");

    assertThat(apply(CreditFilter::new, fixture.build()), is(empty()));
  }

  @Test
  public void creditOnlyNarrowsTheGivenStems() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "t1");
    fixture.add(ObjectKind.COLUMN, table, "customer_order");

    final Names names = fixture.build();

    final Set<String> kept = new CreditFilter(names).apply(Set.of("customer"));

    assertThat(kept, containsInAnyOrder("customer"));
  }

  // Prefixes

  @Test
  public void prefixOfMostNamesIsDropped() {
    final NamesFixture fixture = new NamesFixture();
    for (int i = 0; i < 20; i++) {
      fixture.add(ObjectKind.ROUTINE, "proc" + i + "_task");
    }

    final Set<String> kept = apply(n -> new PrefixFilter(n, THRESHOLDS), fixture.build());

    assertThat(kept, containsInAnyOrder("task"));
  }

  @Test
  public void triggerPrefixIsDropped() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "t1");
    for (int i = 0; i < 20; i++) {
      fixture.add(ObjectKind.TRIGGER, table, "before_change" + i);
    }

    final Set<String> kept = apply(n -> new PrefixFilter(n, THRESHOLDS), fixture.build());

    assertThat(kept, not(hasItem("before")));
    assertThat(kept, hasItem("change"));
  }

  @Test
  public void prefixSharedByFewNamesIsKept() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.ROUTINE, "billing_report");
    fixture.add(ObjectKind.ROUTINE, "billing_run");
    for (int i = 0; i < 38; i++) {
      fixture.add(ObjectKind.ROUTINE, "work" + i + "_x");
    }

    final Set<String> kept = apply(n -> new PrefixFilter(n, THRESHOLDS), fixture.build());

    assertThat(kept, containsInAnyOrder("billing", "report"));
  }

  @Test
  public void kindsWithFewNamesAreNotChecked() {
    final NamesFixture fixture = new NamesFixture();
    for (int i = 0; i < 9; i++) {
      fixture.add(ObjectKind.ROUTINE, "proc" + i + "_task");
    }

    final Set<String> kept = apply(n -> new PrefixFilter(n, THRESHOLDS), fixture.build());

    assertThat(kept, containsInAnyOrder("proc", "task"));
  }

  @Test
  public void prefixShareBoundary() {
    // 3 of 10 names, which is exactly the default share of 0.3
    final NamesFixture fixture = new NamesFixture();
    for (int i = 0; i < 3; i++) {
      fixture.add(ObjectKind.ROUTINE, "shared" + i + "_alpha");
    }
    for (int i = 0; i < 7; i++) {
      fixture.add(ObjectKind.ROUTINE, "other" + i + "_beta");
    }
    final Names names = fixture.build();

    assertThat(apply(n -> new PrefixFilter(n, THRESHOLDS), names), not(hasItem("shared")));
    final VocabularyThresholds higherShare = new VocabularyThresholds(3, 0.31, 10, 0.25, 10, 0.5);
    assertThat(apply(n -> new PrefixFilter(n, higherShare), names), hasItem("shared"));
  }

  // Generic

  @Test
  public void genericWordNotInATableNameIsDropped() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.TABLE, "client");
    for (int i = 0; i < 9; i++) {
      final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "unit" + i);
      fixture.add(ObjectKind.COLUMN, table, "date_created");
    }

    final Set<String> kept = apply(n -> new GenericWordFilter(n, THRESHOLDS), fixture.build());

    assertThat(kept, not(hasItem("created")));
    assertThat(kept, not(hasItem("date")));
  }

  @Test
  public void wordInATableNameIsNeverGeneric() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.TABLE, "customer");
    for (int i = 0; i < 9; i++) {
      final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "unit" + i);
      fixture.add(ObjectKind.COLUMN, table, "id_customer");
    }

    final Set<String> kept = apply(n -> new GenericWordFilter(n, THRESHOLDS), fixture.build());

    assertThat(kept, hasItem("customer"));
  }

  @Test
  public void wordInARoutineNameIsNeverGeneric() {
    final NamesFixture fixture = new NamesFixture();
    fixture.add(ObjectKind.ROUTINE, "refresh_customer");
    for (int i = 0; i < 4; i++) {
      final NamedObjectKey table = fixture.add(ObjectKind.TABLE, "unit" + i);
      fixture.add(ObjectKind.COLUMN, table, "id_customer");
    }

    assertThat(
        apply(n -> new GenericWordFilter(n, THRESHOLDS), fixture.build()), hasItem("customer"));
  }

  @Test
  public void genericShareBoundary() {
    // The word is in the columns of 1 of 4 tables, which is exactly the default share of 0.25
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey first = fixture.add(ObjectKind.TABLE, "unit0");
    fixture.add(ObjectKind.COLUMN, first, "date_created");
    for (int i = 1; i < 4; i++) {
      fixture.add(ObjectKind.TABLE, "unit" + i);
    }
    final Names names = fixture.build();

    assertThat(apply(n -> new GenericWordFilter(n, THRESHOLDS), names), not(hasItem("created")));
    final VocabularyThresholds higherShare = new VocabularyThresholds(3, 0.3, 10, 0.26, 10, 0.5);
    assertThat(apply(n -> new GenericWordFilter(n, higherShare), names), hasItem("created"));
  }

  @Test
  public void manyColumnsInOneTableCountOnce() {
    // 10 columns in 1 of 10 tables is 10 percent of the tables, below the default share of 0.25
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey first = fixture.add(ObjectKind.TABLE, "unit0");
    for (int i = 0; i < 10; i++) {
      fixture.add(ObjectKind.COLUMN, first, "date_created" + i);
    }
    for (int i = 1; i < 10; i++) {
      fixture.add(ObjectKind.TABLE, "unit" + i);
    }

    assertThat(
        apply(n -> new GenericWordFilter(n, THRESHOLDS), fixture.build()), hasItem("created"));
  }

  @Test
  public void noTablesMeansNothingIsGeneric() {
    final NamesFixture fixture = new NamesFixture();
    final NamedObjectKey routine = fixture.add(ObjectKind.ROUTINE, "t1");
    fixture.add(ObjectKind.PARAMETER, routine, "date_created");

    assertThat(
        apply(n -> new GenericWordFilter(n, THRESHOLDS), fixture.build()),
        containsInAnyOrder("date", "created"));
  }
}
