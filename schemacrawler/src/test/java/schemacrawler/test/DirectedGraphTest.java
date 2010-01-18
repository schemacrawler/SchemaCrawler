/*
 * SchemaCrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.test;


import java.util.Arrays;

import static org.junit.Assert.*;

import org.junit.Test;
import sf.util.DirectedGraph;
import sf.util.GraphException;

public class DirectedGraphTest
{

  @Test
  public void cycles()
    throws Exception
  {
    final DirectedGraph<String> graph = makeGraph();

    assertFalse(graph.containsCycle());

    graph.addDirectedEdge("C", "A");

    assertTrue(graph.containsCycle());
  }

  @Test
  public void topologicalSort()
    throws Exception
  {
    for (int i = 0; i < 8; i++)
    {
      final DirectedGraph<String> graph = makeGraph();

      assertEquals("Test run #" + (i + 1), Arrays.asList("E", "A", "D", "B", "C"), graph.topologicalSort());
    }
  }

  @Test(expected = GraphException.class)
  public void topologicalSortCyclical()
    throws Exception
  {
    final DirectedGraph<String> graph = makeGraph();
    graph.addDirectedEdge("C", "A");

    assertEquals(Arrays.asList("E", "A", "D", "B", "C"), graph.topologicalSort());
  }

  private DirectedGraph<String> makeGraph()
  {

    final DirectedGraph<String> graph = new DirectedGraph<String>()
    {{
        addDirectedEdge("A", "B");
        addDirectedEdge("B", "C");
        addDirectedEdge("A", "D");
        addVertex("E");
      }};

    return graph;
  }

}
