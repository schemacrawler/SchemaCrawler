/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.graph;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;

import schemacrawler.test.utility.TestName;
import sf.util.graph.DirectedGraph;
import sf.util.graph.GraphException;

public class DirectedGraphTest
  extends GraphTestBase
{

  @Rule
  public TestName testName = new TestName();

  @Test
  public void cycles()
    throws Exception
  {
    final DirectedGraph<String> graph = makeGraph();
    graph.addEdge("C", "A");

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  @Test
  public void noCycles()
    throws Exception
  {
    final DirectedGraph<String> graph = makeGraph();

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

  @Test
  public void smallCycle()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

    graph.addEdge("A", "B");
    graph.addEdge("B", "A");

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  @Test
  public void topologicalSort()
    throws Exception
  {
    for (int i = 0; i < 8; i++)
    {
      final DirectedGraph<String> graph = makeGraph();

      assertEquals("Test run #" + (i + 1),
                   "[A, E, B, D, C]",
                   topologicalSort(graph).toString());
    }
  }

  @Test(expected = GraphException.class)
  public void topologicalSortCyclical()
    throws Exception
  {
    final DirectedGraph<String> graph = makeGraph();
    graph.addEdge("C", "A");

    assertEquals(Arrays.asList("E", "A", "D", "B", "C"),
                 topologicalSort(graph));
  }

  private DirectedGraph<String> makeGraph()
  {

    final DirectedGraph<String> graph = new DirectedGraph<String>(testName
      .currentMethodFullName())
    {
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
