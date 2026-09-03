/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.util;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;

/**
 * Accumulates the three independently-computed, per-node inputs needed to build a {@code
 * TableImportance} record - graph topology metrics, table traits, and table counts - keyed by
 * {@link DatabaseObjectNodeId}.
 *
 * <p>Values for the three input types are typically put in separate passes (topology metrics from
 * {@link GraphMetricsCalculator}, traits/counts from {@code TableImportanceUtility}), and a node's
 * consolidated {@link TableImportanceInput} only becomes available once all three have been put for
 * that node. This centralizes the "all three inputs are present" check that would otherwise be
 * duplicated across every consumer of the three maps.
 */
public final class TableImportanceInputs {

  /** Consolidated per-node inputs, present only once all three values have been put. */
  public record TableImportanceInput(
      TableTraits tableTraits, TableCounts tableCounts, TableImportanceMetrics importanceMetrics) {}

  private final Map<DatabaseObjectNodeId, TableTraits> traits = new LinkedHashMap<>();
  private final Map<DatabaseObjectNodeId, TableCounts> counts = new LinkedHashMap<>();
  private final Map<DatabaseObjectNodeId, TableImportanceMetrics> graphMetrics =
      new LinkedHashMap<>();
  private final Set<DatabaseObjectNodeId> nodeIds = new LinkedHashSet<>();

  /**
   * Gets the consolidated inputs for a node.
   *
   * @param nodeId node to look up
   * @return an {@link TableImportanceInput} combining all three inputs, or {@code null} if any of
   *     the three has not been put for this node
   */
  public TableImportanceInput get(final DatabaseObjectNodeId nodeId) {
    final TableTraits nodeTraits = traits.get(nodeId);
    final TableCounts nodeCounts = counts.get(nodeId);
    final TableImportanceMetrics nodeGraphMetrics = graphMetrics.get(nodeId);
    if (nodeTraits == null || nodeCounts == null || nodeGraphMetrics == null) {
      return null;
    }
    return new TableImportanceInput(nodeTraits, nodeCounts, nodeGraphMetrics);
  }

  /**
   * Gets every node id that has had at least one of the three inputs put, whether or not all three
   * are present.
   *
   * @return the union of node ids across all put calls
   */
  public Set<DatabaseObjectNodeId> keySet() {
    return Set.copyOf(nodeIds);
  }

  public void put(final DatabaseObjectNodeId nodeId, final TableCounts value) {
    requireNonNull(nodeId, "No database node provided");
    requireNonNull(value, "No value provided");
    counts.put(nodeId, value);
    nodeIds.add(nodeId);
  }

  public void put(final DatabaseObjectNodeId nodeId, final TableImportanceMetrics value) {
    requireNonNull(nodeId, "No database node provided");
    requireNonNull(value, "No value provided");
    graphMetrics.put(nodeId, value);
    nodeIds.add(nodeId);
  }

  public void put(final DatabaseObjectNodeId nodeId, final TableTraits value) {
    requireNonNull(nodeId, "No database node provided");
    requireNonNull(value, "No value provided");
    traits.put(nodeId, value);
    nodeIds.add(nodeId);
  }
}
