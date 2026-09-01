/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.cache;

import java.util.Objects;
import org.jgrapht.graph.DefaultEdge;
import schemacrawler.schema.NamedObjectKey;

/**
 * A typed schema dependency edge.
 *
 * <p>This class deliberately retains reference equality from {@link DefaultEdge}, allowing multiple
 * dependencies with identical metadata to connect the same vertices.
 */
public final class SchemaEdge extends DefaultEdge {

  private static final long serialVersionUID = 1L;

  private final EdgeType edgeType;
  private final NamedObjectKey referenceKey;

  public SchemaEdge(final EdgeType edgeType, final NamedObjectKey referenceKey) {
    this.edgeType = Objects.requireNonNull(edgeType, "No edge type provided");
    this.referenceKey = referenceKey;
  }

  public EdgeType getEdgeType() {
    return edgeType;
  }

  public NamedObjectKey getReferenceKey() {
    return referenceKey;
  }
}
