/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;

import schemacrawler.test.utility.TestName;
import sf.util.graph.DirectedGraph;

/**
 * Tests from
 * https://github.com/danielrbradley/CycleDetection/blob/master/
 * StronglyConnectedComponentsTests/StronglyConnectedComponentTests.cs
 */
public class DirectedGraphTest5
  extends GraphTestBase
{

  @Rule
  public TestName testName = new TestName();

  // A↔B
  @Test
  public void cycle2()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addEdge("A", "B");
    graph.addEdge("B", "A");

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  // A→B
  // ↑ ↓
  // └─C
  @Test
  public void cycle3()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  // A→B
  // ↑ ↓
  // └─C-→D
  @Test
  public void cycle3WithStub()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");
    graph.addEdge("C", "D");

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  // A→B
  @Test
  public void linear2()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addEdge("A", "B");

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

  // A→B→C
  @Test
  public void linear3()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

  // A→B→D→E
  // ↑ ↓ ↑ ↓
  // └─C └─F
  @Test
  public void twoConnected3Cycles()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");

    graph.addEdge("D", "E");
    graph.addEdge("E", "F");
    graph.addEdge("F", "D");

    graph.addEdge("B", "D");

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  // A→B D→E
  // ↑ ↓ ↑ ↓
  // └─C └─F
  @Test
  public void twoIsolated3Cycles()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addEdge("A", "B");
    graph.addEdge("B", "C");
    graph.addEdge("C", "A");

    graph.addEdge("D", "E");
    graph.addEdge("E", "F");
    graph.addEdge("F", "D");

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

}
