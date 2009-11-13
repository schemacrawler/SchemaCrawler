/*
 * SchemaCrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

import schemacrawler.utility.Graph;
import schemacrawler.utility.TestDatabase;

public class GraphTest
{

  private static final Logger LOGGER = Logger.getLogger(GraphTest.class
    .getName());

  private static TestDatabase testUtility = new TestDatabase();

  @Test
  public void cycles()
    throws Exception
  {

    final Graph<String> graph = new Graph<String>();
    graph.addDirectedEdge("A", "B");
    graph.addDirectedEdge("B", "C");
    graph.addDirectedEdge("A", "D");
    graph.addVertex("E");

    assertFalse(graph.containsCycle());

    graph.addDirectedEdge("C", "A");

    assertTrue(graph.containsCycle());

  }

}
