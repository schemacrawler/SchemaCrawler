/*
 * ======================================================================== SchemaCrawler
 * http://www.schemacrawler.com Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>. All
 * rights reserved. ------------------------------------------------------------------------
 *
 * SchemaCrawler is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * SchemaCrawler and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0, GNU General Public License v3 or GNU Lesser General Public License v3.
 *
 * You may elect to redistribute this code under any of these licenses.
 *
 * The Eclipse Public License is available at: http://www.eclipse.org/legal/epl-v10.html
 *
 * The GNU General Public License v3 and the GNU Lesser General Public License v3 are available at:
 * http://www.gnu.org/licenses/
 *
 * ========================================================================
 */

package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.chatgpt.embeddings.EmbeddedTable;
import schemacrawler.tools.command.chatgpt.embeddings.EmbeddingService;
import schemacrawler.tools.command.chatgpt.embeddings.TableEmbeddingService;

public class TableEmbeddingServiceTest {

  private EmbeddingService mockService;
  private TableEmbeddingService tableEmbeddingService;
  private Table table;

  @BeforeEach
  public void setUp() {
    final LightTable table = new LightTable(new SchemaReference("schema_name", ""), "table_name");
    table.addColumn("column_name");
    this.table = table;

    mockService = mock(EmbeddingService.class);
    tableEmbeddingService = new TableEmbeddingService(mockService);
  }

  @Test
  public void testGetEmbeddedTable() {

    when(mockService.embed(anyString())).thenReturn(Arrays.asList(1.0, 2.0, 3.0));

    final EmbeddedTable result = tableEmbeddingService.getEmbeddedTable(table);

    assertThat(result, is(notNullValue()));
    assertThat(result.getEmbedding(), contains(1.0, 2.0, 3.0));
  }

  @Test
  public void testGetEmbeddedTableWithNullTable() {
    assertThrows(NullPointerException.class, () -> tableEmbeddingService.getEmbeddedTable(null));
  }
}
