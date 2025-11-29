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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.graph.DirectedEdge;
import us.fatehi.utility.graph.DirectedGraph;
import us.fatehi.utility.graph.GraphException;
import us.fatehi.utility.graph.Vertex;

public class DirectedGraphTest extends GraphTestBase {

  @Test
  public void cycles() throws Exception {
    final DirectedGraph<String> graph = makeGraph();
    graph.addEdge("C", "A");

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  @Test
  public void directedEdge() {
    EqualsVerifier.forClass(DirectedEdge.class).verify();
  }

  @Test
  public void noCycles() throws Exception {
    final DirectedGraph<String> graph = makeGraph();

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));
  }

  @Test
  public void smallCycle() throws Exception {
    final DirectedGraph<String> graph = new DirectedGraph<>("");

    assertThat(containsCycleSimple(graph), is(false));
    assertThat(containsCycleTarjan(graph), is(false));

    graph.addEdge("A", "B");
    graph.addEdge("B", "A");

    assertThat(containsCycleSimple(graph), is(true));
    assertThat(containsCycleTarjan(graph), is(true));
  }

  @Test
  public void topologicalSort() throws Exception {
    for (int i = 0; i < 8; i++) {
      final DirectedGraph<String> graph = makeGraph();

      assertThat(
          "Test run #" + (i + 1), topologicalSort(graph), is(List.of("A", "E", "B", "D", "C")));
    }
  }

  @Test
  public void topologicalSortCyclical() throws Exception {
    final DirectedGraph<String> graph = makeGraph();
    graph.addEdge("C", "A");

    assertThrows(
        GraphException.class,
        () -> topologicalSort(graph),
        () -> List.of("E", "A", "D", "B", "C").toString());
  }

  @Test
  public void toStringTest() throws Exception {
    final DirectedGraph<String> graph = makeGraph();
    assertThat(
        graph.toString(),
        is(
            """
            digraph {
              [label="graph_name"]
              A;
              B;
              C;
              D;
              E;
              A -> B;
              B -> C;
              A -> D;
            }
            """));
    assertThat(graph.getName(), is("graph_name"));
  }

  @Test
  public void vertex() {
    EqualsVerifier.forClass(Vertex.class).withIgnoredFields("attributes").verify();
  }

  private DirectedGraph<String> makeGraph() {

    final DirectedGraph<String> graph =
        new DirectedGraph<>("graph_name") {
          {
            addEdge("A", "B");
            addEdge("B", "C");
            addEdge("A", "D");
            addVertex("E");
          }
        };

    return graph;
  }
}
