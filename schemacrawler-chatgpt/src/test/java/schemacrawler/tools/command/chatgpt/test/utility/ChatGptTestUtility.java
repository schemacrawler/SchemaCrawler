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

package schemacrawler.tools.command.chatgpt.test.utility;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class ChatGptTestUtility {

  public static OpenAiService setUpMockOpenAiService(final List<Double> expectedEmbedding) {
    final OpenAiService openAiService = mock(OpenAiService.class);
    final Embedding embedding = mock(Embedding.class);
    when(embedding.getEmbedding()).thenReturn(expectedEmbedding);
    final EmbeddingResult embeddingResult = mock(EmbeddingResult.class);
    when(embeddingResult.getData()).thenReturn(Collections.singletonList(embedding));
    when(openAiService.createEmbeddings(any(EmbeddingRequest.class))).thenReturn(embeddingResult);

    return openAiService;
  }

  private ChatGptTestUtility() {
    // Prevent instantiation
  }
}
