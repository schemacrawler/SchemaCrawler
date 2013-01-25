/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

public class DirectedGraph<T extends Comparable<? super T>>
{

  /**
   * Directed edge in a graph.
   */
  public class DirectedEdge
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

    public Vertex getFrom()
    {
      return from;
    }

    public Vertex getTo()
    {
      return to;
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

    public boolean isFrom(final Vertex vertex)
    {
      return vertex != null && vertex.equals(from);
    }

    public boolean isTo(final Vertex vertex)
    {
      return vertex != null && vertex.equals(to);
    }

    @Override
    public String toString()
    {
      return "(" + from + " --> " + to + ")";
    }

  }

  /**
   * Vertex in a graph.
   */
  public class Vertex
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

    public T getValue()
    {
      return value;
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

    void setTraversalState(final TraversalState traversalState)
    {
      this.traversalState = traversalState;
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
    final Collection<Vertex> vertices = clearTraversalStates();
    for (final Vertex vertex: vertices)
    {
      if (vertex.getTraversalState() == TraversalState.notStarted)
      {
        if (visitForCyles(vertex))
        {
          return true;
        }
      }
    }

    return false;
  }

  public DirectedGraph<T> subGraph(final T value)
  {
    return subGraph(value, -1);
  }

  public DirectedGraph<T> subGraph(final T value, final int depth)
  {
    final Collection<Vertex> vertices = clearTraversalStates();
    visitForSubGraph(verticesMap.get(value), depth);

    final Set<Vertex> subGraphVertices = new HashSet<Vertex>();
    for (final Vertex currentVertex: vertices)
    {
      if (currentVertex.getTraversalState() == TraversalState.complete)
      {
        subGraphVertices.add(currentVertex);
      }
    }

    final DirectedGraph<T> subGraph = new DirectedGraph<T>();
    for (final DirectedEdge edge: edges)
    {
      final Vertex from = edge.getFrom();
      final Vertex to = edge.getTo();
      if (subGraphVertices.contains(from) && subGraphVertices.contains(to))
      {
        subGraph.addDirectedEdge(from.getValue(), to.getValue());
      }
    }
    // In case this is an isolated node
    subGraph.addVertex(value);

    return subGraph;
  }

  public List<T> topologicalSort()
    throws GraphException
  {
    if (containsCycle())
    {
      throw new GraphException("Graph contains a cycle, so cannot be topologically sorted");
    }

    final int collectionSize = verticesMap.size();

    final Collection<Vertex> vertices = new ArrayList<Vertex>(verticesMap.values());
    final Collection<DirectedEdge> edges = new ArrayList<DirectedEdge>(this.edges);
    final List<T> sortedValues = new ArrayList<T>(collectionSize);

    while (!vertices.isEmpty())
    {

      final List<T> nodesAtLevel = new ArrayList<T>(collectionSize);

      // Remove unattached nodes
      for (final Iterator<Vertex> iterator = vertices.iterator(); iterator
        .hasNext();)
      {
        final Vertex vertex = iterator.next();
        if (isUnattachedNode(vertex, edges))
        {
          nodesAtLevel.add(vertex.getValue());
          iterator.remove();
        }
      }

      // Find all nodes at the current level
      final List<Vertex> startNodes = new ArrayList<Vertex>(collectionSize);
      for (final Vertex vertex: vertices)
      {
        if (isStartNode(vertex, edges))
        {
          startNodes.add(vertex);
        }
      }

      for (final Vertex vertex: startNodes)
      {
        // Save the vertex value
        nodesAtLevel.add(vertex.getValue());
        // Remove all out edges
        dropOutEdges(vertex, edges);
        // Remove the vertex itself
        vertices.remove(vertex);
      }

      Collections.sort(nodesAtLevel);
      sortedValues.addAll(nodesAtLevel);
    }

    return sortedValues;
  }

  private Collection<Vertex> clearTraversalStates()
  {
    final Collection<Vertex> vertices = verticesMap.values();
    for (final Vertex vertex: vertices)
    {
      vertex.setTraversalState(TraversalState.notStarted);
    }
    return vertices;
  }

  private void dropOutEdges(final Vertex vertex,
                            final Collection<DirectedEdge> edges)
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

  private boolean isStartNode(final Vertex vertex,
                              final Collection<DirectedEdge> edges)
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
                                   final Collection<DirectedEdge> edges)
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

  private boolean visitForCyles(final Vertex vertex)
  {
    vertex.setTraversalState(TraversalState.inProgress);

    for (final DirectedEdge edge: edges)
    {
      if (edge.isFrom(vertex))
      {
        final Vertex to = edge.getTo();
        if (to.getTraversalState() == TraversalState.inProgress)
        {
          return true;
        }
        else if (to.getTraversalState() == TraversalState.notStarted)
        {
          if (visitForCyles(to))
          {
            return true;
          }
        }
      }
    }

    vertex.setTraversalState(TraversalState.complete);

    return false;
  }

  private void visitForSubGraph(final Vertex vertex, final int depth)
  {
    vertex.setTraversalState(TraversalState.complete);
    if (depth == 0)
    {
      return;
    }
    for (final DirectedEdge edge: edges)
    {
      if (edge.isFrom(vertex))
      {
        visitForSubGraph(edge.getTo(), depth - 1);
      }
    }

  }

}
