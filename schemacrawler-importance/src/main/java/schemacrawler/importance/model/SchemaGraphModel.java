/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnmodifiableGraph;
import schemacrawler.schema.DatabaseObject;

/** Immutable graph and object lookup data built from a SchemaCrawler catalog. */
public final class SchemaGraphModel {

  private final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph;
  private final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject;
  private final Set<DatabaseObjectNodeId> tableViewNodes;

  public SchemaGraphModel(
      final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph,
      final Set<DatabaseObjectNodeId> tableViewNodes,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject) {
    this.fullGraph =
        new AsUnmodifiableGraph<>(Objects.requireNonNull(fullGraph, "No full graph provided"));
    this.tableViewNodes = Set.copyOf(tableViewNodes);
    this.nodeToObject = Map.copyOf(nodeToObject);
  }

  public Graph<DatabaseObjectNodeId, SchemaEdge> getFullGraph() {
    return fullGraph;
  }

  public DatabaseObject getObjectByNodeId(final DatabaseObjectNodeId nodeId) {
    return nodeToObject.get(nodeId);
  }

  public Set<DatabaseObjectNodeId> getTableNodes() {
    return tableViewNodes;
  }
}
