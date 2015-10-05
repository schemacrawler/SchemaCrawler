package schemacrawler.test.graph;


import java.util.Collection;
import java.util.List;

import sf.util.graph.DirectedGraph;
import sf.util.graph.GraphException;
import sf.util.graph.SimpleCycleDetector;
import sf.util.graph.SimpleTopologicalSort;
import sf.util.graph.TarjanStronglyConnectedComponentFinder;

abstract class GraphTestBase
{

  private final boolean DEBUG = false;

  protected <T extends Comparable<? super T>> boolean
    containsCycleSimple(final DirectedGraph<T> graph)
  {
    final boolean containsCycle = new SimpleCycleDetector<>(graph)
      .containsCycle();

    if (DEBUG && containsCycle)
    {
      System.out.println(graph);
    }

    return containsCycle;
  }

  protected <T extends Comparable<? super T>> boolean
    containsCycleTarjan(final DirectedGraph<T> graph)
  {
    final Collection<List<T>> sccs = new TarjanStronglyConnectedComponentFinder<T>(graph)
      .detectCycles();

    if (DEBUG)
    {
      System.out.print(graph.getName());
      System.out.println(sccs);
    }

    return !sccs.isEmpty();
  }

  protected <T extends Comparable<? super T>> List<T>
    topologicalSort(final DirectedGraph<T> graph)
      throws GraphException
  {
    return new SimpleTopologicalSort<>(graph).topologicalSort();
  }

}
