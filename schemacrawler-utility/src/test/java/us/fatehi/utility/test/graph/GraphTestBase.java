/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.test.graph;

import java.util.Collection;
import java.util.List;

import us.fatehi.utility.graph.DirectedGraph;
import us.fatehi.utility.graph.GraphException;
import us.fatehi.utility.graph.SimpleCycleDetector;
import us.fatehi.utility.graph.SimpleTopologicalSort;
import us.fatehi.utility.graph.TarjanStronglyConnectedComponentFinder;

abstract class GraphTestBase {

  private final boolean DEBUG = false;

  protected <T extends Comparable<? super T>> boolean containsCycleSimple(
      final DirectedGraph<T> graph) {
    final boolean containsCycle = new SimpleCycleDetector<>(graph).containsCycle();

    if (DEBUG && containsCycle) {
      System.out.println(graph);
    }

    return containsCycle;
  }

  protected <T extends Comparable<? super T>> boolean containsCycleTarjan(
      final DirectedGraph<T> graph) {
    final Collection<List<T>> sccs =
        new TarjanStronglyConnectedComponentFinder<>(graph).detectCycles();

    if (DEBUG) {
      System.out.print(graph.getName());
      System.out.println(sccs);
    }

    return !sccs.isEmpty();
  }

  protected <T extends Comparable<? super T>> List<T> topologicalSort(final DirectedGraph<T> graph)
      throws GraphException {
    return new SimpleTopologicalSort<>(graph).topologicalSort();
  }
}
