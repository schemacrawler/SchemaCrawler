/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.util;

import static java.util.Objects.requireNonNull;

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
import schemacrawler.importance.cache.TableImportance;
import schemacrawler.importance.cache.TableImportanceMetrics;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.tools.utility.TableImportanceUtility;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;
import us.fatehi.utility.Builder;

/** Builds the immutable dependency graph foundation from a SchemaCrawler catalog. */
public final class SchemaGraphCacheBuilder implements Builder<SchemaGraphCache> {

  public static SchemaGraphCacheBuilder builder(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");
    return new SchemaGraphCacheBuilder(catalog);
  }

  private Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph;
  private Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject;
  private Set<DatabaseObjectNodeId> tableViewNodes;

  private SchemaGraphCacheBuilder(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    fullGraph = new DirectedPseudograph<>(SchemaEdge.class);
    nodeToObject = new LinkedHashMap<>();
    tableViewNodes = new LinkedHashSet<>();

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
  }

  @Override
  public SchemaGraphCache build() {
    if (fullGraph == null) {
      throw new IllegalStateException("Build nodes and edges before building the cache");
    }
    final Map<DatabaseObjectNodeId, TableImportanceMetrics> metrics =
        GraphMetricsCalculator.calculate(declaredDependenciesGraph());
    storeTableImportance(metrics);
    return new SchemaGraphCache(fullGraph, tableViewNodes, nodeToObject);
  }

  private void addNode(final DatabaseObject databaseObject) {
    final DatabaseObjectNodeId nodeId = NodeIdFactory.create(databaseObject);
    fullGraph.addVertex(nodeId);
    nodeToObject.put(nodeId, databaseObject);
    if (nodeId.type() == SimpleDatabaseObjectType.table
        || nodeId.type() == SimpleDatabaseObjectType.view) {
      tableViewNodes.add(nodeId);
    }
  }

  private Graph<DatabaseObjectNodeId, SchemaEdge> declaredDependenciesGraph() {
    final Set<SchemaEdge> declaredEdges = new LinkedHashSet<>();
    for (final SchemaEdge edge : fullGraph.edgeSet()) {
      if (edge.getEdgeType() != EdgeType.IMPLICIT_ASSOCIATION) {
        declaredEdges.add(edge);
      }
    }
    return new AsSubgraph<>(fullGraph, fullGraph.vertexSet(), declaredEdges);
  }

  private void storeTableImportance(
      final Map<DatabaseObjectNodeId, TableImportanceMetrics> metrics) {
    for (final Map.Entry<DatabaseObjectNodeId, DatabaseObject> entry : nodeToObject.entrySet()) {
      if (entry.getValue() instanceof final Table table) {
        table.setAttribute(
            TableImportance.class.getName(),
            new TableImportance(
                TableImportanceUtility.tableTraitsfrom(table),
                TableImportanceUtility.tableCountsfrom(table),
                metrics.get(entry.getKey())));
      }
    }
  }
}
