/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.importance.model.VertexUtility;
import schemacrawler.schema.Table;
import schemacrawler.test.utility.crawl.LightPrimaryKey;
import schemacrawler.test.utility.crawl.LightTable;

class ImportanceScoreCalculatorTest {

  private static void put(
      final TableImportanceInputs inputs,
      final Table table,
      final TableImportanceMetrics metrics,
      final boolean hasPrimaryKey,
      final boolean hasIndexes) {
    doReturn(hasPrimaryKey).when(table).hasPrimaryKey();
    doReturn(hasIndexes).when(table).hasIndexes();
    inputs.putInputs(table, metrics);
  }

  private static Table table(final String name) {
    final LightTable table = new LightTable(name);
    table.setPrimaryKey(new LightPrimaryKey(table.addColumn("ID")));
    return spy(table);
  }

  @Test
  void connectedTableOutranksDisconnectedTable() {
    final Table connectedTable = table("AUTHORS");
    final DatabaseObjectVertexId connectedNode = VertexUtility.createVertexId(connectedTable);
    final Table disconnectedTable = table("BOOKAUTHORS");
    final DatabaseObjectVertexId disconnectedNode = VertexUtility.createVertexId(disconnectedTable);

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(inputs, connectedTable, new TableImportanceMetrics(1, 1, 1, 1, 1), false, false);
    put(inputs, disconnectedTable, new TableImportanceMetrics(0, 0, 0, 0, 0), false, false);

    final Map<DatabaseObjectVertexId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    assertThat(scores.get(connectedNode), greaterThan(scores.get(disconnectedNode)));
  }

  @Test
  void missingPrimaryKeyOrIndexesDampensWithoutZeroingOutTheScore() {
    final Table wellFormedTable = table("WELL_FORMED");
    final DatabaseObjectVertexId wellFormed = VertexUtility.createVertexId(wellFormedTable);
    final Table noPrimaryKeyOrIndexesTable = table("NO_PK_NO_INDEXES");
    final DatabaseObjectVertexId noPrimaryKeyOrIndexes =
        VertexUtility.createVertexId(noPrimaryKeyOrIndexesTable);

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(inputs, wellFormedTable, new TableImportanceMetrics(2, 2, 1, 2, 2), true, true);
    put(
        inputs,
        noPrimaryKeyOrIndexesTable,
        new TableImportanceMetrics(2, 2, 1, 2, 2),
        false,
        false);

    final Map<DatabaseObjectVertexId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    final int dampened = scores.get(noPrimaryKeyOrIndexes);
    final int undampened = scores.get(wellFormed);
    assertThat(dampened, lessThan(undampened));
    assertThat(dampened, greaterThan(0));
  }

  @Test
  void scoreIsAlwaysWithinZeroToOneHundred() {
    final Table table = table("MAXED_OUT");
    final DatabaseObjectVertexId maxed = VertexUtility.createVertexId(table);
    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(inputs, table, new TableImportanceMetrics(100, 100, 1000, 500, 500), true, true);

    final Map<DatabaseObjectVertexId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    assertThat(scores.get(maxed), greaterThanOrEqualTo(0));
    assertThat(scores.get(maxed), lessThanOrEqualTo(100));
  }

  @Test
  void scoreIsDeterministicAndReproducibleForTheSameInputs() {
    final Table table = table("ORDERS");
    final DatabaseObjectVertexId node = VertexUtility.createVertexId(table);
    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(inputs, table, new TableImportanceMetrics(3, 4, 2, 5, 6), true, true);

    final int firstRun = ImportanceScoreCalculator.calculate(inputs).get(node);
    final int secondRun = ImportanceScoreCalculator.calculate(inputs).get(node);

    assertThat(firstRun, is(equalTo(secondRun)));
  }

  @Test
  void scalesBetweennessCentralityLinearly() {
    final Table lowCentralityTable = table("LOW_CENTRALITY");
    final DatabaseObjectVertexId lowCentralityNode =
        VertexUtility.createVertexId(lowCentralityTable);
    final Table highCentralityTable = table("HIGH_CENTRALITY");
    final DatabaseObjectVertexId highCentralityNode =
        VertexUtility.createVertexId(highCentralityTable);

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(inputs, lowCentralityTable, new TableImportanceMetrics(0, 0, 10, 0, 0), true, true);
    put(inputs, highCentralityTable, new TableImportanceMetrics(0, 0, 50, 0, 0), true, true);

    final Map<DatabaseObjectVertexId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    assertThat(scores.get(lowCentralityNode), is(equalTo(21)));
    assertThat(scores.get(highCentralityNode), is(equalTo(45)));
  }

  @Test
  void wellConnectedTableOutranksPoorlyConnectedTable() {
    final Table smallTable = table("SMALL_LOOKUP");
    final DatabaseObjectVertexId smallNode = VertexUtility.createVertexId(smallTable);
    final Table connectedTable = table("BOOKAUTHORS");
    final DatabaseObjectVertexId connectedNode = VertexUtility.createVertexId(connectedTable);

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(inputs, smallTable, new TableImportanceMetrics(0, 0, 0, 0, 0), false, false);
    put(inputs, connectedTable, new TableImportanceMetrics(10, 10, 50, 20, 20), false, false);

    final Map<DatabaseObjectVertexId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    assertThat(scores.get(connectedNode), greaterThan(scores.get(smallNode)));
  }

  @Test
  void zeroGraphSignalsProduceAValidScore() {
    final Table table = table("ONLY_TABLE");
    final DatabaseObjectVertexId onlyTable = VertexUtility.createVertexId(table);

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(inputs, table, new TableImportanceMetrics(0, 0, 0, 0, 0), true, true);

    final Map<DatabaseObjectVertexId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    assertThat(scores.get(onlyTable), greaterThanOrEqualTo(0));
    assertThat(scores.get(onlyTable), lessThanOrEqualTo(100));
  }
}
