/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.EdgeType;
import schemacrawler.importance.model.SchemaEdge;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightProcedure;
import schemacrawler.test.utility.crawl.LightTable;

class SchemaGraphAssemblyTest {

  @Test
  void tableSubgraphRetainsParallelTableEdgesAndExcludesNonTableEdges() {
    final Table orders = new LightTable("ORDERS");
    final Table customers = new LightTable("CUSTOMERS");
    final Procedure refreshOrders =
        new LightProcedure(new SchemaReference(null, "PUBLIC"), "REFRESH_ORDERS");

    final SchemaGraphAssembly assembly = new SchemaGraphAssembly();
    assembly.addNode(orders);
    assembly.addNode(customers);
    assembly.addNode(refreshOrders);

    final SchemaEdge foreignKey = new SchemaEdge(EdgeType.FOREIGN_KEY, null);
    final SchemaEdge impliedAssociation = new SchemaEdge(EdgeType.IMPLICIT_ASSOCIATION, null);
    final SchemaEdge routineDependency = new SchemaEdge(EdgeType.ROUTINE_DEPENDENCY, null);
    assembly.addEdge(orders, customers, foreignKey);
    assembly.addEdge(orders, customers, impliedAssociation);
    assembly.addEdge(refreshOrders, orders, routineDependency);

    final Graph<DatabaseObjectVertexId, SchemaEdge> tableSubgraph = assembly.tableSubgraph();

    assertThat(tableSubgraph.vertexSet(), hasSize(2));
    assertThat(tableSubgraph.edgeSet(), containsInAnyOrder(foreignKey, impliedAssociation));
  }
}
