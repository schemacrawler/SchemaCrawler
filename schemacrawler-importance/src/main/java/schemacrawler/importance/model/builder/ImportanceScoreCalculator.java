/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.tools.utility.EntityModelType;
import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;
import us.fatehi.utility.UtilityMarker;

/**
 * Calculates a composite, catalog-relative importance score for each table/view from both
 * structural topology metrics and table metadata.
 *
 * <p>The score blends structural signals (betweenness centrality, impact reachability, total
 * degree) with data-modeling signals (entity role, attribute column count, row count, foreign key
 * count, trigger count, self-referencing), then applies multiplicative dampening for tables that
 * have no primary key or no indexes. Every raw count signal is normalized against the catalog-wide
 * maximum for that signal using a log-dampened scale, so a single outlier table does not compress
 * every other table's score into a narrow band. Weights:
 *
 * <pre>
 * structural (50%):
 *   0.30 betweennessCentrality
 *   0.15 impactReachabilityCount
 *   0.05 inDegree + outDegree
 *
 * data-modeling (50%):
 *   0.15 entityRoleWeight
 *   0.13 attributeColumnCount
 *   0.09 rowCount
 *   0.06 foreignKeyCount
 *   0.05 triggerCount
 *   0.02 selfReferencing
 *
 * dampening (multiplicative, applied last):
 *   x 0.85 if noPrimaryKey
 *   x 0.90 if noIndexes
 * </pre>
 *
 * <p>The resulting score is a whole number, always in {@code [0, 100]}, and is stable and
 * reproducible for the same catalog inputs, but is only meaningful relative to other tables in the
 * same catalog.
 */
@UtilityMarker
final class ImportanceScoreCalculator {

  /**
   * Catalog-wide maximum structural signals, analogous to {@link TableImportanceMetrics} but
   * holding the maximum (not per-node) value of each normalized structural signal.
   */
  private record MaxGraphMetrics(
      double betweennessCentrality, double impactReachabilityCount, double totalDegree) {}

  /**
   * Catalog-wide maximum data-modeling count signals, analogous to {@link TableCounts} but holding
   * the maximum (not per-node) value of each normalized count signal.
   */
  private record MaxTableCounts(
      double attributeColumnCount, double rowCount, double foreignKeyCount, double triggerCount) {}

  /** The two catalog-wide maxima needed to normalize every node's raw signals. */
  private record MaxValues(MaxGraphMetrics graphMetrics, MaxTableCounts tableCounts) {}

  private static final double WEIGHT_BETWEENNESS = 0.30;
  private static final double WEIGHT_IMPACT_REACHABILITY = 0.15;
  private static final double WEIGHT_TOTAL_DEGREE = 0.05;
  private static final double WEIGHT_ENTITY_ROLE = 0.15;
  private static final double WEIGHT_ATTRIBUTE_COLUMN_COUNT = 0.13;
  private static final double WEIGHT_ROW_COUNT = 0.09;
  private static final double WEIGHT_FOREIGN_KEY_COUNT = 0.06;
  private static final double WEIGHT_TRIGGER_COUNT = 0.05;
  private static final double WEIGHT_SELF_REFERENCING = 0.02;

  private static final double DAMPENING_NO_PRIMARY_KEY = 1.00 - 0.15;
  private static final double DAMPENING_NO_INDEXES = 1.00 - 0.10;

  static Map<DatabaseObjectNodeId, Integer> calculate(final TableImportanceInputs inputs) {
    final MaxValues maxValues = calculateMaxValues(inputs);
    return calculateScores(inputs, maxValues);
  }

  private static MaxValues calculateMaxValues(final TableImportanceInputs inputs) {

    double maxBetweenness = 0;
    double maxImpactReachability = 0;
    double maxTotalDegree = 0;
    double maxAttributeColumnCount = 0;
    double maxRowCount = 0;
    double maxForeignKeyCount = 0;
    double maxTriggerCount = 0;

    for (final DatabaseObjectNodeId nodeId : inputs.keySet()) {
      final TableImportanceInputs.TableImportanceInput entry = inputs.get(nodeId);
      if (entry == null) {
        continue;
      }
      final TableImportanceMetrics nodeMetrics = entry.importanceMetrics();
      final TableCounts nodeCounts = entry.tableCounts();

      maxBetweenness = Math.max(maxBetweenness, nodeMetrics.betweennessCentrality());
      maxImpactReachability =
          Math.max(maxImpactReachability, nodeMetrics.impactReachabilityCount());
      maxTotalDegree = Math.max(maxTotalDegree, nodeMetrics.inDegree() + nodeMetrics.outDegree());
      maxAttributeColumnCount =
          Math.max(
              maxAttributeColumnCount,
              nonNegativeInt(nodeCounts, TableCounts::attributeColumnCount));
      maxRowCount = Math.max(maxRowCount, nonNegativeLong(nodeCounts, TableCounts::rowCount));
      maxForeignKeyCount =
          Math.max(maxForeignKeyCount, nonNegativeInt(nodeCounts, TableCounts::foreignKeyCount));
      maxTriggerCount =
          Math.max(maxTriggerCount, nonNegativeInt(nodeCounts, TableCounts::triggerCount));
    }

    return new MaxValues(
        new MaxGraphMetrics(maxBetweenness, maxImpactReachability, maxTotalDegree),
        new MaxTableCounts(
            maxAttributeColumnCount, maxRowCount, maxForeignKeyCount, maxTriggerCount));
  }

  private static Map<DatabaseObjectNodeId, Integer> calculateScores(
      final TableImportanceInputs inputs, final MaxValues maxValues) {

    final MaxGraphMetrics maxGraphMetrics = maxValues.graphMetrics();
    final MaxTableCounts maxTableCounts = maxValues.tableCounts();

    final Map<DatabaseObjectNodeId, Integer> scores = new LinkedHashMap<>();
    for (final DatabaseObjectNodeId nodeId : inputs.keySet()) {
      final TableImportanceInputs.TableImportanceInput entry = inputs.get(nodeId);
      if (entry == null) {
        continue;
      }
      final TableImportanceMetrics nodeMetrics = entry.importanceMetrics();
      final TableTraits nodeTraits = entry.tableTraits();
      final TableCounts nodeCounts = entry.tableCounts();

      final double totalDegree = nodeMetrics.inDegree() + nodeMetrics.outDegree();
      final boolean selfReferencing =
          nodeTraits != null && Boolean.TRUE.equals(nodeTraits.selfReferencing());

      final double rawScore =
          100
              * (WEIGHT_BETWEENNESS
                      * norm(
                          nodeMetrics.betweennessCentrality(),
                          maxGraphMetrics.betweennessCentrality())
                  + WEIGHT_IMPACT_REACHABILITY
                      * norm(
                          nodeMetrics.impactReachabilityCount(),
                          maxGraphMetrics.impactReachabilityCount())
                  + WEIGHT_TOTAL_DEGREE * norm(totalDegree, maxGraphMetrics.totalDegree())
                  + WEIGHT_ENTITY_ROLE * entityRoleWeight(nodeTraits)
                  + WEIGHT_ATTRIBUTE_COLUMN_COUNT
                      * norm(
                          nonNegativeInt(nodeCounts, TableCounts::attributeColumnCount),
                          maxTableCounts.attributeColumnCount())
                  + WEIGHT_ROW_COUNT
                      * norm(
                          nonNegativeLong(nodeCounts, TableCounts::rowCount),
                          maxTableCounts.rowCount())
                  + WEIGHT_FOREIGN_KEY_COUNT
                      * norm(
                          nonNegativeInt(nodeCounts, TableCounts::foreignKeyCount),
                          maxTableCounts.foreignKeyCount())
                  + WEIGHT_TRIGGER_COUNT
                      * norm(
                          nonNegativeInt(nodeCounts, TableCounts::triggerCount),
                          maxTableCounts.triggerCount())
                  + WEIGHT_SELF_REFERENCING * (selfReferencing ? 1.0 : 0.0));

      final boolean noPrimaryKey =
          nodeTraits != null && Boolean.TRUE.equals(nodeTraits.noPrimaryKey());
      final boolean noIndexes = nodeTraits != null && Boolean.TRUE.equals(nodeTraits.noIndexes());

      final double importanceScore =
          rawScore
              * (noPrimaryKey ? DAMPENING_NO_PRIMARY_KEY : 1.0)
              * (noIndexes ? DAMPENING_NO_INDEXES : 1.0);

      scores.put(nodeId, (int) Math.max(0, Math.min(100, Math.round(importanceScore))));
    }
    return Map.copyOf(scores);
  }

  private static double entityRoleWeight(final TableTraits traits) {
    final EntityModelType entityModelType = traits == null ? null : traits.entityModelType();
    if (entityModelType == null) {
      return 0.40;
    }
    return switch (entityModelType) {
      case strong_entity -> 1.00;
      case weak_entity -> 0.85;
      case subtype -> 0.70;
      case bridge_table -> 0.55;
      case non_entity -> 0.30;
      case unknown -> 0.10;
    };
  }

  private static double nonNegativeInt(
      final TableCounts counts, final Function<TableCounts, Integer> accessor) {
    if (counts == null) {
      return 0;
    }
    final Integer value = accessor.apply(counts);
    return value == null ? 0 : value;
  }

  private static double nonNegativeLong(
      final TableCounts counts, final Function<TableCounts, Long> accessor) {
    if (counts == null) {
      return 0;
    }
    final Long value = accessor.apply(counts);
    return value == null ? 0 : value;
  }

  private static double norm(final double x, final double max) {
    if (max <= 0) {
      return 0;
    }
    return Math.log1p(x) / Math.log1p(max);
  }

  private ImportanceScoreCalculator() {
    // Prevent instantiation
  }
}
