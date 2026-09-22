/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.implementation;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.graph.AsUndirectedGraph;
import schemacrawler.importance.model.DatabaseObjectVertexId;
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

  static Map<DatabaseObjectVertexId, TableImportanceMetrics> calculate(
      final Graph<DatabaseObjectVertexId, SchemaEdge> graph) {
    final BetweennessCentrality<DatabaseObjectVertexId, SchemaEdge> centrality =
        new BetweennessCentrality<>(new AsUndirectedGraph<>(graph), true);
    final Map<DatabaseObjectVertexId, TableImportanceMetrics> metrics = new LinkedHashMap<>();
    for (final DatabaseObjectVertexId vertexId : graph.vertexSet()) {
      final int vertexCentralityPercentageScore;
      final Double vertexCentralityScore = centrality.getVertexScore(vertexId);
      if (vertexCentralityScore == null) {
        vertexCentralityPercentageScore = 0;
      } else {
        vertexCentralityPercentageScore = (int) Math.round(vertexCentralityScore * 100D);
      }
      metrics.put(
          vertexId,
          new TableImportanceMetrics(
              graph.inDegreeOf(vertexId),
              graph.outDegreeOf(vertexId),
              vertexCentralityPercentageScore,
              reachableCount(graph, vertexId, false),
              reachableCount(graph, vertexId, true)));
    }
    return Map.copyOf(metrics);
  }

  private static int reachableCount(
      final Graph<DatabaseObjectVertexId, SchemaEdge> graph,
      final DatabaseObjectVertexId start,
      final boolean reverse) {
    final Set<DatabaseObjectVertexId> visited = new LinkedHashSet<>();
    final ArrayDeque<DatabaseObjectVertexId> pending = new ArrayDeque<>();
    visited.add(start);
    pending.add(start);
    while (!pending.isEmpty()) {
      final DatabaseObjectVertexId vertexId = pending.removeFirst();
      final Set<SchemaEdge> edges =
          reverse ? graph.incomingEdgesOf(vertexId) : graph.outgoingEdgesOf(vertexId);
      for (final SchemaEdge edge : edges) {
        final DatabaseObjectVertexId adjacent =
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
