/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.importance.model.VertexUtility;
import schemacrawler.schema.Table;
import schemacrawler.test.utility.crawl.LightTable;

class TableImportanceInputsTest {

  @Test
  void getReturnsNullForAnUnknownNode() {
    final TableImportanceInputs inputs = new TableImportanceInputs();

    assertThat(
        inputs.store(VertexUtility.createVertexId(new LightTable("ORPHAN")), 1), is(nullValue()));
  }

  @Test
  void getReturnsTheCompleteInputForAPopulatedNode() {
    final TableImportanceInputs inputs = new TableImportanceInputs();
    final Table table = new LightTable("ORDERS");
    final DatabaseObjectVertexId vertexId = VertexUtility.createVertexId(table);
    final TableImportanceMetrics metrics = new TableImportanceMetrics(1, 2, 3, 4, 5);

    inputs.putInputs(table, metrics);

    final var importance = inputs.store(vertexId, 1);

    assertThat(importance.importanceMetrics(), is(metrics));
    assertThat(table.getAttribute(TableImportance.class.getName()), is(importance));
  }

  @Test
  void puttingACompleteInputForTheSameNodeOverwritesTheEarlierInput() {
    final TableImportanceInputs inputs = new TableImportanceInputs();
    final Table table = new LightTable("ORDERS");
    final DatabaseObjectVertexId vertexId = VertexUtility.createVertexId(table);
    final TableImportanceMetrics firstMetrics = new TableImportanceMetrics(1, 1, 1, 1, 1);
    final TableImportanceMetrics secondMetrics = new TableImportanceMetrics(2, 2, 2, 2, 2);

    inputs.putInputs(table, firstMetrics);
    inputs.putInputs(table, secondMetrics);

    assertThat(inputs.store(vertexId, 1).importanceMetrics(), is(secondMetrics));
  }
}
