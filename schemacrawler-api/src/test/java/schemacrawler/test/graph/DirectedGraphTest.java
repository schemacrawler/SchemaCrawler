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
