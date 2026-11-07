/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.implementation;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DirectedPseudograph;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.VertexUtility;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;

/** Holds the mutable graph state and typed catalog indexes used while building a graph model. */
final class SchemaGraphAssembly {

  private final Graph<DatabaseObjectVertexId, SchemaEdge> catalogGraph;
  private final Map<DatabaseObjectVertexId, DatabaseObject> objectsByVertexId;
  private final Map<DatabaseObjectVertexId, Table> tablesByVertexId;

  SchemaGraphAssembly() {
    // Vertices and edges are supplied explicitly; avoid serializing a lambda-based edge supplier.
    catalogGraph = new DirectedPseudograph<>(null, null, false);
    objectsByVertexId = new LinkedHashMap<>();
    tablesByVertexId = new LinkedHashMap<>();
  }

  boolean addEdge(final DatabaseObject source, final DatabaseObject target, final SchemaEdge edge) {
    if (source == null || target == null) {
      return false;
    }
    final DatabaseObjectVertexId sourceVertexId = VertexUtility.createVertexId(source);
    final DatabaseObjectVertexId targetVertexId = VertexUtility.createVertexId(target);
    if (!catalogGraph.containsVertex(sourceVertexId)
        || !catalogGraph.containsVertex(targetVertexId)) {
      return false;
    }
    catalogGraph.addEdge(sourceVertexId, targetVertexId, edge);
    return true;
  }

  void addNode(final DatabaseObject databaseObject) {
    requireNonNull(databaseObject, "No database object provided");
    final DatabaseObjectVertexId vertexId = VertexUtility.createVertexId(databaseObject);
    catalogGraph.addVertex(vertexId);
    objectsByVertexId.put(vertexId, databaseObject);
    if (databaseObject instanceof final Table table) {
      tablesByVertexId.put(vertexId, table);
    }
  }

  Graph<DatabaseObjectVertexId, SchemaEdge> catalogGraph() {
    return catalogGraph;
  }

  Map<DatabaseObjectVertexId, DatabaseObject> objectsByVertexId() {
    return objectsByVertexId;
  }

  Set<DatabaseObjectVertexId> tableVertexIds() {
    return tablesByVertexId.keySet();
  }

  Map<DatabaseObjectVertexId, Table> tablesByVertexId() {
    return tablesByVertexId;
  }

  Graph<DatabaseObjectVertexId, SchemaEdge> tableSubgraph() {
    return new AsSubgraph<>(catalogGraph, tablesByVertexId.keySet());
  }
}
