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
 * Test cases from http://codereview.stackexchange.com/questions/38063/check-if-directed
 * -graph-contains-a-cycle
 */
public class DirectedGraphTest3 extends GraphTestBase {

  @Test
  public void test1() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addEdge(1, 2);
            addEdge(2, 3);
            addEdge(1, 3);
          }
        };

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  @Test
  public void test2() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addEdge(1, 2);
            addEdge(2, 3);
            addEdge(3, 1);
          }
        };

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  @Test
  public void test3() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addEdge(1, 2);
            addEdge(2, 3);
            addEdge(2, 4);
            addEdge(3, 4);
            addEdge(4, 5);
          }
        };

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  @Test
  public void test4() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addEdge(1, 2);
            addEdge(2, 3);
            addEdge(2, 4);
            addEdge(3, 4);
            addEdge(4, 5);
            addEdge(5, 2);
          }
        };

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  @Test
  public void test5() throws Exception {
    final DirectedGraph<Integer> graph =
        new DirectedGraph<Integer>("") {
          {
            addEdge(1, 2);
            addEdge(2, 3);
            addEdge(3, 1);
            addEdge(10, 11);
          }
        };

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }
}
