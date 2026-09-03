/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.builder;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedPseudograph;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.tools.utility.TableImportanceUtility;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;
import us.fatehi.utility.Builder;

/** Builds the immutable dependency graph foundation from a SchemaCrawler catalog. */
public final class SchemaGraphModelBuilder implements Builder<SchemaGraphModel> {

  public static SchemaGraphModelBuilder builder(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");
    return new SchemaGraphModelBuilder(catalog);
  }

  private final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph;
  private final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject;
  private final Set<DatabaseObjectNodeId> tableViewNodes;

  private SchemaGraphModelBuilder(final Catalog catalog) {
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
  public SchemaGraphModel build() {
    if (fullGraph == null) {
      throw new IllegalStateException(
          "Build nodes and edges before building the schema graph model");
    }
    final Map<DatabaseObjectNodeId, TableImportanceMetrics> topologyMetrics =
        GraphMetricsCalculator.calculate(fullGraph);

    final TableImportanceInputs inputs = new TableImportanceInputs();
    for (final Map.Entry<DatabaseObjectNodeId, TableImportanceMetrics> entry :
        topologyMetrics.entrySet()) {
      inputs.put(entry.getKey(), entry.getValue());
    }
    for (final Map.Entry<DatabaseObjectNodeId, DatabaseObject> entry : nodeToObject.entrySet()) {
      if (entry.getValue() instanceof final Table table) {
        inputs.put(entry.getKey(), TableImportanceUtility.tableTraitsfrom(table));
        inputs.put(entry.getKey(), TableImportanceUtility.tableCountsfrom(table));
      }
    }

    final Map<DatabaseObjectNodeId, Integer> importanceScores =
        ImportanceScoreCalculator.calculate(inputs);

    storeTableImportance(inputs, importanceScores);
    return new SchemaGraphModel(fullGraph, tableViewNodes, nodeToObject);
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

  private void storeTableImportance(
      final TableImportanceInputs inputs,
      final Map<DatabaseObjectNodeId, Integer> importanceScores) {
    for (final Map.Entry<DatabaseObjectNodeId, DatabaseObject> entry : nodeToObject.entrySet()) {
      final DatabaseObjectNodeId nodeId = entry.getKey();
      if (entry.getValue() instanceof final Table table) {
        final TableImportanceInputs.TableImportanceInput tableInputs = inputs.get(nodeId);
        table.setAttribute(
            TableImportance.class.getName(),
            new TableImportance(
                importanceScores.get(nodeId),
                tableInputs.importanceMetrics(),
                tableInputs.tableTraits(),
                tableInputs.tableCounts()));
      }
    }
  }
}
