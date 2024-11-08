/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of <a href=
 * "https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm"> Tarjan's
 * algorithm</a>
 *
 * @param <T> Any comparable class
 */
public class TarjanStronglyConnectedComponentFinder<T extends Comparable<? super T>> {

  private static final String ATTRIBUTE_INDEX = "index";
  private static final String ATTRIBUTE_LOWLINK = "lowlink";
  private final DirectedGraph<T> graph;
  private final Deque<Vertex<T>> stack;
  private final Collection<List<T>> stronglyConnectedComponents;

  public TarjanStronglyConnectedComponentFinder(final DirectedGraph<T> graph) {
    this.graph = Objects.requireNonNull(graph, "No diagram provided");

    stronglyConnectedComponents = new HashSet<>();
    stack = new ArrayDeque<>();
  }

  /**
   * Calculates the sets of strongly connected vertices.
   *
   * @return Set of strongly connected components (sets of vertices)
   */
  public Collection<List<T>> detectCycles() {
    for (final Vertex<T> vertex : graph.vertexSet()) {
      if (!vertex.hasAttribute(ATTRIBUTE_INDEX)) {
        strongConnect(vertex, 0);
      }
    }
    return stronglyConnectedComponents;
  }

  private void strongConnect(final Vertex<T> vertexFrom, final int index) {
    vertexFrom.putAttribute(ATTRIBUTE_INDEX, index);
    vertexFrom.putAttribute(ATTRIBUTE_LOWLINK, index);
    stack.push(vertexFrom);

    for (final DirectedEdge<T> edge : graph.getOutgoingEdges(vertexFrom)) {
      final Vertex<T> vertexTo = edge.getTo();
      if (!vertexTo.hasAttribute(ATTRIBUTE_INDEX)) {
        // Successor vertex has not yet been visited; recurse on it
        strongConnect(vertexTo, index + 1);
        vertexFrom.putAttribute(
            ATTRIBUTE_LOWLINK,
            Math.min(
                vertexFrom.getAttribute(ATTRIBUTE_LOWLINK),
                vertexTo.getAttribute(ATTRIBUTE_LOWLINK)));
      } else if (stack.contains(vertexTo)) {
        // Successor vertex is on stack, hence in the current SCC
        vertexFrom.putAttribute(
            ATTRIBUTE_LOWLINK,
            Math.min(
                vertexFrom.getAttribute(ATTRIBUTE_LOWLINK),
                vertexTo.getAttribute(ATTRIBUTE_INDEX)));
      }
    }

    if (vertexFrom.getAttribute(ATTRIBUTE_LOWLINK) == vertexFrom.getAttribute(ATTRIBUTE_INDEX)) {
      final LinkedList<T> scc = new LinkedList<>();
      Vertex<T> sccVertex;
      do {
        sccVertex = stack.pop();
        scc.addFirst(sccVertex.getValue());
      } while (!vertexFrom.equals(sccVertex));
      if (scc.size() > 1) {
        stronglyConnectedComponents.add(scc);
      }
    }
  }
}
