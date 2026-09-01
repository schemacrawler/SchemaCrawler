/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.cache;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnmodifiableGraph;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObjectKey;

/** Immutable graph and object lookup data built from a SchemaCrawler catalog. */
public final class SchemaGraphCache {

  private final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph;
  private final Graph<DatabaseObjectNodeId, SchemaEdge> metricsGraph;
  private final Map<NamedObjectKey, DatabaseObject> keyToObject;
  private final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject;
  private final Set<DatabaseObjectNodeId> tableViewNodes;

  public SchemaGraphCache(
      final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph,
      final Graph<DatabaseObjectNodeId, SchemaEdge> metricsGraph,
      final Set<DatabaseObjectNodeId> tableViewNodes,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject,
      final Map<NamedObjectKey, DatabaseObject> keyToObject) {
    this.fullGraph =
        new AsUnmodifiableGraph<>(Objects.requireNonNull(fullGraph, "No full graph provided"));
    this.metricsGraph =
        new AsUnmodifiableGraph<>(
            Objects.requireNonNull(metricsGraph, "No metrics graph provided"));
    this.tableViewNodes = Set.copyOf(tableViewNodes);
    this.nodeToObject = Map.copyOf(nodeToObject);
    this.keyToObject = Map.copyOf(keyToObject);
  }

  public Graph<DatabaseObjectNodeId, SchemaEdge> getFullGraph() {
    return fullGraph;
  }

  public Graph<DatabaseObjectNodeId, SchemaEdge> getMetricsGraph() {
    return metricsGraph;
  }

  public DatabaseObject getObjectByKey(final NamedObjectKey key) {
    return keyToObject.get(key);
  }

  public DatabaseObject getObjectByNodeId(final DatabaseObjectNodeId nodeId) {
    return nodeToObject.get(nodeId);
  }

  public Set<DatabaseObjectNodeId> getTableViewNodes() {
    return tableViewNodes;
  }
}
