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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;

public class CatalogNamesTest {

  private static List<String> stemsOfKind(final Names names, final ObjectKind kind) {
    return names.itemsOfKind(kind).stream()
        .flatMap(item -> item.stems().stream())
        .sorted()
        .toList();
  }

  @Test
  public void namesOfEveryKindAreCollected() {
    final Catalog catalog =
        new TestCatalogBuilder()
            .table("customer_order", "shipping_address")
            .triggers("audit_orders_trigger")
            .routine("refresh_totals", "billing_period")
            .sequence("invoice_number_seq")
            .synonym("shipment_alias")
            .build();

    final Names names = CatalogNames.read(catalog);

    assertThat(stemsOfKind(names, ObjectKind.TABLE), contains("customer", "order"));
    assertThat(stemsOfKind(names, ObjectKind.COLUMN), contains("address", "shipping"));
    assertThat(stemsOfKind(names, ObjectKind.TRIGGER), contains("audit", "order", "trigger"));
    assertThat(stemsOfKind(names, ObjectKind.ROUTINE), contains("refresh", "total"));
    assertThat(stemsOfKind(names, ObjectKind.PARAMETER), contains("billing", "period"));
    assertThat(stemsOfKind(names, ObjectKind.SEQUENCE), contains("invoice", "number"));
    assertThat(stemsOfKind(names, ObjectKind.SYNONYM), contains("alias", "shipment"));
    // "seq" is too short to be a word
    assertThat(names.items(), hasSize(7));
  }

  @Test
  public void columnsKnowTheirTable() {
    final Catalog catalog =
        new TestCatalogBuilder().table("first", "alpha_one").table("second", "beta_two").build();

    final Names names = CatalogNames.read(catalog);

    assertThat(names.tableCount(), is(2));
    final List<NamedItem> tables = names.itemsOfKind(ObjectKind.TABLE);
    final List<NamedItem> columns = names.itemsOfKind(ObjectKind.COLUMN);
    assertThat(columns.get(0).parentKey(), is(tables.get(0).key()));
    assertThat(columns.get(1).parentKey(), is(tables.get(1).key()));
    assertThat(columns.get(0).parentKey(), is(tables.get(0).key()));
  }

  @Test
  public void parametersKnowTheirRoutine() {
    final Catalog catalog =
        new TestCatalogBuilder().routine("refresh_totals", "billing_period").build();

    final Names names = CatalogNames.read(catalog);

    final NamedItem routine = names.itemsOfKind(ObjectKind.ROUTINE).get(0);
    final NamedItem parameter = names.itemsOfKind(ObjectKind.PARAMETER).get(0);
    assertThat(parameter.parentKey(), is(routine.key()));
    assertThat(parameter.parentKey(), is(routine.key()));
    // A top-level object is its own parent
    assertThat(routine.parentKey(), is(routine.key()));
  }

  @Test
  public void overloadedRoutinesAreDifferentObjects() {
    final Catalog catalog =
        new TestCatalogBuilder().routine("refresh_totals").routine("refresh_totals").build();

    final Names names = CatalogNames.read(catalog);

    final List<NamedItem> routines = names.itemsOfKind(ObjectKind.ROUTINE);
    assertThat(routines.get(0).key().equals(routines.get(1).key()), is(false));
  }

  @Test
  public void triggerNamesCountTowardThePrefixCheck() {
    final TestCatalogBuilder builder = new TestCatalogBuilder().table("audit_log");
    final String[] triggers = new String[12];
    for (int i = 0; i < triggers.length; i++) {
      triggers[i] = "before_change" + i;
    }
    builder.triggers(triggers);

    final Names names = CatalogNames.read(builder.build());

    final PrefixFilter filter = new PrefixFilter(names, VocabularyThresholds.DEFAULT);
    assertThat(filter.apply(names.allStems()).contains("before"), is(false));
    assertThat(filter.apply(names.allStems()).contains("change"), is(true));
  }
}
