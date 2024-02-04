/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.chatgpt.embeddings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;

public class EmbeddedTableTest {

  private LightTable table;
  private EmbeddedTable embeddedTable;

  @BeforeEach
  public void setUp() {
    table = new LightTable(new SchemaReference("schema_name", ""), "table_name");
    table.addColumn("column_name");

    embeddedTable = new EmbeddedTable(table);
  }

  @Test
  public void testAccessors() {
    assertThat(embeddedTable.getFullName(), is(table.getFullName()));
    assertThat(embeddedTable.getName(), is(table.getName()));
    assertThat(embeddedTable.getSchema(), is(embeddedTable.getSchema()));
    assertThat(embeddedTable.key(), is(table.key()));
    assertThat(embeddedTable.toString(), is(table.getFullName()));
    assertThat(
        embeddedTable.toJson(),
        is(
            "{\"table\":\"table_name\",\"columns\":[{\"column\":\"column_name\",\"type\":\"INTEGER\"}],\"schema\":\"schema_name\"}"));
  }

  @Test
  public void testCompare() {
    assertThat(embeddedTable.compareTo(table), is(0));
    assertThat(
        embeddedTable.compareTo(new LightTable(new SchemaReference(), "zz_table")),
        is(lessThan(0)));
  }

  @Test
  public void testEmbedding() {
    assertThat(embeddedTable.hasEmbedding(), is(false));
    assertNotNull(embeddedTable.getEmbedding());
    assertThat(embeddedTable.getEmbedding().isEmpty(), is(true));

    final List<Double> embeddings1 = Collections.singletonList(0.25);
    embeddedTable.setEmbedding(embeddings1);

    assertThat(embeddedTable.hasEmbedding(), is(true));
    assertThat(embeddedTable.getEmbedding(), is(embeddings1));

    final List<Double> embeddings2 = Collections.singletonList(0.75);
    embeddedTable.setEmbedding(embeddings2);

    assertThat(embeddedTable.hasEmbedding(), is(true));
    assertThat(embeddedTable.getEmbedding(), is(embeddings2));

    embeddedTable.setEmbedding(null);

    assertThat(embeddedTable.hasEmbedding(), is(false));
    assertThat(embeddedTable.getEmbedding().isEmpty(), is(true));
  }
}
