package schemacrawler.importance.model.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.EdgeType;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.importance.model.VertexUtility;
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
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

class ImportanceModelBuilderTest {

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
      final Graph<DatabaseObjectVertexId, SchemaEdge> graph, final EdgeType edgeType) {
    return (int) graph.edgeSet().stream().filter(edge -> edge.getEdgeType() == edgeType).count();
  }

  private static void initialize(final Procedure procedure, final String name) {
    when(procedure.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
  }

  private static void initialize(final Synonym synonym, final String name) {
    when(synonym.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
  }

  private static Table table(final String name) {
    return spy(new LightTable(name));
  }

  @Test
  void buildsAnEmptyCatalog() {
    final Catalog catalog = catalog();
    final ImportanceModel importanceModel = ImportanceModelBuilder.builder(catalog).build();

    assertThat(importanceModel.getCatalogGraph().vertexSet(), hasSize(0));
  }

  @Test
  void buildsASingleTableCatalog() {
    final Table customers = table("CUSTOMERS");
    final Catalog catalog = catalog(List.of(customers), List.of(), List.of());
    final ImportanceModel importanceModel = ImportanceModelBuilder.builder(catalog).build();

    assertThat(importanceModel.getCatalogGraph().vertexSet(), hasSize(1));
    assertThat(importanceModel.getTableVertexIds(), hasSize(1));
    assertThat(
        customers
            .<TableImportance>getAttribute(TableImportance.class.getName())
            .importanceMetrics()
            .outDegree(),
        is(0));
  }

  @Test
  void buildsTypedEdgesForAllSupportedCatalogObjects() {
    final Table customers = table("CUSTOMERS");
    final Table orders = table("ORDERS");
    final View orderSummary = mock(View.class);
    when(orderSummary.key()).thenReturn(new NamedObjectKey("PUBLIC", "ORDER_SUMMARY"));
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
    doReturn(List.of(foreignKey)).when(orders).getImportedForeignKeys();

    final TableReference impliedAssociation = mock(TableReference.class);
    when(impliedAssociation.getType()).thenReturn(TableConstraintType.implicit_association);
    when(impliedAssociation.getPrimaryKeyTable()).thenReturn(customers);
    when(impliedAssociation.key()).thenReturn(new NamedObjectKey("IA_ORDERS_CUSTOMERS"));
    doReturn(List.of(impliedAssociation)).when(orders).getTableConstraints();

    final Catalog catalog =
        catalog(
            List.of(customers, orders, orderSummary),
            List.<Routine>of(refreshOrders),
            List.of(customerAlias));
    final ImportanceModel importanceModel = ImportanceModelBuilder.builder(catalog).build();

    final Graph<DatabaseObjectVertexId, SchemaEdge> catalogGraph =
        importanceModel.getCatalogGraph();
    assertThat(catalogGraph.vertexSet(), hasSize(5));
    assertThat(catalogGraph.edgeSet(), hasSize(5));
    assertThat(edgesOfType(catalogGraph, EdgeType.FOREIGN_KEY), is(1));
    assertThat(edgesOfType(catalogGraph, EdgeType.IMPLICIT_ASSOCIATION), is(1));
    assertThat(edgesOfType(catalogGraph, EdgeType.VIEW_DEPENDENCY), is(1));
    assertThat(edgesOfType(catalogGraph, EdgeType.ROUTINE_DEPENDENCY), is(1));
    assertThat(edgesOfType(catalogGraph, EdgeType.SYNONYM_RESOLUTION), is(1));
    final SchemaEdge foreignKeyEdge =
        catalogGraph.edgeSet().stream()
            .filter(edge -> edge.getEdgeType() == EdgeType.FOREIGN_KEY)
            .findFirst()
            .orElseThrow();
    final SchemaEdge implicitAssocEdge =
        catalogGraph.edgeSet().stream()
            .filter(edge -> edge.getEdgeType() == EdgeType.IMPLICIT_ASSOCIATION)
            .findFirst()
            .orElseThrow();
    final SchemaEdge viewDepEdge =
        catalogGraph.edgeSet().stream()
            .filter(edge -> edge.getEdgeType() == EdgeType.VIEW_DEPENDENCY)
            .findFirst()
            .orElseThrow();
    final SchemaEdge routineDepEdge =
        catalogGraph.edgeSet().stream()
            .filter(edge -> edge.getEdgeType() == EdgeType.ROUTINE_DEPENDENCY)
            .findFirst()
            .orElseThrow();
    final SchemaEdge synonymResEdge =
        catalogGraph.edgeSet().stream()
            .filter(edge -> edge.getEdgeType() == EdgeType.SYNONYM_RESOLUTION)
            .findFirst()
            .orElseThrow();
    assertThat(
        catalogGraph.getEdgeSource(foreignKeyEdge), is(VertexUtility.createVertexId(orders)));
    assertThat(
        catalogGraph.getEdgeTarget(foreignKeyEdge), is(VertexUtility.createVertexId(customers)));
    assertThat(foreignKeyEdge.getReferenceKey(), is(foreignKey.key()));
    assertThat(importanceModel.getTableVertexIds(), hasSize(3));
    assertThat(importanceModel.getTableClusters(), hasSize(1));
    assertThat(
        orders
            .<TableImportance>getAttribute(TableImportance.class.getName())
            .importanceMetrics()
            .inDegree(),
        is(2));
    assertThat(
        orders
            .<TableImportance>getAttribute(TableImportance.class.getName())
            .importanceMetrics()
            .outDegree(),
        is(2));
    assertThat(
        orders
            .<TableImportance>getAttribute(TableImportance.class.getName())
            .importanceMetrics()
            .betweennessCentrality(),
        greaterThan(0));
    verify(refreshOrders, never()).setAttribute(anyString(), any());
    verify(customerAlias, never()).setAttribute(anyString(), any());
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            catalogGraph.addVertex(
                new DatabaseObjectVertexId(
                    new NamedObjectKey("OTHER"), SimpleDatabaseObjectType.table)));
    assertThrows(UnsupportedOperationException.class, importanceModel.getTableVertexIds()::clear);
    assertThrows(UnsupportedOperationException.class, importanceModel.getTableClusters()::clear);
  }

  @Test
  void retainsTypedObjectLookupsForCollidingNames() {
    final Table table = table("ORDERS");
    final Procedure procedure = mock(Procedure.class);
    initialize(procedure, "ORDERS");

    final Catalog catalog = catalog(List.of(table), List.<Routine>of(procedure), List.of());
    final ImportanceModel importanceModel = ImportanceModelBuilder.builder(catalog).build();

    assertThat(
        importanceModel.lookupByVertexId(VertexUtility.createVertexId(table)).orElseThrow(),
        is(table));
    assertThat(
        importanceModel.lookupByVertexId(VertexUtility.createVertexId(procedure)).orElseThrow(),
        is(procedure));
  }

  @Test
  void storesAnImportanceScoreWithinZeroToOneHundredForEveryTable() {
    final Table customers = table("CUSTOMERS");
    final Table orders = table("ORDERS");
    final ForeignKey foreignKey = mock(ForeignKey.class);
    when(foreignKey.getPrimaryKeyTable()).thenReturn(customers);
    when(foreignKey.key()).thenReturn(new NamedObjectKey("FK_ORDERS_CUSTOMERS"));
    doReturn(List.of(foreignKey)).when(orders).getImportedForeignKeys();

    final Catalog catalog = catalog(List.of(customers, orders), List.of(), List.of());
    ImportanceModelBuilder.builder(catalog).build();

    assertThat(
        customers.<TableImportance>getAttribute(TableImportance.class.getName()).importanceScore(),
        greaterThanOrEqualTo(0));
    assertThat(
        customers.<TableImportance>getAttribute(TableImportance.class.getName()).importanceScore(),
        lessThanOrEqualTo(100));
    assertThat(
        orders.<TableImportance>getAttribute(TableImportance.class.getName()).importanceScore(),
        greaterThanOrEqualTo(0));
    assertThat(
        orders.<TableImportance>getAttribute(TableImportance.class.getName()).importanceScore(),
        lessThanOrEqualTo(100));
  }
}
