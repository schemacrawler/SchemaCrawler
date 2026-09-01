package schemacrawler.importance.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedPseudograph;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.cache.DatabaseObjectNodeId;
import schemacrawler.importance.cache.EdgeType;
import schemacrawler.importance.cache.SchemaEdge;
import schemacrawler.importance.cache.SchemaGraphCache;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class PathServiceTest {

  @Test
  void prefersAnAvailableForeignKeyPath() {
    final DatabaseObjectNodeId orders = table("ORDERS");
    final DatabaseObjectNodeId customers = table("CUSTOMERS");
    final DatabaseObjectNodeId countries = table("COUNTRIES");
    final PathService pathService =
        pathService(
            List.of(orders, customers, countries),
            edge(orders, customers, EdgeType.FOREIGN_KEY),
            edge(customers, countries, EdgeType.FOREIGN_KEY),
            edge(orders, countries, EdgeType.IMPLICIT_ASSOCIATION));

    final PathResult result = pathService.findShortestPath(orders, countries);

    assertThat(result.path(), contains(orders, customers, countries));
    assertThat(result.usesImpliedAssociations(), is(false));
  }

  @Test
  void fallsBackToImplicitAssociations() {
    final DatabaseObjectNodeId orders = table("ORDERS");
    final DatabaseObjectNodeId customers = table("CUSTOMERS");
    final PathService pathService =
        pathService(
            List.of(orders, customers), edge(orders, customers, EdgeType.IMPLICIT_ASSOCIATION));

    final PathResult result = pathService.findShortestPath(orders, customers);

    assertThat(result.path(), contains(orders, customers));
    assertThat(result.usesImpliedAssociations(), is(true));
  }

  @Test
  void handlesNoPathSameNodeAndUnsupportedNodes() {
    final DatabaseObjectNodeId orders = table("ORDERS");
    final DatabaseObjectNodeId customers = table("CUSTOMERS");
    final DatabaseObjectNodeId procedure =
        new DatabaseObjectNodeId(
            new NamedObjectKey("PUBLIC", "REFRESH_ORDERS"), SimpleDatabaseObjectType.procedure);
    final PathService pathService = pathService(List.of(orders, customers));

    assertThat(pathService.findShortestPath(orders, customers).path(), empty());
    assertThat(pathService.findShortestPath(orders, orders).path(), contains(orders));
    assertThrows(
        IllegalArgumentException.class, () -> pathService.findShortestPath(procedure, customers));
  }

  @Test
  void returnsThePathWithFewestEdges() {
    final DatabaseObjectNodeId orders = table("ORDERS");
    final DatabaseObjectNodeId customers = table("CUSTOMERS");
    final DatabaseObjectNodeId countries = table("COUNTRIES");
    final DatabaseObjectNodeId regions = table("REGIONS");
    final PathService pathService =
        pathService(
            List.of(orders, customers, countries, regions),
            edge(orders, customers, EdgeType.FOREIGN_KEY),
            edge(customers, countries, EdgeType.FOREIGN_KEY),
            edge(orders, regions, EdgeType.FOREIGN_KEY),
            edge(regions, countries, EdgeType.FOREIGN_KEY),
            edge(orders, countries, EdgeType.FOREIGN_KEY));

    assertThat(pathService.findShortestPath(orders, countries).path(), contains(orders, countries));
  }

  private static Edge edge(
      final DatabaseObjectNodeId source,
      final DatabaseObjectNodeId target,
      final EdgeType edgeType) {
    return new Edge(source, target, new SchemaEdge(edgeType, new NamedObjectKey(edgeType.name())));
  }

  private static PathService pathService(
      final List<DatabaseObjectNodeId> nodes, final Edge... graphEdges) {
    final Graph<DatabaseObjectNodeId, SchemaEdge> graph =
        new DirectedPseudograph<>(SchemaEdge.class);
    nodes.forEach(graph::addVertex);
    for (final Edge edge : graphEdges) {
      graph.addEdge(edge.source(), edge.target(), edge.edge());
    }
    return new PathService(
        new SchemaGraphCache(graph, graph, Set.copyOf(nodes), Map.of(), Map.of()));
  }

  private static DatabaseObjectNodeId table(final String name) {
    return new DatabaseObjectNodeId(
        new NamedObjectKey("PUBLIC", name), SimpleDatabaseObjectType.table);
  }

  private record Edge(DatabaseObjectNodeId source, DatabaseObjectNodeId target, SchemaEdge edge) {}
}
