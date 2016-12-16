/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
