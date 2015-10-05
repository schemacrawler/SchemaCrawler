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
import java.util.Objects;

public class SimpleCycleDetector<T extends Comparable<? super T>>
{

  enum TraversalState
  {

    notStarted("white"),
    inProgress("lightgray"),
    marked("red"),
    complete("gray");

    private final String color;

    private TraversalState(final String color)
    {
      this.color = color;
    }

    public String getColor()
    {
      return color;
    }

  }

  private final DirectedGraph<T> graph;

  public SimpleCycleDetector(final DirectedGraph<T> graph)
  {
    this.graph = Objects.requireNonNull(graph);
  }

  /**
   * Checks if the graph contains a cycle.
   *
   * @return true if the graph contains a cycle, false otherwise
   */
  public boolean containsCycle()
  {
    final Collection<Vertex<T>> vertices = clearTraversalStates();
    for (final Vertex<T> vertex: vertices)
    {
      if (vertex.getAttribute("traversalstate") == TraversalState.notStarted)
      {
        if (visitForCyles(vertex))
        {
          return true;
        }
      }
    }

    return false;
  }

  private Collection<Vertex<T>> clearTraversalStates()
  {
    final Collection<Vertex<T>> vertices = graph.vertexSet();
    for (final Vertex<T> vertex: vertices)
    {
      vertex.putAttribute("traversalstate", TraversalState.notStarted);
    }
    return vertices;
  }

  private boolean visitForCyles(final Vertex<T> vertex)
  {
    vertex.putAttribute("traversalstate", TraversalState.inProgress);

    for (final DirectedEdge<T> edge: graph.edgeSet())
    {
      if (edge.isFrom(vertex))
      {
        final Vertex<T> to = edge.getTo();
        if (to.getAttribute("traversalstate") == TraversalState.inProgress)
        {
          to.putAttribute("traversalstate", TraversalState.marked);
          return true;
        }
        else if (to.getAttribute("traversalstate") == TraversalState.notStarted)
        {
          if (visitForCyles(to))
          {
            return true;
          }
        }
      }
    }

    vertex.putAttribute("traversalstate", TraversalState.complete);

    return false;
  }

}
