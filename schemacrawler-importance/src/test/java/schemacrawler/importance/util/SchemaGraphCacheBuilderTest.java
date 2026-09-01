package schemacrawler.importance.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.cache.DatabaseObjectNodeId;
import schemacrawler.importance.cache.EdgeType;
import schemacrawler.importance.cache.SchemaEdge;
import schemacrawler.importance.cache.SchemaGraphCache;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TableType;
import schemacrawler.schema.View;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class SchemaGraphCacheBuilderTest {

  @Test
  void buildsAnEmptyCatalog() {
    final SchemaGraphCache cache =
        new SchemaGraphCacheBuilder().buildNodesAndEdges(catalog()).build();

    assertThat(cache.getFullGraph().vertexSet(), hasSize(0));
    assertThat(cache.getMetricsGraph().edgeSet(), hasSize(0));
  }

  @Test
  void buildsASingleTableCatalog() {
    final Table customers = table("CUSTOMERS");

    final SchemaGraphCache cache =
        new SchemaGraphCacheBuilder()
            .buildNodesAndEdges(catalog(List.of(customers), List.of(), List.of()))
            .build();

    assertThat(cache.getFullGraph().vertexSet(), hasSize(1));
    assertThat(cache.getTableViewNodes(), hasSize(1));
  }

  @Test
  void buildsTypedEdgesForAllSupportedCatalogObjects() {
    final Table customers = table("CUSTOMERS");
    final Table orders = table("ORDERS");
    final View orderSummary = mock(View.class);
    initialize(orderSummary, "ORDER_SUMMARY");
    when(orderSummary.getTableType()).thenReturn(new TableType("VIEW"));
    doReturn(List.of(orders)).when(orderSummary).getReferencedObjects();
    final Procedure refreshOrders = mock(Procedure.class);
    initialize(refreshOrders, "REFRESH_ORDERS");
    doReturn(List.of(orders)).when(refreshOrders).getReferencedObjects();
    final Synonym customerAlias = mock(Synonym.class);
    initialize(customerAlias, "CUSTOMER_ALIAS");
    when(customerAlias.hasReferencedObject()).thenReturn(true);
    when(customerAlias.getReferencedObject()).thenReturn(customers);

    final ForeignKey foreignKey = mock(ForeignKey.class);
    when(foreignKey.getPrimaryKeyTable()).thenReturn(customers);
    when(foreignKey.key()).thenReturn(new NamedObjectKey("FK_ORDERS_CUSTOMERS"));
    when(orders.getImportedForeignKeys()).thenReturn(List.of(foreignKey));

    final TableReference impliedAssociation = mock(TableReference.class);
    when(impliedAssociation.getType()).thenReturn(TableConstraintType.implicit_association);
    when(impliedAssociation.getPrimaryKeyTable()).thenReturn(customers);
    when(impliedAssociation.key()).thenReturn(new NamedObjectKey("IA_ORDERS_CUSTOMERS"));
    when(orders.getTableConstraints()).thenReturn(List.of(impliedAssociation));

    final SchemaGraphCache cache =
        new SchemaGraphCacheBuilder()
            .buildNodesAndEdges(
                catalog(
                    List.of(customers, orders, orderSummary),
                    List.<Routine>of(refreshOrders),
                    List.of(customerAlias)))
            .precomputeViews()
            .build();

    final Graph<DatabaseObjectNodeId, SchemaEdge> fullGraph = cache.getFullGraph();
    assertThat(fullGraph.vertexSet(), hasSize(5));
    assertThat(fullGraph.edgeSet(), hasSize(5));
    assertThat(edgesOfType(fullGraph, EdgeType.FOREIGN_KEY), is(1));
    assertThat(edgesOfType(fullGraph, EdgeType.IMPLICIT_ASSOCIATION), is(1));
    assertThat(edgesOfType(fullGraph, EdgeType.VIEW_DEPENDENCY), is(1));
    assertThat(edgesOfType(fullGraph, EdgeType.ROUTINE_DEPENDENCY), is(1));
    assertThat(edgesOfType(fullGraph, EdgeType.SYNONYM_RESOLUTION), is(1));
    final SchemaEdge foreignKeyEdge =
        fullGraph.edgeSet().stream()
            .filter(edge -> edge.getEdgeType() == EdgeType.FOREIGN_KEY)
            .findFirst()
            .orElseThrow();
    assertThat(fullGraph.getEdgeSource(foreignKeyEdge), is(NodeIdFactory.create(orders)));
    assertThat(fullGraph.getEdgeTarget(foreignKeyEdge), is(NodeIdFactory.create(customers)));
    assertThat(foreignKeyEdge.getReferenceKey(), is(foreignKey.key()));
    assertThat(cache.getMetricsGraph().edgeSet(), hasSize(4));
    assertThat(cache.getTableViewNodes(), hasSize(3));
    assertThat(cache.getObjectByKey(customers.key()), is(customers));
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            fullGraph.addVertex(
                new DatabaseObjectNodeId(
                    new NamedObjectKey("OTHER"), SimpleDatabaseObjectType.table)));
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            cache
                .getMetricsGraph()
                .addVertex(
                    new DatabaseObjectNodeId(
                        new NamedObjectKey("OTHER"), SimpleDatabaseObjectType.table)));
    assertThrows(UnsupportedOperationException.class, cache.getTableViewNodes()::clear);
  }

  @Test
  void skipsMalformedForeignKeyReferences() {
    final Table orders = table("ORDERS");
    final ForeignKey missingTarget = mock(ForeignKey.class);
    when(missingTarget.getPrimaryKeyTable()).thenReturn(null);
    final ForeignKey excludedTarget = mock(ForeignKey.class);
    final Table excludedCustomers = table("CUSTOMERS");
    when(excludedTarget.getPrimaryKeyTable()).thenReturn(excludedCustomers);
    when(orders.getImportedForeignKeys()).thenReturn(List.of(missingTarget, excludedTarget));

    final SchemaGraphCache cache =
        new SchemaGraphCacheBuilder()
            .buildNodesAndEdges(catalog(List.of(orders), List.of(), List.of()))
            .build();

    assertThat(cache.getFullGraph().edgeSet(), hasSize(0));
  }

  private static Catalog catalog() {
    return catalog(List.of(), List.of(), List.of());
  }

  private static Catalog catalog(
      final List<Table> tables, final List<Routine> routines, final List<Synonym> synonyms) {
    final Catalog catalog = mock(Catalog.class);
    when(catalog.getTables()).thenReturn(tables);
    when(catalog.getRoutines()).thenReturn(routines);
    when(catalog.getSynonyms()).thenReturn(synonyms);
    return catalog;
  }

  private static int edgesOfType(
      final Graph<DatabaseObjectNodeId, SchemaEdge> graph, final EdgeType edgeType) {
    return (int) graph.edgeSet().stream().filter(edge -> edge.getEdgeType() == edgeType).count();
  }

  private static Table table(final String name) {
    final Table table = mock(Table.class);
    initialize(table, name);
    when(table.getImportedForeignKeys()).thenReturn(List.of());
    when(table.getTableConstraints()).thenReturn(List.of());
    return table;
  }

  private static void initialize(final Table table, final String name) {
    when(table.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
    when(table.getTableType()).thenReturn(new TableType("TABLE"));
  }

  private static void initialize(final Procedure procedure, final String name) {
    when(procedure.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
  }

  private static void initialize(final Synonym synonym, final String name) {
    when(synonym.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
  }
}
