/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.importance.model.builder.TableImportanceInputs;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class TableImportanceInputsTest {

  @Test
  void getReturnsNullWhenNoValuesArePut() {
    final TableImportanceInputs inputs = new TableImportanceInputs();

    assertThat(inputs.get(node("ORPHAN")), is(nullValue()));
  }

  @Test
  void getReturnsNullWhenOnlyOneOfThreeValuesIsPut() {
    final TableImportanceInputs inputs = new TableImportanceInputs();
    final DatabaseObjectNodeId nodeId = node("ORDERS");

    inputs.put(nodeId, new TableTraits());

    assertThat(inputs.get(nodeId), is(nullValue()));
  }

  @Test
  void getReturnsNullWhenOnlyTwoOfThreeValuesArePut() {
    final TableImportanceInputs inputs = new TableImportanceInputs();
    final DatabaseObjectNodeId nodeId = node("ORDERS");

    inputs.put(nodeId, new TableTraits());
    inputs.put(nodeId, new TableCounts());

    assertThat(inputs.get(nodeId), is(nullValue()));
  }

  @Test
  void getReturnsPopulatedEntryOnceAllThreeValuesArePutRegardlessOfOrder() {
    final TableImportanceInputs inputs = new TableImportanceInputs();
    final DatabaseObjectNodeId nodeId = node("ORDERS");
    final TableImportanceMetrics metrics = new TableImportanceMetrics(1, 2, 3.0, 4, 5);
    final TableTraits traits = new TableTraits();
    final TableCounts counts = new TableCounts();

    // Put in a different order than the record's declared field order, to verify
    // get() assembly does not depend on put order.
    inputs.put(nodeId, metrics);
    inputs.put(nodeId, traits);
    inputs.put(nodeId, counts);

    final TableImportanceInputs.TableImportanceInput entry = inputs.get(nodeId);

    assertThat(entry, is(new TableImportanceInputs.TableImportanceInput(traits, counts, metrics)));
  }

  @Test
  void nodeIdsReflectsTheUnionAcrossAllPutCalls() {
    final TableImportanceInputs inputs = new TableImportanceInputs();
    final DatabaseObjectNodeId complete = node("ORDERS");
    final DatabaseObjectNodeId traitsOnly = node("CUSTOMERS");
    final DatabaseObjectNodeId countsOnly = node("PRODUCTS");

    inputs.put(complete, new TableTraits());
    inputs.put(complete, new TableCounts());
    inputs.put(complete, new TableImportanceMetrics(0, 0, 0.0, 0, 0));
    inputs.put(traitsOnly, new TableTraits());
    inputs.put(countsOnly, new TableCounts());

    assertThat(inputs.keySet(), containsInAnyOrder(complete, traitsOnly, countsOnly));
    assertThat(inputs.get(traitsOnly), is(nullValue()));
    assertThat(inputs.get(countsOnly), is(nullValue()));
  }

  @Test
  void puttingALaterValueForTheSameNodeAndTypeOverwritesTheEarlierOne() {
    final TableImportanceInputs inputs = new TableImportanceInputs();
    final DatabaseObjectNodeId nodeId = node("ORDERS");
    final TableImportanceMetrics firstMetrics = new TableImportanceMetrics(1, 1, 1.0, 1, 1);
    final TableImportanceMetrics secondMetrics = new TableImportanceMetrics(2, 2, 2.0, 2, 2);

    inputs.put(nodeId, new TableTraits());
    inputs.put(nodeId, new TableCounts());
    inputs.put(nodeId, firstMetrics);
    inputs.put(nodeId, secondMetrics);

    assertThat(inputs.get(nodeId).importanceMetrics(), is(secondMetrics));
    assertThat(inputs.keySet(), contains(nodeId));
  }

  private static DatabaseObjectNodeId node(final String name) {
    return new DatabaseObjectNodeId(
        new NamedObjectKey("PUBLIC", name), SimpleDatabaseObjectType.table);
  }
}
