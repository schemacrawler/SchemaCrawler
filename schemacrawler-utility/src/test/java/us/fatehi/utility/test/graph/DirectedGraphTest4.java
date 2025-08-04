/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.graph;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import us.fatehi.utility.graph.DirectedGraph;

/**
 * Exercises from https://algocoding.wordpress.com/2015/04/02/detecting-cycles-in-a-
 * directed-graph-with-dfs-python/
 */
public class DirectedGraphTest4 extends GraphTestBase {

  @Test
  public void test1() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addEdge(0, 1);
            addEdge(0, 2);
            addEdge(2, 3);
            addEdge(3, 4);
            addEdge(4, 2);
          }
        };

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  @Test
  public void test2() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addVertex(0);
            addVertex(1);
            addVertex(2);
            addVertex(3);
          }
        };

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  @Test
  public void test3() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addVertex(0);
            addEdge(1, 2);
            addEdge(3, 4);
            addEdge(4, 5);
            addEdge(5, 3);
          }
        };

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  @Test
  public void test4() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addEdge(0, 1);
            addEdge(0, 2);
            addEdge(1, 3);
            addEdge(1, 4);
            addEdge(5, 6);
            addEdge(5, 7);
          }
        };

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }
}
