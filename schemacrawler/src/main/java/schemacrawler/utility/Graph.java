/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.utility;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph<T extends Comparable<T>>
{

  /**
   * Traversal state when detecting cycle.
   */
  private enum TraversalState
  {
    notStarted,
    inProgress,
    complete;
  }

  /**
   * Directed edge in a graph.
   * 
   * @param <T>
   *        Type of node object
   */
  class DirectedEdge<T extends Comparable<T>>
  {

    private final Vertex<T> from;
    private final Vertex<T> to;
    private TraversalState traversalState = TraversalState.notStarted;

    DirectedEdge(final Vertex<T> from, final Vertex<T> to)
    {
      this.from = from;
      this.to = to;
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (!(obj instanceof DirectedEdge))
      {
        return false;
      }
      final DirectedEdge<T> other = (DirectedEdge<T>) obj;
      if (!getOuterType().equals(other.getOuterType()))
      {
        return false;
      }
      if (from == null)
      {
        if (other.from != null)
        {
          return false;
        }
      }
      else if (!from.equals(other.from))
      {
        return false;
      }
      if (to == null)
      {
        if (other.to != null)
        {
          return false;
        }
      }
      else if (!to.equals(other.to))
      {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + (from == null? 0: from.hashCode());
      result = prime * result + (to == null? 0: to.hashCode());
      return result;
    }

    @Override
    public String toString()
    {
      return "[" + from + " --> " + to + "]";
    }

    private Graph getOuterType()
    {
      return Graph.this;
    }

    Vertex<T> getFrom()
    {
      return from;
    }

    Vertex<T> getTo()
    {
      return to;
    }

    TraversalState getTraversalState()
    {
      return traversalState;
    }

    void setTraversalState(final TraversalState traversalState)
    {
      this.traversalState = traversalState;
    }

  }

  /**
   * Vertex in a graph.
   * 
   * @param <T>
   *        Type of node object
   */
  class Vertex<T extends Comparable<T>>
    implements Comparable<Vertex<T>>
  {

    private final T value;
    private TraversalState traversalState;

    Vertex(final T value)
    {
      this.value = value;
    }

    public int compareTo(final Vertex<T> otherVertex)
    {
      return value.compareTo(otherVertex.getValue());
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (!(obj instanceof Vertex))
      {
        return false;
      }
      final Vertex<T> other = (Vertex<T>) obj;
      if (!getOuterType().equals(other.getOuterType()))
      {
        return false;
      }
      if (value == null)
      {
        if (other.value != null)
        {
          return false;
        }
      }
      else if (!value.equals(other.value))
      {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + (value == null? 0: value.hashCode());
      return result;
    }

    @Override
    public String toString()
    {
      return "[" + value + " (" + traversalState + ")]";
    }

    private Graph getOuterType()
    {
      return Graph.this;
    }

    TraversalState getTraversalState()
    {
      return traversalState;
    }

    T getValue()
    {
      return value;
    }

    void setTraversalState(final TraversalState traversalState)
    {
      this.traversalState = traversalState;
    }

  }

  private final Map<T, Vertex<T>> verticesMap;
  private final Set<DirectedEdge<T>> edges;

  public Graph()
  {
    verticesMap = new HashMap<T, Vertex<T>>();
    edges = new HashSet<DirectedEdge<T>>();
  }

  /**
   * Adds vertices, and a directed edge between them.
   * 
   * @param from
   *        Vertex value at the start of the edge
   * @param to
   *        Vertex value at the end of the edge
   */
  public void addDirectedEdge(final T from, final T to)
  {
    edges.add(new DirectedEdge<T>(addVertex(from), addVertex(to)));
  }

  /**
   * Adds a vertex.
   * 
   * @param value
   *        Vertex value at the start of the edge
   */
  public Vertex<T> addVertex(final T value)
  {
    final Vertex<T> vertex;
    if (verticesMap.containsKey(value))
    {
      vertex = verticesMap.get(value);
    }
    else
    {
      vertex = new Vertex<T>(value);
      verticesMap.put(value, vertex);
    }
    return vertex;
  }

  /**
   * Returns true if the graph contains a cycle, false otherwise.
   */
  public boolean containsCycle()
  {
    final Collection<Vertex<T>> vertices = verticesMap.values();

    for (final Vertex<T> vertex: vertices)
    {
      vertex.setTraversalState(TraversalState.notStarted);
    }

    for (final Vertex<T> vertex: vertices)
    {
      if (vertex.getTraversalState() == TraversalState.notStarted)
      {
        if (visit(vertex))
        {
          return true;
        }
      }
    }

    return false;
  }

  private boolean visit(final Vertex<T> vertex)
  {
    vertex.setTraversalState(TraversalState.inProgress);

    for (final DirectedEdge<T> edge: edges)
    {
      final Vertex<T> from = edge.getFrom();
      final Vertex<T> to = edge.getTo();
      if (vertex.equals(from))
      {
        if (to.getTraversalState() == TraversalState.inProgress)
        {
          return true;
        }
        else if (to.getTraversalState() == TraversalState.notStarted)
        {
          if (visit(to))
          {
            return true;
          }
        }
      }
    }

    vertex.setTraversalState(TraversalState.complete);

    return false;
  }

}
