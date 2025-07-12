/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.graph;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DirectedGraph<T extends Comparable<? super T>> {

  private final Set<DirectedEdge<T>> edges;
  private final String name;
  private final Map<T, Vertex<T>> verticesMap;

  public DirectedGraph(final String name) {
    this.name = name;
    verticesMap = new LinkedHashMap<>();
    edges = new LinkedHashSet<>();
  }

  /**
   * Adds vertices, and a directed edge between them. Simple directed graphs do not allow
   * self-loops.
   *
   * @see <a href="https://en.wikipedia.org/wiki/Loop_(graph_theory)">Loop (graph theory)</a>
   * @param from Vertex value at the start of the edge
   * @param to Vertex value at the end of the edge
   */
  public void addEdge(final T from, final T to) {
    if (!from.equals(to)) {
      edges.add(new DirectedEdge<>(addVertex(from), addVertex(to)));
    }
  }

  /**
   * Adds a vertex.
   *
   * @param value Vertex value
   * @return The newly added vertex
   */
  public Vertex<T> addVertex(final T value) {
    final Vertex<T> vertex;
    if (verticesMap.containsKey(value)) {
      vertex = verticesMap.get(value);
    } else {
      vertex = new Vertex<>(value);
      verticesMap.put(value, vertex);
    }
    return vertex;
  }

  public Set<DirectedEdge<T>> edgeSet() {
    return new LinkedHashSet<>(edges);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  public Set<DirectedEdge<T>> getOutgoingEdges(final Vertex<T> vertexFrom) {
    Objects.requireNonNull(vertexFrom, "No vertex provided");

    final Set<DirectedEdge<T>> outgoingEdges = new LinkedHashSet<>();
    for (final DirectedEdge<T> edge : edges) {
      if (edge.getFrom().equals(vertexFrom)) {
        outgoingEdges.add(edge);
      }
    }
    return outgoingEdges;
  }

  @Override
  public String toString() {
    final StringBuilder writer = new StringBuilder(4096);
    writer.append("digraph {\n");
    if (name != null && !name.isEmpty()) {
      writer.append(String.format("  [label=\"%s\"]\n", name));
    }
    for (final Vertex<T> vertex : verticesMap.values()) {
      writer.append("  ").append(vertex);
      if (vertex.hasAttribute("fillcolor")) {
        writer.append(
            String.format(" [fillcolor=%s, style=filled]", vertex.getAttribute("fillcolor")));
      }
      writer.append(";\n");
    }
    for (final DirectedEdge<T> edge : edges) {
      writer.append("  ").append(edge).append(";\n");
    }
    writer.append("}\n");
    return writer.toString();
  }

  public Set<Vertex<T>> vertexSet() {
    return new LinkedHashSet<>(verticesMap.values());
  }
}
