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


import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;

import schemacrawler.test.utility.TestName;
import sf.util.graph.DirectedGraph;

/**
 * Tests from
 * https://github.com/danielrbradley/CycleDetection/blob/master/
 * StronglyConnectedComponentsTests/StronglyConnectedComponentTests.cs
 */
public class DirectedGraphBoundaryTest
  extends GraphTestBase
{

  @Rule
  public TestName testName = new TestName();

  @Test
  public void emptyGraph()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

  @Test
  public void selfLoop()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addEdge("A", "A");

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

  @Test
  public void singleVertex()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<>(testName
      .currentMethodFullName());
    graph.addVertex("A");

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

}
