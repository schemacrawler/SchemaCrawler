/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AsSubgraph;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.EdgeType;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.SchemaGraphModel;

/** Finds directed shortest paths through table and view foreign-key relationships. */
public final class PathService {

  private final SchemaGraphModel schemaGraphModel;

  public PathService(final SchemaGraphModel schemaGraphModel) {
    this.schemaGraphModel =
        Objects.requireNonNull(schemaGraphModel, "No schema graph model provided");
  }

  public PathResult findShortestPath(
      final DatabaseObjectNodeId from, final DatabaseObjectNodeId to) {
    requireTableOrView(from, "source");
    requireTableOrView(to, "target");
    if (from.equals(to)) {
      return new PathResult(List.of(from), false);
    }

    final GraphPath<DatabaseObjectNodeId, SchemaEdge> foreignKeyPath =
        findPath(from, to, edge -> edge.getEdgeType() == EdgeType.FOREIGN_KEY);
    if (foreignKeyPath != null) {
      return new PathResult(foreignKeyPath.getVertexList(), false);
    }

    final GraphPath<DatabaseObjectNodeId, SchemaEdge> fallbackPath =
        findPath(
            from,
            to,
            edge ->
                edge.getEdgeType() == EdgeType.FOREIGN_KEY
                    || edge.getEdgeType() == EdgeType.IMPLICIT_ASSOCIATION);
    return fallbackPath == null
        ? new PathResult(List.of(), false)
        : new PathResult(fallbackPath.getVertexList(), true);
  }

  private GraphPath<DatabaseObjectNodeId, SchemaEdge> findPath(
      final DatabaseObjectNodeId from,
      final DatabaseObjectNodeId to,
      final Predicate<SchemaEdge> edgeFilter) {
    final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph = schemaGraphModel.getFullGraph();
    final Set<SchemaEdge> edges =
        fullGraph.edgeSet().stream().filter(edgeFilter).collect(Collectors.toSet());
    final Graph<DatabaseObjectNodeId, SchemaEdge> graph =
        new AsSubgraph<>(fullGraph, schemaGraphModel.getTableNodes(), edges);
    if (!graph.containsVertex(from) || !graph.containsVertex(to)) {
      return null;
    }
    return new DijkstraShortestPath<>(graph).getPath(from, to);
  }

  private void requireTableOrView(final DatabaseObjectNodeId nodeId, final String role) {
    Objects.requireNonNull(nodeId, "No %s node provided".formatted(role));
    if (!schemaGraphModel.getTableNodes().contains(nodeId)) {
      throw new IllegalArgumentException(
          "%s node must identify a table or view in the graph: %s".formatted(role, nodeId));
    }
  }
}
