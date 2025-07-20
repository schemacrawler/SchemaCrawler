/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
