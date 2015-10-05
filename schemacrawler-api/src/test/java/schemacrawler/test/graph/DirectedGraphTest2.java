package schemacrawler.test.graph;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;

import schemacrawler.test.utility.TestName;
import sf.util.graph.DirectedGraph;

public class DirectedGraphTest2
  extends GraphTestBase
{

  @Rule
  public TestName testName = new TestName();

  @Test
  public void dbcycles1()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<String>(testName
      .currentMethodFullName())
    {
      {
        addEdge("ACTIVITIES", "PRODUCTION_VARIANTS");
        addEdge("PRODUCTION_VARIANTS", "ACTIVITIES");
        addEdge("PRODUCTION_VARIANTS", "TARGETGROUPS");
        addEdge("TARGETGROUPS", "ORDERS");
        addEdge("TARGETGROUPS", "ORDER_LAYOUTS");
        addEdge("ORDERS", "ACTIVITIES");
        addEdge("ORDERS", "AD_CARRIERS");
        addEdge("ORDERS", "AD_SPACES");
        addEdge("ORDERS", "PRODUCTION_VARIANTS");
        addEdge("AD_CARRIERS", "ACTIVITIES");
        addEdge("AD_SPACES", "AD_CARRIERS");
        addEdge("ORDER_LAYOUTS", "ORDERS");
      }
    };

    assertTrue(containsCycleSimple(graph));
    assertTrue(containsCycleTarjan(graph));

  }

  @Test
  public void dbcycles2()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<String>(testName
      .currentMethodFullName())
    {
      {
        addEdge("ORDERS", "ACTIVITIES");
        addEdge("ORDERS", "AD_CARRIERS");
        addEdge("ORDERS", "AD_SPACES");
        addEdge("ORDERS", "PRODUCTION_VARIANTS");
        addEdge("AD_CARRIERS", "ACTIVITIES");
        addEdge("AD_SPACES", "AD_CARRIERS");
        addEdge("ORDER_LAYOUTS", "ORDERS");
      }
    };

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

  @Test
  public void dbcycles3()
    throws Exception
  {
    final DirectedGraph<String> graph = new DirectedGraph<String>(testName
      .currentMethodFullName())
    {
      {
        addEdge("TARGETGROUPS", "ORDERS");
        addEdge("TARGETGROUPS", "ORDER_LAYOUTS");
        addEdge("ORDERS", "ACTIVITIES");
        addEdge("ORDERS", "AD_CARRIERS");
        addEdge("ORDERS", "AD_SPACES");
        addEdge("ORDERS", "PRODUCTION_VARIANTS");
        addEdge("AD_CARRIERS", "ACTIVITIES");
        addEdge("AD_SPACES", "AD_CARRIERS");
        addEdge("ORDER_LAYOUTS", "ORDERS");
      }
    };

    assertFalse(containsCycleSimple(graph));
    assertFalse(containsCycleTarjan(graph));

  }

}
