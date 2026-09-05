/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.builder;

import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.jgrapht.Graph;
import org.jgrapht.alg.clustering.LabelPropagationClustering;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.AsUndirectedGraph;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.SchemaCommunity;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;
import us.fatehi.utility.UtilityMarker;

/** Detects functional domain communities over schema graph table and view nodes. */
@UtilityMarker
final class CommunityDetector {

  static List<SchemaCommunity> detectCommunities(
      final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph,
      final Set<DatabaseObjectNodeId> tableNodes,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject) {
    requireNonNull(fullGraph, "No full graph provided");
    requireNonNull(tableNodes, "No table and view nodes provided");
    requireNonNull(nodeToObject, "No node-to-object map provided");
    if (tableNodes.isEmpty()) {
      return List.of();
    }

    final Graph<DatabaseObjectNodeId, SchemaEdge> tableSubgraph =
        tableSubgraph(fullGraph, tableNodes);
    final List<SchemaCommunity> communities = createCommunities(tableSubgraph, nodeToObject);
    return sortCommunities(communities, nodeToObject);
  }

  private static List<SchemaCommunity> createCommunities(
      final Graph<DatabaseObjectNodeId, SchemaEdge> tableSubgraph,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject) {
    final ClusteringAlgorithm.Clustering<DatabaseObjectNodeId> clustering =
        new LabelPropagationClustering<>(tableSubgraph, 100, new Random(0)).getClustering();

    final List<SchemaCommunity> communities = new ArrayList<>();
    for (final Set<DatabaseObjectNodeId> cluster : clustering.getClusters()) {
      if (cluster != null && !cluster.isEmpty()) {
        communities.add(createCommunity(cluster, nodeToObject));
      }
    }
    return communities;
  }

  private static SchemaCommunity createCommunity(
      final Set<DatabaseObjectNodeId> cluster,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject) {
    // Sorting establishes both the anchor and deterministic member output.
    final List<DatabaseObjectNodeId> sortedMembers =
        cluster.stream()
            .sorted(
                Comparator.comparingInt(
                        (final DatabaseObjectNodeId nodeId) ->
                            getImportanceScore(nodeId, nodeToObject))
                    .reversed()
                    .thenComparing(nodeId -> getTableFullName(nodeId, nodeToObject)))
            .toList();
    final DatabaseObjectNodeId anchorNode = sortedMembers.get(0);
    final String anchorFullName = getTableFullName(anchorNode, nodeToObject);
    final UUID communityId =
        UUID.nameUUIDFromBytes(("community:" + anchorFullName).getBytes(StandardCharsets.UTF_8));
    return new SchemaCommunity(communityId, anchorNode, sortedMembers);
  }

  private static int getImportanceScore(
      final DatabaseObjectNodeId nodeId,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject) {
    final DatabaseObject object = nodeToObject.get(nodeId);
    if (object instanceof final Table table) {
      final TableImportance importance = table.getAttribute(TableImportance.class.getName());
      if (importance != null) {
        return importance.importanceScore();
      }
    }
    return 0;
  }

  private static String getTableFullName(
      final DatabaseObjectNodeId nodeId,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject) {
    final DatabaseObject object = nodeToObject.get(nodeId);
    if (object != null && object.getFullName() != null) {
      return object.getFullName();
    }
    return nodeId.key().toString();
  }

  private static List<SchemaCommunity> sortCommunities(
      final List<SchemaCommunity> communities,
      final Map<DatabaseObjectNodeId, DatabaseObject> nodeToObject) {
    return communities.stream()
        .sorted(
            Comparator.comparingInt(
                    (final SchemaCommunity community) ->
                        getImportanceScore(community.anchorNode(), nodeToObject))
                .reversed()
                .thenComparing(community -> getTableFullName(community.anchorNode(), nodeToObject)))
        .toList();
  }

  private static Graph<DatabaseObjectNodeId, SchemaEdge> tableSubgraph(
      final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph,
      final Set<DatabaseObjectNodeId> tableNodes) {
    final Set<SchemaEdge> tableEdges = new LinkedHashSet<>();
    for (final SchemaEdge edge : fullGraph.edgeSet()) {
      final DatabaseObjectNodeId source = fullGraph.getEdgeSource(edge);
      final DatabaseObjectNodeId target = fullGraph.getEdgeTarget(edge);
      if (tableNodes.contains(source) && tableNodes.contains(target)) {
        tableEdges.add(edge);
      }
    }
    // Community affinity is direction-independent, unlike schema dependencies.
    return new AsUndirectedGraph<>(new AsSubgraph<>(fullGraph, tableNodes, tableEdges));
  }

  private CommunityDetector() {
    // Prevent instantiation
  }
}
