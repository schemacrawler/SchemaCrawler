/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.chatgpt.embeddings.EmbeddedTable;
import schemacrawler.tools.command.chatgpt.embeddings.TableSimilarity;

public class TableSimilarityTest {

  private EmbeddedTable mockTable;
  private TableSimilarity tableSimilarity;

  @BeforeEach
  public void setUp() {
    mockTable = mock(EmbeddedTable.class);
    tableSimilarity = new TableSimilarity(mockTable, 0.75);
  }

  @Test
  public void testCompareTo() {
    TableSimilarity other = new TableSimilarity(mockTable, 0.5);
    assertThat(tableSimilarity.compareTo(other), lessThan(0));

    other = new TableSimilarity(mockTable, 0.75);
    assertThat(tableSimilarity.compareTo(other), is(0));

    other = new TableSimilarity(mockTable, 1.0);
    assertThat(tableSimilarity.compareTo(other), greaterThan(0));
  }

  @Test
  public void testCompareToWithNull() {
    assertThrows(NullPointerException.class, () -> tableSimilarity.compareTo(null));
  }

  @Test
  public void testGetSimilarity() {
    assertThat(tableSimilarity.getSimilarity(), is(0.75));
  }

  @Test
  public void testGetTable() {
    assertThat(tableSimilarity.getTable(), is(mockTable));
  }
}
