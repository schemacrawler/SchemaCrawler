/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DirectedPseudograph;
import schemacrawler.importance.cache.DatabaseObjectNodeId;
import schemacrawler.importance.cache.EdgeType;
import schemacrawler.importance.cache.SchemaEdge;
import schemacrawler.importance.cache.SchemaGraphCache;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

/** Builds the immutable dependency graph foundation from a SchemaCrawler catalog. */
public final class SchemaGraphCacheBuilder {

  private Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph;
  private Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject;
  private Map<NamedObjectKey, DatabaseObject> keyToObject;
  private Graph<DatabaseObjectNodeId, SchemaEdge> metricsGraph;
  private Set<DatabaseObjectNodeId> tableViewNodes;

  public SchemaGraphCache build() {
    if (fullGraph == null) {
      throw new IllegalStateException("Build nodes and edges before building the cache");
    }
    if (metricsGraph == null || tableViewNodes == null) {
      precomputeViews();
    }
    return new SchemaGraphCache(fullGraph, metricsGraph, tableViewNodes, nodeToObject, keyToObject);
  }

  public SchemaGraphCacheBuilder buildNodesAndEdges(final Catalog catalog) {
    fullGraph = new DirectedPseudograph<>(SchemaEdge.class);
    nodeToObject = new LinkedHashMap<>();
    keyToObject = new LinkedHashMap<>();

    for (final Table table : catalog.getTables()) {
      addNode(table);
    }
    for (final schemacrawler.schema.Routine routine : catalog.getRoutines()) {
      addNode(routine);
    }
    for (final schemacrawler.schema.Synonym synonym : catalog.getSynonyms()) {
      addNode(synonym);
    }
    EdgeFactory.addEdges(
        catalog.getTables(), catalog.getRoutines(), catalog.getSynonyms(), fullGraph);
    return this;
  }

  public SchemaGraphCacheBuilder precomputeViews() {
    if (fullGraph == null) {
      throw new IllegalStateException("Build nodes and edges before precomputing views");
    }
    final Set<SchemaEdge> declaredEdges = new LinkedHashSet<>();
    for (final SchemaEdge edge : fullGraph.edgeSet()) {
      if (edge.getEdgeType() != EdgeType.IMPLICIT_ASSOCIATION) {
        declaredEdges.add(edge);
      }
    }
    metricsGraph = new AsSubgraph<>(fullGraph, fullGraph.vertexSet(), declaredEdges);
    tableViewNodes = new LinkedHashSet<>();
    for (final DatabaseObjectNodeId nodeId : fullGraph.vertexSet()) {
      if (nodeId.type() == SimpleDatabaseObjectType.table
          || nodeId.type() == SimpleDatabaseObjectType.view) {
        tableViewNodes.add(nodeId);
      }
    }
    return this;
  }

  private void addNode(final DatabaseObject databaseObject) {
    final DatabaseObjectNodeId nodeId = NodeIdFactory.create(databaseObject);
    fullGraph.addVertex(nodeId);
    nodeToObject.put(nodeId, databaseObject);
    keyToObject.put(databaseObject.key(), databaseObject);
  }
}
