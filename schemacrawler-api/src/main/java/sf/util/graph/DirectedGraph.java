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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sf.util.GraphException;

public class DirectedGraph<T extends Comparable<? super T>>
{

  private final Map<T, Vertex<T>> verticesMap;
  private final Set<DirectedEdge<T>> edges;

  public DirectedGraph()
  {
    verticesMap = new HashMap<>();
    edges = new HashSet<>();
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
    if (!from.equals(to))
    {
      edges.add(new DirectedEdge<T>(addVertex(from), addVertex(to)));
    }
  }

  /**
   * Adds a vertex.
   *
   * @param value
   *        Vertex value
   * @return The newly added vertex
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
   * Checks if the graph contains a cycle.
   *
   * @return true if the graph contains a cycle, false otherwise
   */
  public boolean containsCycle()
  {
    final Collection<Vertex<T>> vertices = clearTraversalStates();
    for (final Vertex<T> vertex: vertices)
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
    final Collection<Vertex<T>> vertices = clearTraversalStates();
    visitForSubGraph(verticesMap.get(value), depth);

    final Set<Vertex<T>> subGraphVertices = new HashSet<>();
    for (final Vertex<T> currentVertex: vertices)
    {
      if (currentVertex.getTraversalState() == TraversalState.complete)
      {
        subGraphVertices.add(currentVertex);
      }
    }

    final DirectedGraph<T> subGraph = new DirectedGraph<>();
    for (final DirectedEdge<T> edge: edges)
    {
      final Vertex<T> from = edge.getFrom();
      final Vertex<T> to = edge.getTo();
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

    final Collection<Vertex<T>> vertices = new ArrayList<>(verticesMap.values());
    final Collection<DirectedEdge<T>> edges = new ArrayList<>(this.edges);
    final List<T> sortedValues = new ArrayList<>(collectionSize);

    while (!vertices.isEmpty())
    {

      final List<T> nodesAtLevel = new ArrayList<>(collectionSize);

      // Remove unattached nodes
      for (final Iterator<Vertex<T>> iterator = vertices.iterator(); iterator
        .hasNext();)
      {
        final Vertex<T> vertex = iterator.next();
        if (isUnattachedNode(vertex, edges))
        {
          nodesAtLevel.add(vertex.getValue());
          iterator.remove();
        }
      }

      // Find all nodes at the current level
      final List<Vertex<T>> startNodes = new ArrayList<>(collectionSize);
      for (final Vertex<T> vertex: vertices)
      {
        if (isStartNode(vertex, edges))
        {
          startNodes.add(vertex);
        }
      }

      for (final Vertex<T> vertex: startNodes)
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

  private Collection<Vertex<T>> clearTraversalStates()
  {
    final Collection<Vertex<T>> vertices = verticesMap.values();
    for (final Vertex<T> vertex: vertices)
    {
      vertex.setTraversalState(TraversalState.notStarted);
    }
    return vertices;
  }

  private void dropOutEdges(final Vertex<T> vertex,
                            final Collection<DirectedEdge<T>> edges)
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
                              final Collection<DirectedEdge<T>> edges)
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
                                   final Collection<DirectedEdge<T>> edges)
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

  private boolean visitForCyles(final Vertex<T> vertex)
  {
    vertex.setTraversalState(TraversalState.inProgress);

    for (final DirectedEdge<T> edge: edges)
    {
      if (edge.isFrom(vertex))
      {
        final Vertex<T> to = edge.getTo();
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

  private void visitForSubGraph(final Vertex<T> vertex, final int depth)
  {
    vertex.setTraversalState(TraversalState.complete);
    if (depth == 0)
    {
      return;
    }
    for (final DirectedEdge<T> edge: edges)
    {
      if (edge.isFrom(vertex))
      {
        visitForSubGraph(edge.getTo(), depth - 1);
      }
    }

  }

}
