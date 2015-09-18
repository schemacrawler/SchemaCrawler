/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package sf.util.graph;


import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * Implementation of <a href=
 * "https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm">
 * Tarjan's algorithm</a>
 * 
 * @author Sualeh Fatehi
 * @param <T>
 *        Any comparable class
 */
public class TarjanStronglyConnectedComponentFinder<T extends Comparable<? super T>>
{

  private final DirectedGraph<T> graph;
  private final Collection<List<T>> stronglyConnectedComponents;
  private final Stack<Vertex<T>> stack;

  public TarjanStronglyConnectedComponentFinder(final DirectedGraph<T> graph)
  {
    this.graph = Objects.requireNonNull(graph);

    stronglyConnectedComponents = new HashSet<>();
    stack = new Stack<Vertex<T>>();
  }

  /**
   * Calculates the sets of strongly connected vertices.
   *
   * @param graph
   *        Graph to detect cycles within.
   * @return Set of strongly connected components (sets of vertices)
   */
  public Collection<List<T>> detectCycles()
  {
    for (final Vertex<T> vertex: graph.vertexSet())
    {
      if (!vertex.hasAttribute("index"))
      {
        strongConnect(vertex, 0);
      }
    }
    return stronglyConnectedComponents;
  }

  private void strongConnect(final Vertex<T> vertexFrom, final int index)
  {
    vertexFrom.putAttribute("index", index);
    vertexFrom.putAttribute("lowlink", index);
    stack.push(vertexFrom);

    for (final DirectedEdge<T> edge: graph.getOutgoingEdges(vertexFrom))
    {
      final Vertex<T> vertexTo = edge.getTo();
      if (!vertexTo.hasAttribute("index"))
      {
        // Successor vertex has not yet been visited; recurse on it
        strongConnect(vertexTo, index + 1);
        vertexFrom.putAttribute("lowlink",
                                Math.min(vertexFrom.getAttribute("lowlink"),
                                         vertexTo.getAttribute("lowlink")));
      }
      else if (stack.contains(vertexTo))
      {
        // Successor vertex is on stack, hence in the current SCC
        vertexFrom.putAttribute("lowlink",
                                Math.min(vertexFrom.getAttribute("lowlink"),
                                         vertexTo.getAttribute("index")));
      }
    }

    if (vertexFrom.getAttribute("lowlink") == vertexFrom.getAttribute("index"))
    {
      final LinkedList<T> scc = new LinkedList<>();
      Vertex<T> sccVertex;
      do
      {
        sccVertex = stack.pop();
        scc.addFirst(sccVertex.getValue());
      } while (!vertexFrom.equals(sccVertex));
      if (scc.size() > 1)
      {
        stronglyConnectedComponents.add(scc);
      }
    }

  }

}
