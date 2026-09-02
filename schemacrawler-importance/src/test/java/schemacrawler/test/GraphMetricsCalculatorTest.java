package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedPseudograph;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.EdgeType;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.TableImportanceMetrics;
import schemacrawler.importance.util.GraphMetricsCalculator;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class GraphMetricsCalculatorTest {

  @Test
  void calculatesCompleteSchemaGraphMetricsIncludingUndirectedBridgeCentrality() {
    final DatabaseObjectNodeId orders = node("ORDERS");
    final DatabaseObjectNodeId customers = node("CUSTOMERS");
    final DatabaseObjectNodeId countries = node("COUNTRIES");
    final Graph<DatabaseObjectNodeId, SchemaEdge> graph =
        new DirectedPseudograph<>(SchemaEdge.class);
    graph.addVertex(orders);
    graph.addVertex(customers);
    graph.addVertex(countries);
    graph.addEdge(orders, customers, new SchemaEdge(EdgeType.FOREIGN_KEY, null));
    graph.addEdge(orders, countries, new SchemaEdge(EdgeType.IMPLICIT_ASSOCIATION, null));

    final var metrics = GraphMetricsCalculator.calculate(graph);
    final TableImportanceMetrics ordersMetrics = metrics.get(orders);
    final TableImportanceMetrics customersMetrics = metrics.get(customers);
    final TableImportanceMetrics countriesMetrics = metrics.get(countries);

    assertThat(ordersMetrics.inDegree(), is(0));
    assertThat(ordersMetrics.outDegree(), is(2));
    assertThat(ordersMetrics.dependencyReachabilityCount(), is(2));
    assertThat(ordersMetrics.impactReachabilityCount(), is(0));
    assertThat(customersMetrics.inDegree(), is(1));
    assertThat(customersMetrics.outDegree(), is(0));
    assertThat(customersMetrics.dependencyReachabilityCount(), is(0));
    assertThat(customersMetrics.impactReachabilityCount(), is(1));
    assertThat(
        ordersMetrics.betweennessCentrality(),
        greaterThan(customersMetrics.betweennessCentrality()));
    assertThat(
        ordersMetrics.betweennessCentrality(),
        greaterThan(countriesMetrics.betweennessCentrality()));
  }

  private static DatabaseObjectNodeId node(final String name) {
    return new DatabaseObjectNodeId(
        new NamedObjectKey("PUBLIC", name), SimpleDatabaseObjectType.table);
  }
}
