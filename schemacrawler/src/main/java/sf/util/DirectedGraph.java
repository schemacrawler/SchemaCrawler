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

package sf.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DirectedGraph<T extends Comparable>
{

  /**
   * Directed edge in a graph.
   * 
   * @param <T>
   *        Type of node object
   */
  class DirectedEdge<T extends Comparable>
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
      return "(" + from + " --> " + to + ")";
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

    boolean isFrom(final Vertex<T> vertex)
    {
      return vertex != null && vertex.equals(from);
    }

    boolean isTo(final Vertex<T> vertex)
    {
      return vertex != null && vertex.equals(to);
    }

    void setTraversalState(final TraversalState traversalState)
    {
      this.traversalState = traversalState;
    }

    private DirectedGraph<?> getOuterType()
    {
      return DirectedGraph.this;
    }

  }

  /**
   * Vertex in a graph.
   * 
   * @param <T>
   *        Type of node object
   */
  class Vertex<T extends Comparable>
    implements Comparable<Vertex<T>>
  {

    private final T value;
    private TraversalState traversalState = TraversalState.notStarted;

    Vertex(final T value)
    {
      this.value = value;
    }

    public int compareTo(final Vertex<T> otherVertex)
    {
      if (value == null)
      {
        return -1;
      }
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
      return value.toString();
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

    private DirectedGraph getOuterType()
    {
      return DirectedGraph.this;
    }

  }

  /**
   * Traversal state when detecting cycle.
   */
  private enum TraversalState
  {
    notStarted,
    inProgress,
    complete;
  }

  private final Map<T, Vertex<T>> verticesMap;
  private final Set<DirectedEdge<T>> edges;

  public DirectedGraph()
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

  public List<T> topologicalSort()
    throws GraphException
  {
    if (containsCycle())
    {
      throw new GraphException("Graph contains a cycle, so cannot be topologically sorted");
    }

    final int collectionSize = verticesMap.size();

    final Collection<Vertex<T>> vertices = new HashSet<Vertex<T>>(verticesMap
      .values());
    final HashSet<DirectedEdge<T>> edges = new HashSet<DirectedEdge<T>>(this.edges);
    final List<T> sortedValues = new ArrayList<T>(collectionSize);

    while (!vertices.isEmpty())
    {
      final List<Vertex<T>> startNodes = new ArrayList<Vertex<T>>(collectionSize);

      final List<T> unattachedNodeValues = new ArrayList<T>(collectionSize);
      for (final Iterator<Vertex<T>> iterator = vertices.iterator(); iterator
        .hasNext();)
      {
        final Vertex<T> vertex = iterator.next();
        if (isUnattachedNode(vertex, edges))
        {
          unattachedNodeValues.add(vertex.getValue());
          iterator.remove();
        }
      }
      Collections.sort(unattachedNodeValues);
      sortedValues.addAll(unattachedNodeValues);

      for (final Vertex<T> vertex: vertices)
      {
        if (isStartNode(vertex, edges))
        {
          startNodes.add(vertex);
        }
      }
      Collections.sort(startNodes);

      for (final Vertex<T> vertex: startNodes)
      {
        // Save the vertex value
        sortedValues.add(vertex.getValue());
        // Remove all out edges
        dropOutEdges(vertex, edges);
        // Remove the vertex itself
        vertices.remove(vertex);
      }
    }

    return sortedValues;
  }

  private void dropOutEdges(final Vertex<T> vertex,
                            final Set<DirectedEdge<T>> edges)
  {
    for (final Iterator<DirectedEdge<T>> iterator = edges.iterator(); iterator
      .hasNext();)
    {
      final DirectedEdge<T> edge = iterator.next();
      if (edge.isFrom(vertex))
      {
        iterator.remove();
      }
    }
  }

  private boolean isStartNode(final Vertex<T> vertex,
                              final Set<DirectedEdge<T>> edges)
  {
    for (final DirectedEdge<T> edge: edges)
    {
      if (edge.isTo(vertex))
      {
        return false;
      }
    }
    return true;
  }

  private boolean isUnattachedNode(final Vertex<T> vertex,
                                   final Set<DirectedEdge<T>> edges)
  {
    for (final DirectedEdge<T> edge: edges)
    {
      if (edge.isTo(vertex) || edge.isFrom(vertex))
      {
        return false;
      }
    }
    return true;
  }

  private boolean visit(final Vertex<T> vertex)
  {
    vertex.setTraversalState(TraversalState.inProgress);

    for (final DirectedEdge<T> edge: edges)
    {
      final Vertex<T> to = edge.getTo();
      if (edge.isFrom(vertex))
      {
        if (to.getTraversalState() == TraversalState.inProgress)
        {
          return true;
        }
        else if (to.getTraversalState() == TraversalState.notStarted)
        {
          if (visit(edge.getTo()))
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
