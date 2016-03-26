/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


/**
 * Directed edge in a graph.
 */
public final class DirectedEdge<T>
{

  private final Vertex<T> from;
  private final Vertex<T> to;

  DirectedEdge(final Vertex<T> from, final Vertex<T> to)
  {
    this.from = from;
    this.to = to;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
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
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final DirectedEdge<T> other = (DirectedEdge<T>) obj;
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

  public Vertex<T> getFrom()
  {
    return from;
  }

  public Vertex<T> getTo()
  {
    return to;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (from == null? 0: from.hashCode());
    result = prime * result + (to == null? 0: to.hashCode());
    return result;
  }

  public boolean isFrom(final Vertex<T> vertex)
  {
    return vertex != null && vertex.equals(from);
  }

  public boolean isTo(final Vertex<T> vertex)
  {
    return vertex != null && vertex.equals(to);
  }

  @Override
  public String toString()
  {
    return from + " -> " + to;
  }

}
