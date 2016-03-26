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
 * Test cases from
 * http://codereview.stackexchange.com/questions/38063/check-if-directed
 * -graph-contains-a-cycle
 */
public class DirectedGraphTest3
  extends GraphTestBase
{

  @Rule
  public TestName testName = new TestName();

  @Test
  public void test1()
    throws Exception
  {
    final DirectedGraph<Integer> graph = new DirectedGraph<Integer>(testName
      .currentMethodFullName())
    {
      {
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(1, 3);
      }
    };

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

  @Test
  public void test2()
    throws Exception
  {
    final DirectedGraph<Integer> graph = new DirectedGraph<Integer>(testName
      .currentMethodFullName())
    {
      {
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(3, 1);
      }
    };

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  @Test
  public void test3()
    throws Exception
  {
    final DirectedGraph<Integer> graph = new DirectedGraph<Integer>(testName
      .currentMethodFullName())
    {
      {
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(2, 4);
        addEdge(3, 4);
        addEdge(4, 5);
      }
    };

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

  @Test
  public void test4()
    throws Exception
  {
    final DirectedGraph<Integer> graph = new DirectedGraph<Integer>(testName
      .currentMethodFullName())
    {
      {
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(2, 4);
        addEdge(3, 4);
        addEdge(4, 5);
        addEdge(5, 2);
      }
    };

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  @Test
  public void test5()
    throws Exception
  {
    final DirectedGraph<Integer> graph = new DirectedGraph<Integer>(testName
      .currentMethodFullName())
    {
      {
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(3, 1);
        addEdge(10, 11);
      }
    };

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

}
