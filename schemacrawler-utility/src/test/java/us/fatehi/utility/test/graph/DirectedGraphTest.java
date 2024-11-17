/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package us.fatehi.utility.test.graph;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import nl.jqno.equalsverifier.EqualsVerifier;
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
          "Test run #" + (i + 1),
          topologicalSort(graph),
          is(Arrays.asList("A", "E", "B", "D", "C")));
    }
  }

  @Test
  public void topologicalSortCyclical() throws Exception {
    final DirectedGraph<String> graph = makeGraph();
    graph.addEdge("C", "A");

    assertThrows(
        GraphException.class,
        () -> topologicalSort(graph),
        () -> Arrays.asList("E", "A", "D", "B", "C").toString());
  }

  @Test
  public void toStringTest() throws Exception {
    final DirectedGraph<String> graph = makeGraph();

    assertThat(
        graph.toString(),
        is(
            "digraph {\n  [label=\"graph_name\"]\n  A;\n  B;\n  C;\n  D;\n  E;\n  A -> B;\n  B -> C;\n  A -> D;\n}\n"));
    assertThat(graph.getName(), is("graph_name"));
  }

  @Test
  public void vertex() {
    EqualsVerifier.forClass(Vertex.class).withIgnoredFields("attributes").verify();
  }

  private DirectedGraph<String> makeGraph() {

    final DirectedGraph<String> graph =
        new DirectedGraph<String>("graph_name") {
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
