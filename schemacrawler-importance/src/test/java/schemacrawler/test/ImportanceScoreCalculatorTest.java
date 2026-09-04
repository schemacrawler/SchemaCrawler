/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.importance.model.builder.ImportanceScoreCalculator;
import schemacrawler.importance.model.builder.TableImportanceInputs;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.tools.utility.EntityModelType;
import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class ImportanceScoreCalculatorTest {

  @Test
  void strongEntityOutranksPoorlyConnectedBridgeTableAllElseEqual() {
    final DatabaseObjectNodeId strongEntity = node("AUTHORS");
    final DatabaseObjectNodeId bridgeTable = node("BOOKAUTHORS");

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(
        inputs,
        strongEntity,
        new TableImportanceMetrics(1, 1, 0.1, 1, 1),
        traits(EntityModelType.strong_entity, false, false),
        counts(5, 2, 10L));
    put(
        inputs,
        bridgeTable,
        new TableImportanceMetrics(1, 1, 0.1, 1, 1),
        traits(EntityModelType.bridge_table, false, false),
        counts(5, 2, 10L));

    final Map<DatabaseObjectNodeId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    assertThat(scores.get(strongEntity), greaterThan(scores.get(bridgeTable)));
  }

  @Test
  void wellConnectedBridgeTableOutranksPoorlyConnectedStrongEntity() {
    final DatabaseObjectNodeId strongEntity = node("SMALL_LOOKUP");
    final DatabaseObjectNodeId bridgeTable = node("BOOKAUTHORS");

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(
        inputs,
        strongEntity,
        new TableImportanceMetrics(0, 0, 0.0, 0, 0),
        traits(EntityModelType.strong_entity, false, false),
        counts(2, 0, 5L));
    put(
        inputs,
        bridgeTable,
        new TableImportanceMetrics(10, 10, 50.0, 20, 20),
        traits(EntityModelType.bridge_table, false, false),
        counts(2, 2, 1000L));

    final Map<DatabaseObjectNodeId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    assertThat(scores.get(bridgeTable), greaterThan(scores.get(strongEntity)));
  }

  @Test
  void missingPrimaryKeyOrIndexesDampensWithoutZeroingOutTheScore() {
    final DatabaseObjectNodeId wellFormed = node("WELL_FORMED");
    final DatabaseObjectNodeId noPrimaryKeyOrIndexes = node("NO_PK_NO_INDEXES");

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(
        inputs,
        wellFormed,
        new TableImportanceMetrics(2, 2, 1.0, 2, 2),
        traits(EntityModelType.strong_entity, false, false),
        counts(5, 2, 100L));
    put(
        inputs,
        noPrimaryKeyOrIndexes,
        new TableImportanceMetrics(2, 2, 1.0, 2, 2),
        traits(EntityModelType.strong_entity, true, true),
        counts(5, 2, 100L));

    final Map<DatabaseObjectNodeId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    final int dampened = scores.get(noPrimaryKeyOrIndexes);
    final int undampened = scores.get(wellFormed);
    assertThat(dampened, lessThan(undampened));
    assertThat(dampened, greaterThan(0));
  }

  @Test
  void everySignalNormalizesToZeroWhenItsCatalogMaximumIsZero() {
    final DatabaseObjectNodeId onlyTable = node("ONLY_TABLE");

    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(
        inputs,
        onlyTable,
        new TableImportanceMetrics(0, 0, 0.0, 0, 0),
        traits(EntityModelType.non_entity, false, false),
        counts(0, 0, 0L));

    final Map<DatabaseObjectNodeId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    // Only the fixed entity-role weight term (0.15 * 0.30 = 4.5) contributes; every
    // normalized/count-based term is 0 because every catalog-wide maximum is 0. Rounds up to 5.
    assertThat(scores.get(onlyTable), is(5));
  }

  @Test
  void scoreIsDeterministicAndReproducibleForTheSameInputs() {
    final DatabaseObjectNodeId table = node("ORDERS");
    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(
        inputs,
        table,
        new TableImportanceMetrics(3, 4, 2.5, 5, 6),
        traits(EntityModelType.weak_entity, false, false),
        counts(6, 3, 200L));

    final int firstRun = ImportanceScoreCalculator.calculate(inputs).get(table);
    final int secondRun = ImportanceScoreCalculator.calculate(inputs).get(table);

    assertThat(firstRun, is(equalTo(secondRun)));
  }

  @Test
  void scoreIsAlwaysWithinZeroToOneHundred() {
    final DatabaseObjectNodeId maxed = node("MAXED_OUT");
    final TableImportanceInputs inputs = new TableImportanceInputs();
    put(
        inputs,
        maxed,
        new TableImportanceMetrics(100, 100, 1000.0, 500, 500),
        traits(EntityModelType.strong_entity, false, false),
        counts(500, 200, 1_000_000L));

    final Map<DatabaseObjectNodeId, Integer> scores = ImportanceScoreCalculator.calculate(inputs);

    assertThat(scores.get(maxed), greaterThanOrEqualTo(0));
    assertThat(scores.get(maxed), lessThanOrEqualTo(100));
  }

  private static void put(
      final TableImportanceInputs inputs,
      final DatabaseObjectNodeId nodeId,
      final TableImportanceMetrics metrics,
      final TableTraits traits,
      final TableCounts counts) {
    inputs.put(nodeId, metrics);
    inputs.put(nodeId, traits);
    inputs.put(nodeId, counts);
  }

  private static TableTraits traits(
      final EntityModelType entityModelType, final boolean noPrimaryKey, final boolean noIndexes) {
    return new TableTraits(noPrimaryKey, null, noIndexes, null, null, null, entityModelType);
  }

  private static TableCounts counts(
      final int attributeColumnCount, final int foreignKeyCount, final long rowCount) {
    return new TableCounts(attributeColumnCount, null, foreignKeyCount, null, null, rowCount);
  }

  private static DatabaseObjectNodeId node(final String name) {
    return new DatabaseObjectNodeId(
        new NamedObjectKey("PUBLIC", name), SimpleDatabaseObjectType.table);
  }
}
