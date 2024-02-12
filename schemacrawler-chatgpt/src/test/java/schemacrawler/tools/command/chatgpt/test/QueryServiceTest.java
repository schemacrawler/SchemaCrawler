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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.chatgpt.embeddings.QueryService;
import schemacrawler.tools.command.chatgpt.test.utility.ChatGptTestUtility;

public class QueryServiceTest {

  private OpenAiService openAiService;
  private QueryService queryService;
  private Table table;

  @BeforeEach
  public void setUp() {
    final LightTable table = new LightTable(new SchemaReference("schema_name", ""), "table_name");
    table.addColumn("column_name");
    this.table = table;

    openAiService =
        ChatGptTestUtility.setUpMockOpenAiService(new ArrayList<>(Collections.singletonList(0.5)));
    queryService = new QueryService(openAiService);
  }

  @Test
  public void testQuery() {
    final String prompt = "test prompt";
    queryService.addTables(Arrays.asList(table));

    final Collection<ChatMessage> messages = queryService.query(prompt);

    assertThat(messages, hasSize(2));
  }
}
