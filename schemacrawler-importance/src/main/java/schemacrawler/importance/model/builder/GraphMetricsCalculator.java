/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.builder;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.graph.AsUndirectedGraph;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.TableImportanceMetrics;
import us.fatehi.utility.UtilityMarker;

/**
 * Calculates topology metrics from the complete schema graph.
 *
 * <p>Degree and reachability retain dependency direction. Betweenness treats every typed dependency
 * as an undirected structural connection so bridge tables with only outgoing foreign keys are
 * scored.
 */
@UtilityMarker
final class GraphMetricsCalculator {

  static Map<DatabaseObjectNodeId, TableImportanceMetrics> calculate(
      final Graph<DatabaseObjectNodeId, SchemaEdge> graph) {
    final BetweennessCentrality<DatabaseObjectNodeId, SchemaEdge> centrality =
        new BetweennessCentrality<>(new AsUndirectedGraph<>(graph));
    final Map<DatabaseObjectNodeId, TableImportanceMetrics> metrics = new LinkedHashMap<>();
    for (final DatabaseObjectNodeId nodeId : graph.vertexSet()) {
      metrics.put(
          nodeId,
          new TableImportanceMetrics(
              graph.inDegreeOf(nodeId),
              graph.outDegreeOf(nodeId),
              centrality.getVertexScore(nodeId),
              reachableCount(graph, nodeId, false),
              reachableCount(graph, nodeId, true)));
    }
    return Map.copyOf(metrics);
  }

  private static int reachableCount(
      final Graph<DatabaseObjectNodeId, SchemaEdge> graph,
      final DatabaseObjectNodeId start,
      final boolean reverse) {
    final Set<DatabaseObjectNodeId> visited = new LinkedHashSet<>();
    final ArrayDeque<DatabaseObjectNodeId> pending = new ArrayDeque<>();
    visited.add(start);
    pending.add(start);
    while (!pending.isEmpty()) {
      final DatabaseObjectNodeId nodeId = pending.removeFirst();
      final Set<SchemaEdge> edges =
          reverse ? graph.incomingEdgesOf(nodeId) : graph.outgoingEdgesOf(nodeId);
      for (final SchemaEdge edge : edges) {
        final DatabaseObjectNodeId adjacent =
            reverse ? graph.getEdgeSource(edge) : graph.getEdgeTarget(edge);
        if (visited.add(adjacent)) {
          pending.addLast(adjacent);
        }
      }
    }
    return visited.size() - 1;
  }

  private GraphMetricsCalculator() {
    // Prevent instantiation
  }
}
