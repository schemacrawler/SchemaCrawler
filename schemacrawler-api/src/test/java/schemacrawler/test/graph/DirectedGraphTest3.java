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
