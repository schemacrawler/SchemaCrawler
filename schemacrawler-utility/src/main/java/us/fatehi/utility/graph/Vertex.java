/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.utility.graph;

import java.util.HashMap;
import java.util.Map;

/** Vertex in a graph. */
public final class Vertex<T> {

  private final Map<String, Object> attributes;
  private final T value;

  Vertex(final T value) {
    this.value = value;
    attributes = new HashMap<>();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Vertex)) {
      return false;
    }
    final Vertex<?> other = (Vertex<?>) obj;
    if (value == null) {
      return other.value == null;
    } else {
      return value.equals(other.value);
    }
  }

  public T getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (value == null ? 0 : value.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  <V> V getAttribute(final String key) {
    return (V) attributes.get(key);
  }

  boolean hasAttribute(final String key) {
    return attributes.containsKey(key);
  }

  void putAttribute(final String key, final Object value) {
    attributes.put(key, value);
  }
}
