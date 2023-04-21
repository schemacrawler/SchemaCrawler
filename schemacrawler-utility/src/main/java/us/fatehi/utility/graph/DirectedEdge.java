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

/** Directed edge in a diagram. */
public final class DirectedEdge<T> {

  private final Vertex<T> from;
  private final Vertex<T> to;

  DirectedEdge(final Vertex<T> from, final Vertex<T> to) {
    this.from = from;
    this.to = to;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DirectedEdge<T> other = (DirectedEdge<T>) obj;
    if (from == null) {
      if (other.from != null) {
        return false;
      }
    } else if (!from.equals(other.from)) {
      return false;
    }
    if (to == null) {
      return other.to == null;
    } else {
      return to.equals(other.to);
    }
  }

  public Vertex<T> getFrom() {
    return from;
  }

  public Vertex<T> getTo() {
    return to;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (from == null ? 0 : from.hashCode());
    result = prime * result + (to == null ? 0 : to.hashCode());
    return result;
  }

  public boolean isFrom(final Vertex<T> vertex) {
    return vertex != null && vertex.equals(from);
  }

  public boolean isTo(final Vertex<T> vertex) {
    return vertex != null && vertex.equals(to);
  }

  @Override
  public String toString() {
    return from + " -> " + to;
  }
}
