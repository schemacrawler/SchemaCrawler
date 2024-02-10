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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;
import com.theokanning.openai.embedding.EmbeddingResult;
import static java.util.Objects.requireNonNull;

public final class TextEmbedding {

  private final String text;
  private final long tokenCount;
  private final ListRealVector embeddingVector;

  public TextEmbedding(final String text) {
    this.text = requireNonNull(text, "No text provided");
    tokenCount = 0;
    embeddingVector = new ListRealVector();
  }

  public TextEmbedding(final String text, final EmbeddingResult embeddingResult) {
    requireNonNull(embeddingResult, "No embedding result provided");

    final List<com.theokanning.openai.embedding.Embedding> embeddings = embeddingResult.getData();
    final List<Double> embedding;
    if ((embeddings != null) && (embeddings.size() == 1)) {
      embedding = embeddings.get(0).getEmbedding();
    } else {
      embedding = new ArrayList<>();
    }

    this.text = requireNonNull(text, "No text provided");
    tokenCount = embeddingResult.getUsage().getPromptTokens();
    embeddingVector = new ListRealVector(embedding);
  }

  public String getText() {
    return text;
  }

  public List<Double> getEmbedding() {
    return embeddingVector.getEmbedding();
  }

  public RealVector getEmbeddingVector() {
    return embeddingVector;
  }

  public long getTokenCount() {
    return tokenCount;
  }
}
