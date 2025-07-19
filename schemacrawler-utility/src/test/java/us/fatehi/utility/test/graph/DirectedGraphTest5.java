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
public class DirectedGraphTest5 extends GraphTestBase {

  // A↔B
  @Test
  public void cycle2() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addEdge("A", "B");
    graph.addEdge("B", "A");

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  // A→B
  // ↑ ↓
  // └─C
  @Test
  public void cycle3() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  // A→B
  // ↑ ↓
  // └─C-→D
  @Test
  public void cycle3WithStub() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");
    graph.addEdge("C", "D");

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  // A→B
  @Test
  public void linear2() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addEdge("A", "B");

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  // A→B→C
  @Test
  public void linear3() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  // A→B→D→E
  // ↑ ↓ ↑ ↓
  // └─C └─F
  @Test
  public void twoConnected3Cycles() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");

    graph.addEdge("D", "E");
    graph.addEdge("E", "F");
    graph.addEdge("F", "D");

    graph.addEdge("B", "D");

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  // A→B D→E
  // ↑ ↓ ↑ ↓
  // └─C └─F
  @Test
  public void twoIsolated3Cycles() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");

    graph.addEdge("D", "E");
    graph.addEdge("E", "F");
    graph.addEdge("F", "D");

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }
}
