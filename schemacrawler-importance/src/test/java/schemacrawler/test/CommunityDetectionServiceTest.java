/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.SchemaCommunity;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.importance.model.builder.NodeIdFactory;
import schemacrawler.importance.model.builder.SchemaGraphModelBuilder;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;

class CommunityDetectionServiceTest {

  private static Catalog catalog(final List<Table> tables) {
    final Catalog catalog = mock(Catalog.class);
    when(catalog.getTables()).thenReturn(tables);
    when(catalog.getRoutines()).thenReturn(List.of());
    when(catalog.getSynonyms()).thenReturn(List.of());
    return catalog;
  }

  private static void initialize(final Table table, final String name) {
    when(table.key()).thenReturn(new NamedObjectKey("PUBLIC", name));
    when(table.getFullName()).thenReturn("PUBLIC." + name);
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
    return table;
  }

  @Test
  void returnsEmptyListForEmptyCatalog() {
    final Catalog catalog = catalog(List.of());
    final SchemaGraphModel graphModel = SchemaGraphModelBuilder.builder(catalog).build();
    final List<SchemaCommunity> communities = graphModel.getCommunities();

    assertThat(communities, hasSize(0));
    assertThat(graphModel.getCommunities(), hasSize(0));
  }

  @Test
  void detectsCommunitiesForSingleTable() {
    final Table customers = table("CUSTOMERS");
    final AtomicReference<TableImportance> customersImportance = new AtomicReference<>();
    doAnswer(
            invocation -> {
              customersImportance.set(invocation.getArgument(1));
              return null;
            })
        .when(customers)
        .setAttribute(eq(TableImportance.class.getName()), any());
    doAnswer(invocation -> customersImportance.get())
        .when(customers)
        .getAttribute(TableImportance.class.getName());

    final Catalog catalog = catalog(List.of(customers));
    final SchemaGraphModel graphModel = SchemaGraphModelBuilder.builder(catalog).build();
    final List<SchemaCommunity> communities = graphModel.getCommunities();

    assertThat(communities, hasSize(1));
    final SchemaCommunity community = communities.get(0);
    assertThat(community.id(), notNullValue());
    assertThat(community.anchorNode().key(), is(NodeIdFactory.create(customers).key()));
    assertThat(community.memberNodes(), hasSize(1));
    assertThat(community.memberNodes().get(0), is(community.anchorNode()));
    assertThat(graphModel.getCommunities(), is(communities));
  }

  @Test
  void detectsAndAnchorsCommunitiesForConnectedTables() {
    final Table customers = table("CUSTOMERS");
    final Table orders = table("ORDERS");
    final Table orderItems = table("ORDER_ITEMS");

    final AtomicReference<TableImportance> customersImportance = new AtomicReference<>();
    final AtomicReference<TableImportance> ordersImportance = new AtomicReference<>();
    final AtomicReference<TableImportance> orderItemsImportance = new AtomicReference<>();

    doAnswer(
            i -> {
              customersImportance.set(i.getArgument(1));
              return null;
            })
        .when(customers)
        .setAttribute(eq(TableImportance.class.getName()), any());
    doAnswer(i -> customersImportance.get())
        .when(customers)
        .getAttribute(TableImportance.class.getName());

    doAnswer(
            i -> {
              ordersImportance.set(i.getArgument(1));
              return null;
            })
        .when(orders)
        .setAttribute(eq(TableImportance.class.getName()), any());
    doAnswer(i -> ordersImportance.get())
        .when(orders)
        .getAttribute(TableImportance.class.getName());

    doAnswer(
            i -> {
              orderItemsImportance.set(i.getArgument(1));
              return null;
            })
        .when(orderItems)
        .setAttribute(eq(TableImportance.class.getName()), any());
    doAnswer(i -> orderItemsImportance.get())
        .when(orderItems)
        .getAttribute(TableImportance.class.getName());

    final ForeignKey fkOrdersCustomers = mock(ForeignKey.class);
    when(fkOrdersCustomers.getPrimaryKeyTable()).thenReturn(customers);
    when(fkOrdersCustomers.key()).thenReturn(new NamedObjectKey("FK_ORDERS_CUSTOMERS"));
    when(orders.getImportedForeignKeys()).thenReturn(List.of(fkOrdersCustomers));

    final ForeignKey fkItemsOrders = mock(ForeignKey.class);
    when(fkItemsOrders.getPrimaryKeyTable()).thenReturn(orders);
    when(fkItemsOrders.key()).thenReturn(new NamedObjectKey("FK_ITEMS_ORDERS"));
    when(orderItems.getImportedForeignKeys()).thenReturn(List.of(fkItemsOrders));

    final Catalog catalog = catalog(List.of(customers, orders, orderItems));
    final SchemaGraphModel graphModel = SchemaGraphModelBuilder.builder(catalog).build();
    final List<SchemaCommunity> communities = graphModel.getCommunities();

    assertThat(communities.size(), greaterThan(0));
    for (final SchemaCommunity community : communities) {
      assertThat(community.id(), notNullValue());
      assertThat(community.anchorNode(), notNullValue());
      assertThat(community.memberNodes().contains(community.anchorNode()), is(true));
    }
    assertThat(graphModel.getCommunities(), is(communities));
  }
}
