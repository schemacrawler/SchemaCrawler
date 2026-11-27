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
import static schemacrawler.test.utility.crawl.LightCatalogUtility.lightCatalog;

import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.importance.model.TableCluster;
import schemacrawler.importance.model.implementation.ImportanceModelBuilder;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.test.utility.crawl.LightForeignKey;
import schemacrawler.test.utility.crawl.LightTable;

class TableClusterDetectionServiceTest {

  private static Catalog catalog(final List<Table> tables) {
    return lightCatalog(tables, List.of(), List.of(), List.of());
  }

  private static LightTable table(final String name) {
    return new LightTable(name);
  }

  @Test
  void detectsAndAnchorsClustersForConnectedTables() {
    final LightTable customers = table("CUSTOMERS");
    final LightTable orders = table("ORDERS");
    final LightTable orderItems = table("ORDER_ITEMS");

    orders.addImportedForeignKey(new LightForeignKey("FK_ORDERS_CUSTOMERS", orders, customers));
    orderItems.addImportedForeignKey(new LightForeignKey("FK_ITEMS_ORDERS", orderItems, orders));

    final Catalog catalog = catalog(List.of(customers, orders, orderItems));
    final ImportanceModel graphModel = ImportanceModelBuilder.builder(catalog).build();
    final List<TableCluster> tableClusters = graphModel.getTableClusters();

    assertThat(tableClusters.size(), greaterThan(0));
    for (final TableCluster tableCluster : tableClusters) {
      assertThat(tableCluster.id(), notNullValue());
      assertThat(tableCluster.anchorVertexId(), notNullValue());
      assertThat(tableCluster.memberVertexIds().contains(tableCluster.anchorVertexId()), is(true));
    }
    assertThat(graphModel.getTableClusters(), is(tableClusters));
  }

  @Test
  void returnsEmptyListForEmptyCatalog() {
    final Catalog catalog = catalog(List.of());
    final ImportanceModel graphModel = ImportanceModelBuilder.builder(catalog).build();
    final List<TableCluster> tableClusters = graphModel.getTableClusters();

    assertThat(tableClusters, hasSize(0));
    assertThat(graphModel.getTableClusters(), hasSize(0));
  }
}
