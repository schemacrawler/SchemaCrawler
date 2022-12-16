/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package us.fatehi.utility.graph;

import java.util.Collection;
import java.util.Objects;

public class SimpleCycleDetector<T extends Comparable<? super T>> {

  enum TraversalState {
    notStarted("white"),
    inProgress("lightgray"),
    marked("red"),
    complete("gray");

    private final String color;

    TraversalState(final String color) {
      this.color = color;
    }

    public String getColor() {
      return color;
    }
  }

  private static final String ATTRIBUTE_TRAVERSAL_STATE = "traversalstate";

  private final DirectedGraph<T> graph;

  public SimpleCycleDetector(final DirectedGraph<T> graph) {
    this.graph = Objects.requireNonNull(graph, "No diagram provided");
  }

  /**
   * Checks if the diagram contains a cycle.
   *
   * @return true if the diagram contains a cycle, false otherwise
   */
  public boolean containsCycle() {
    final Collection<Vertex<T>> vertices = clearTraversalStates();
    for (final Vertex<T> vertex : vertices) {
      if (vertex.getAttribute(ATTRIBUTE_TRAVERSAL_STATE) == TraversalState.notStarted) {
        if (visitForCyles(vertex)) {
          return true;
        }
      }
    }

    return false;
  }

  private Collection<Vertex<T>> clearTraversalStates() {
    final Collection<Vertex<T>> vertices = graph.vertexSet();
    for (final Vertex<T> vertex : vertices) {
      vertex.putAttribute(ATTRIBUTE_TRAVERSAL_STATE, TraversalState.notStarted);
    }
    return vertices;
  }

  private boolean visitForCyles(final Vertex<T> vertex) {
    vertex.putAttribute(ATTRIBUTE_TRAVERSAL_STATE, TraversalState.inProgress);

    for (final DirectedEdge<T> edge : graph.edgeSet()) {
      if (edge.isFrom(vertex)) {
        final Vertex<T> to = edge.getTo();
        if (to.getAttribute(ATTRIBUTE_TRAVERSAL_STATE) == TraversalState.inProgress) {
          to.putAttribute(ATTRIBUTE_TRAVERSAL_STATE, TraversalState.marked);
          return true;
        } else if (to.getAttribute(ATTRIBUTE_TRAVERSAL_STATE) == TraversalState.notStarted) {
          if (visitForCyles(to)) {
            return true;
          }
        }
      }
    }

    vertex.putAttribute(ATTRIBUTE_TRAVERSAL_STATE, TraversalState.complete);

    return false;
  }
}
