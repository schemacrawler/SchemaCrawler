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
import java.util.logging.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DirectedPseudograph;
import schemacrawler.importance.cache.DatabaseObjectNodeId;
import schemacrawler.importance.cache.EdgeType;
import schemacrawler.importance.cache.SchemaEdge;
import schemacrawler.importance.cache.SchemaGraphCache;
import schemacrawler.importance.cache.TableImportance;
import schemacrawler.importance.cache.TableImportanceMetrics;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.tools.utility.TableImportanceUtility;
import schemacrawler.tools.utility.TableTraits;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

/** Builds the immutable dependency graph foundation from a SchemaCrawler catalog. */
public final class SchemaGraphCacheBuilder {

  private static final Logger LOGGER = Logger.getLogger(SchemaGraphCacheBuilder.class.getName());

  private static TableTraits tableTraits(final Table table) {
    for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
      if (foreignKey.getPrimaryKeyTable() == null || foreignKey.key() == null) {
        LOGGER.warning(
            () ->
                "Skipping entity-model inference for "
                    + table.key()
                    + " because it has malformed foreign-key metadata");
        return new TableTraits();
      }
    }
    return TableImportanceUtility.tableTraitsfrom(table);
  }

  private Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph;
  private Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject;
  private Map<NamedObjectKey, DatabaseObject> keyToObject;
  private Graph<DatabaseObjectNodeId, SchemaEdge> metricsGraph;
  private Set<DatabaseObjectNodeId> tableViewNodes;

  private Set<NamedObjectKey> ambiguousObjectKeys;

  public SchemaGraphCache build() {
    if (fullGraph == null) {
      throw new IllegalStateException("Build nodes and edges before building the cache");
    }
    if (metricsGraph == null || tableViewNodes == null) {
      precomputeViews();
    }
    final Map<DatabaseObjectNodeId, TableImportanceMetrics> metrics =
        GraphMetricsCalculator.calculate(metricsGraph);
    storeTableImportance(metrics);
    return new SchemaGraphCache(fullGraph, tableViewNodes, nodeToObject);
  }

  public SchemaGraphCacheBuilder buildNodesAndEdges(final Catalog catalog) {
    fullGraph = new DirectedPseudograph<>(SchemaEdge.class);
    nodeToObject = new LinkedHashMap<>();
    keyToObject = new LinkedHashMap<>();
    ambiguousObjectKeys = new LinkedHashSet<>();
    metricsGraph = null;
    tableViewNodes = null;

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
    final NamedObjectKey key = databaseObject.key();
    if (keyToObject.containsKey(key)) {
      keyToObject.remove(key);
      ambiguousObjectKeys.add(key);
    } else if (!ambiguousObjectKeys.contains(key)) {
      keyToObject.put(key, databaseObject);
    }
  }

  private void storeTableImportance(
      final Map<DatabaseObjectNodeId, TableImportanceMetrics> metrics) {
    for (final Map.Entry<DatabaseObjectNodeId, DatabaseObject> entry : nodeToObject.entrySet()) {
      if (entry.getValue() instanceof final Table table) {
        table.setAttribute(
            TableImportance.class.getName(),
            new TableImportance(
                tableTraits(table),
                TableImportanceUtility.tableCountsfrom(table),
                metrics.get(entry.getKey())));
      }
    }
  }
}
