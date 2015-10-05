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


import java.util.HashMap;
import java.util.Map;

/**
 * Vertex in a graph.
 */
public final class Vertex<T>
{

  private final T value;
  private final Map<String, Object> attributes;

  Vertex(final T value)
  {
    this.value = value;
    attributes = new HashMap<>();
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
    final Vertex<?> other = (Vertex<?>) obj;
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

  public <V> V getAttribute(final String key)
  {
    return (V) attributes.get(key);
  }

  public T getValue()
  {
    return value;
  }

  public boolean hasAttribute(final String key)
  {
    return attributes.containsKey(key);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (value == null? 0: value.hashCode());
    return result;
  }

  public void putAttribute(final String key, final Object value)
  {
    attributes.put(key, value);
  }

  @Override
  public String toString()
  {
    return value.toString();
  }

}
