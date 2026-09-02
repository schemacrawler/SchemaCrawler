package schemacrawler.importance.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.cache.DatabaseObjectNodeId;
import schemacrawler.importance.cache.EdgeType;
import schemacrawler.importance.cache.SchemaEdge;
import schemacrawler.importance.cache.SchemaGraphCache;
import schemacrawler.importance.cache.TableImportance;
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

  private static void initialize(final Procedure procedure, final String name) {
    when(procedure.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
  }

  private static void initialize(final Synonym synonym, final String name) {
    when(synonym.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
  }

  private static void initialize(final Table table, final String name) {
    when(table.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
    when(table.getTableType()).thenReturn(new TableType("TABLE"));
    when(table.getColumns()).thenReturn(List.of());
    when(table.getReferencedTables()).thenReturn(List.of());
    when(table.getIndexes()).thenReturn(List.of());
    when(table.getTriggers()).thenReturn(List.of());
    when(table.hasPrimaryKey()).thenReturn(false);
    when(table.hasForeignKeys()).thenReturn(false);
    when(table.hasIndexes()).thenReturn(false);
    when(table.isSelfReferencing()).thenReturn(false);
    when(table.hasTriggers()).thenReturn(false);
  }

  private static Table table(final String name) {
    final Table table = mock(Table.class);
    initialize(table, name);
    when(table.getImportedForeignKeys()).thenReturn(List.of());
    when(table.getTableConstraints()).thenReturn(List.of());
    when(table.getColumns()).thenReturn(List.of());
    when(table.getReferencedTables()).thenReturn(List.of());
    when(table.getIndexes()).thenReturn(List.of());
    when(table.getTriggers()).thenReturn(List.of());
    when(table.hasPrimaryKey()).thenReturn(false);
    when(table.hasForeignKeys()).thenReturn(false);
    when(table.hasIndexes()).thenReturn(false);
    when(table.isSelfReferencing()).thenReturn(false);
    when(table.hasTriggers()).thenReturn(false);
    return table;
  }

  @Test
  void buildsAnEmptyCatalog() {
    final Catalog catalog = catalog();
    final SchemaGraphCache cache = SchemaGraphCacheBuilder.builder(catalog).build();

    assertThat(cache.getFullGraph().vertexSet(), hasSize(0));
  }

  @Test
  void buildsASingleTableCatalog() {
    final Table customers = table("CUSTOMERS");
    final AtomicReference<TableImportance> importance = new AtomicReference<>();
    doAnswer(
            invocation -> {
              importance.set(invocation.getArgument(1));
              return null;
            })
        .when(customers)
        .setAttribute(eq(TableImportance.class.getName()), any());
    doAnswer(invocation -> importance.get())
        .when(customers)
        .getAttribute(TableImportance.class.getName());

    final Catalog catalog = catalog(List.of(customers), List.of(), List.of());
    final SchemaGraphCache cache = SchemaGraphCacheBuilder.builder(catalog).build();

    assertThat(cache.getFullGraph().vertexSet(), hasSize(1));
    assertThat(cache.getTableNodes(), hasSize(1));
    assertThat(
        customers
            .<TableImportance>getAttribute(TableImportance.class.getName())
            .graphMetrics()
            .outDegree(),
        is(0));
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

    final Catalog catalog =
        catalog(
            List.of(customers, orders, orderSummary),
            List.<Routine>of(refreshOrders),
            List.of(customerAlias));
    final SchemaGraphCache cache = SchemaGraphCacheBuilder.builder(catalog).build();

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
    assertThat(cache.getTableNodes(), hasSize(3));
    verify(orderSummary)
        .setAttribute(eq(TableImportance.class.getName()), any(TableImportance.class));
    verify(refreshOrders, never()).setAttribute(anyString(), any());
    verify(customerAlias, never()).setAttribute(anyString(), any());
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            fullGraph.addVertex(
                new DatabaseObjectNodeId(
                    new NamedObjectKey("OTHER"), SimpleDatabaseObjectType.table)));
    assertThrows(UnsupportedOperationException.class, cache.getTableNodes()::clear);
  }

  @Test
  void retainsTypedObjectLookupsForCollidingNames() {
    final Table table = table("ORDERS");
    final Procedure procedure = mock(Procedure.class);
    initialize(procedure, "ORDERS");

    final Catalog catalog = catalog(List.of(table), List.<Routine>of(procedure), List.of());
    final SchemaGraphCache cache = SchemaGraphCacheBuilder.builder(catalog).build();

    assertThat(cache.getObjectByNodeId(NodeIdFactory.create(table)), is(table));
    assertThat(cache.getObjectByNodeId(NodeIdFactory.create(procedure)), is(procedure));
  }
}
