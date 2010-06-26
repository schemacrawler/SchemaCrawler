/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DirectedGraph<T extends Comparable<? super T>>
{

  /**
   * Directed edge in a graph.
   */
  private class DirectedEdge
  {

    private final Vertex from;
    private final Vertex to;

    DirectedEdge(final Vertex from, final Vertex to)
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
      final DirectedEdge other = (DirectedEdge) obj;
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
      result = prime * result + (from == null? 0: from.hashCode());
      result = prime * result + (to == null? 0: to.hashCode());
      return result;
    }

    @Override
    public String toString()
    {
      return "(" + from + " --> " + to + ")";
    }

    Vertex getTo()
    {
      return to;
    }

    boolean isFrom(final Vertex vertex)
    {
      return vertex != null && vertex.equals(from);
    }

    boolean isTo(final Vertex vertex)
    {
      return vertex != null && vertex.equals(to);
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

  /**
   * Vertex in a graph.
   * 
   * @param <T>
   *        Type of node object
   */
  private class Vertex
  {

    private final T value;
    private TraversalState traversalState = TraversalState.notStarted;

    Vertex(final T value)
    {
      this.value = value;
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
      final Vertex other = (Vertex) obj;
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

  }

  private final Map<T, Vertex> verticesMap;
  private final Set<DirectedEdge> edges;

  public DirectedGraph()
  {
    verticesMap = new HashMap<T, Vertex>();
    edges = new HashSet<DirectedEdge>();
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
    edges.add(new DirectedEdge(addVertex(from), addVertex(to)));
  }

  /**
   * Adds a vertex.
   * 
   * @param value
   *        Vertex value
   * @return The newly added vertex
   */
  public Vertex addVertex(final T value)
  {
    final Vertex vertex;
    if (verticesMap.containsKey(value))
    {
      vertex = verticesMap.get(value);
    }
    else
    {
      vertex = new Vertex(value);
      verticesMap.put(value, vertex);
    }
    return vertex;
  }

  /**
   * Checks if the graph contains a cycle.
   * 
   * @return true if the graph contains a cycle, false otherwise
   */
  public boolean containsCycle()
  {
    final Collection<Vertex> vertices = verticesMap.values();

    for (final Vertex vertex: vertices)
    {
      vertex.setTraversalState(TraversalState.notStarted);
    }

    for (final Vertex vertex: vertices)
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

    final Collection<Vertex> vertices = new HashSet<Vertex>(verticesMap
      .values());
    final Set<DirectedEdge> edges = new HashSet<DirectedEdge>(this.edges);
    final List<T> sortedValues = new ArrayList<T>(collectionSize);

    while (!vertices.isEmpty())
    {
      final List<Vertex> startNodes = new ArrayList<Vertex>(collectionSize);

      final List<T> unattachedNodeValues = new ArrayList<T>(collectionSize);
      for (final Iterator<Vertex> iterator = vertices.iterator(); iterator
        .hasNext();)
      {
        final Vertex vertex = iterator.next();
        if (isUnattachedNode(vertex, edges))
        {
          unattachedNodeValues.add(vertex.getValue());
          iterator.remove();
        }
      }
      Collections.sort(unattachedNodeValues);
      sortedValues.addAll(unattachedNodeValues);

      for (final Vertex vertex: vertices)
      {
        if (isStartNode(vertex, edges))
        {
          startNodes.add(vertex);
        }
      }
      Collections.sort(startNodes, new Comparator<Vertex>()
      {

        public int compare(final Vertex vertex1, final Vertex vertex2)
        {
          if (vertex1 == null)
          {
            return 1;
          }
          else if (vertex2 == null)
          {
            return -1;
          }
          else
          {
            return vertex1.getValue().compareTo(vertex2.getValue());
          }
        }
      });

      for (final Vertex vertex: startNodes)
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

  private void dropOutEdges(final Vertex vertex, final Set<DirectedEdge> edges)
  {
    for (final Iterator<DirectedEdge> iterator = edges.iterator(); iterator
      .hasNext();)
    {
      final DirectedEdge edge = iterator.next();
      if (edge.isFrom(vertex))
      {
        iterator.remove();
      }
    }
  }

  private boolean isStartNode(final Vertex vertex, final Set<DirectedEdge> edges)
  {
    for (final DirectedEdge edge: edges)
    {
      if (edge.isTo(vertex))
      {
        return false;
      }
    }
    return true;
  }

  private boolean isUnattachedNode(final Vertex vertex,
                                   final Set<DirectedEdge> edges)
  {
    for (final DirectedEdge edge: edges)
    {
      if (edge.isTo(vertex) || edge.isFrom(vertex))
      {
        return false;
      }
    }
    return true;
  }

  private boolean visit(final Vertex vertex)
  {
    vertex.setTraversalState(TraversalState.inProgress);

    for (final DirectedEdge edge: edges)
    {
      final Vertex to = edge.getTo();
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
