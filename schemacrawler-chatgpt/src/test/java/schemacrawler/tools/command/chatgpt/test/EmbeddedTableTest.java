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

package schemacrawler.tools.command.chatgpt.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.tools.command.chatgpt.embeddings.EmbeddedTable;

public class EmbeddedTableTest {

  private Table mockTable;
  private EmbeddedTable embeddedTable;

  @BeforeEach
  public void setUp() {
    mockTable = mock(Table.class);
    when(mockTable.getFullName()).thenReturn("full_name");
    when(mockTable.getName()).thenReturn("name");
    final SchemaReference schema = new SchemaReference("schema_name", "");
    when(mockTable.getSchema()).thenReturn(schema);
    when(mockTable.key()).thenReturn(new NamedObjectKey("name"));
    when(mockTable.getColumns()).thenReturn(Collections.emptyList());

    embeddedTable = new EmbeddedTable(mockTable);
  }

  @Test
  public void testAccessors() {
    assertEquals("full_name", embeddedTable.getFullName());
    assertEquals("name", embeddedTable.getName());
    assertEquals("schema_name", embeddedTable.getSchema().getFullName());
    assertEquals(new NamedObjectKey("name"), embeddedTable.key());
    assertEquals("full_name", embeddedTable.toString());
  }

  @Test
  public void testEmbedding() {
    final List<Double> embedding = embeddedTable.getEmbedding();
    assertNotNull(embedding);
    assertTrue(embedding.isEmpty());
  }
}
