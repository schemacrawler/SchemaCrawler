/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.graph;

import static java.util.Comparator.naturalOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SimpleTopologicalSort<T extends Comparable<? super T>> {

  private final DirectedGraph<T> graph;

  public SimpleTopologicalSort(final DirectedGraph<T> graph) {
    this.graph = Objects.requireNonNull(graph, "No diagram provided");
  }

  public List<T> topologicalSort() throws GraphException {
    if (containsCycle()) {
      throw new GraphException("Graph contains a cycle, so cannot be topologically sorted");
    }

    final Collection<Vertex<T>> vertices = graph.vertexSet();
    final int collectionSize = vertices.size();

    final Collection<DirectedEdge<T>> edges = new ArrayList<>(graph.edgeSet());
    final List<T> sortedValues = new ArrayList<>(collectionSize);

    while (!vertices.isEmpty()) {

      final List<T> nodesAtLevel = new ArrayList<>(collectionSize);

      // Remove unattached nodes
      for (final Iterator<Vertex<T>> iterator = vertices.iterator(); iterator.hasNext(); ) {
        final Vertex<T> vertex = iterator.next();
        if (isUnattachedNode(vertex, edges)) {
          nodesAtLevel.add(vertex.getValue());
          iterator.remove();
        }
      }

      // Find all nodes at the current level
      final List<Vertex<T>> startNodes = new ArrayList<>(collectionSize);
      for (final Vertex<T> vertex : vertices) {
        if (isStartNode(vertex, edges)) {
          startNodes.add(vertex);
        }
      }

      for (final Vertex<T> vertex : startNodes) {
        // Save the vertex value
        nodesAtLevel.add(vertex.getValue());
        // Remove all out edges
        dropOutEdges(vertex, edges);
        // Remove the vertex itself
        vertices.remove(vertex);
      }

      nodesAtLevel.sort(naturalOrder());
      sortedValues.addAll(nodesAtLevel);
    }

    return sortedValues;
  }

  private boolean containsCycle() {
    final SimpleCycleDetector<T> cycleDetector = new SimpleCycleDetector<>(graph);
    return cycleDetector.containsCycle();
  }

  private void dropOutEdges(final Vertex<T> vertex, final Collection<DirectedEdge<T>> edges) {
    for (final Iterator<DirectedEdge<T>> iterator = edges.iterator(); iterator.hasNext(); ) {
      final DirectedEdge<T> edge = iterator.next();
      if (edge.isFrom(vertex)) {
        iterator.remove();
      }
    }
  }

  private boolean isStartNode(final Vertex<T> vertex, final Collection<DirectedEdge<T>> edges) {
    for (final DirectedEdge<T> edge : edges) {
      if (edge.isTo(vertex)) {
        return false;
      }
    }
    return true;
  }

  private boolean isUnattachedNode(
      final Vertex<T> vertex, final Collection<DirectedEdge<T>> edges) {
    for (final DirectedEdge<T> edge : edges) {
      if (edge.isTo(vertex) || edge.isFrom(vertex)) {
        return false;
      }
    }
    return true;
  }
}
