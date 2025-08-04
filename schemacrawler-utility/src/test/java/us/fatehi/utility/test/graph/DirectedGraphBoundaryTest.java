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
 * Tests from https://github.com/danielrbradley/CycleDetection/blob/master/
 * StronglyConnectedComponentsTests/StronglyConnectedComponentTests.cs
 */
public class DirectedGraphBoundaryTest extends GraphTestBase {

  @Test
  public void emptyGraph() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  @Test
  public void selfLoop() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addEdge("A", "A");

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  @Test
  public void singleVertex() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addVertex("A");

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }
}
