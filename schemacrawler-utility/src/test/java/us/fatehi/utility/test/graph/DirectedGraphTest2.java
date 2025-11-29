/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.graph;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import us.fatehi.utility.graph.DirectedGraph;

public class DirectedGraphTest2 extends GraphTestBase {

  @Test
  public void dbcycles1() throws Exception {
    final DirectedGraph<String> graph =
        new DirectedGraph<String>("") {
          {
            addEdge("ACTIVITIES", "PRODUCTION_VARIANTS");
            addEdge("PRODUCTION_VARIANTS", "ACTIVITIES");
            addEdge("PRODUCTION_VARIANTS", "TARGETGROUPS");
            addEdge("TARGETGROUPS", "ORDERS");
            addEdge("TARGETGROUPS", "ORDER_LAYOUTS");
            addEdge("ORDERS", "ACTIVITIES");
            addEdge("ORDERS", "AD_CARRIERS");
            addEdge("ORDERS", "AD_SPACES");
            addEdge("ORDERS", "PRODUCTION_VARIANTS");
            addEdge("AD_CARRIERS", "ACTIVITIES");
            addEdge("AD_SPACES", "AD_CARRIERS");
            addEdge("ORDER_LAYOUTS", "ORDERS");
          }
        };

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  @Test
  public void dbcycles2() throws Exception {
    final DirectedGraph<String> graph =
        new DirectedGraph<String>("") {
          {
            addEdge("ORDERS", "ACTIVITIES");
            addEdge("ORDERS", "AD_CARRIERS");
            addEdge("ORDERS", "AD_SPACES");
            addEdge("ORDERS", "PRODUCTION_VARIANTS");
            addEdge("AD_CARRIERS", "ACTIVITIES");
            addEdge("AD_SPACES", "AD_CARRIERS");
            addEdge("ORDER_LAYOUTS", "ORDERS");
          }
        };

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  @Test
  public void dbcycles3() throws Exception {
    final DirectedGraph<String> graph =
        new DirectedGraph<String>("") {
          {
            addEdge("TARGETGROUPS", "ORDERS");
            addEdge("TARGETGROUPS", "ORDER_LAYOUTS");
            addEdge("ORDERS", "ACTIVITIES");
            addEdge("ORDERS", "AD_CARRIERS");
            addEdge("ORDERS", "AD_SPACES");
            addEdge("ORDERS", "PRODUCTION_VARIANTS");
            addEdge("AD_CARRIERS", "ACTIVITIES");
            addEdge("AD_SPACES", "AD_CARRIERS");
            addEdge("ORDER_LAYOUTS", "ORDERS");
          }
        };

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }
}
