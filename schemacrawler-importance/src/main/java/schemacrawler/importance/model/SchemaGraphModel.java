/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnmodifiableGraph;
import schemacrawler.schema.DatabaseObject;

/** Immutable graph and object lookup data built from a SchemaCrawler catalog. */
public final class SchemaGraphModel {

  private final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph;
  private final List<SchemaCommunity> communities;
  private final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject;
  private final Set<DatabaseObjectNodeId> tableNodes;

  public SchemaGraphModel(
      final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph,
      final Set<DatabaseObjectNodeId> tableViewNodes,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject,
      final List<SchemaCommunity> communities) {
    this.fullGraph =
        new AsUnmodifiableGraph<>(Objects.requireNonNull(fullGraph, "No full graph provided"));
    this.tableNodes = Collections.unmodifiableSet(new LinkedHashSet<>(tableViewNodes));
    this.nodeToObject = Map.copyOf(nodeToObject);
    this.communities = List.copyOf(communities);
  }

  /** Gets the immutable, ordered communities calculated when this model was built. */
  public List<SchemaCommunity> getCommunities() {
    return communities;
  }

  public Graph<DatabaseObjectNodeId, SchemaEdge> getFullGraph() {
    return fullGraph;
  }

  public DatabaseObject getObjectByNodeId(final DatabaseObjectNodeId nodeId) {
    return nodeToObject.get(nodeId);
  }

  public Set<DatabaseObjectNodeId> getTableNodes() {
    return tableNodes;
  }
}
