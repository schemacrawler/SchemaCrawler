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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.linear.RealVector;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import us.fatehi.utility.string.StringFormat;

public final class TableSimilarityService {

  private static final Logger LOGGER =
      Logger.getLogger(TableSimilarityService.class.getCanonicalName());

  private static double cosineSimilarity(final RealVector v1, final RealVector v2) {
    requireNonNull(v1, "No vector provided");
    requireNonNull(v2, "No vector provided");

    return v1.dotProduct(v2) / (v1.getNorm() * v2.getNorm());
  }

  private final EmbeddingService service;
  private final Collection<EmbeddedTable> allTables;

  public TableSimilarityService(final EmbeddingService service) {
    this.service = requireNonNull(service, "No embedding service provided");

    allTables = new ArrayList<>();
  }

  public void addTable(final EmbeddedTable table) {
    if (table != null) {
      allTables.add(table);
    }
  }

  public Collection<EmbeddedTable> query(final String prompt, final long maxTokens) {
    requireNotBlank(prompt, "No prompt provided");

    final TextEmbedding promptEmbedding = service.embed(prompt);

    final List<TableSimilarity> similarities = new ArrayList<>();
    for (final EmbeddedTable embeddedTable : allTables) {
      if (!embeddedTable.hasEmbedding()) {
        continue;
      }
      final TextEmbedding tableEmbedding = embeddedTable.getEmbedding();
      final double cosineSimilarity =
          cosineSimilarity(
              promptEmbedding.getEmbeddingVector(), tableEmbedding.getEmbeddingVector());
      similarities.add(new TableSimilarity(embeddedTable, cosineSimilarity));
    }
    Collections.sort(similarities);

    final List<TableSimilarity> tableSimilarities = pruneToMaxTokens(similarities, maxTokens);

    final List<EmbeddedTable> matchedTables = new ArrayList<>();
    for (final TableSimilarity tableSimilarity : tableSimilarities) {
      matchedTables.add(tableSimilarity.getTable());
    }
    return matchedTables;
  }

  private List<TableSimilarity> pruneToMaxTokens(
      final List<TableSimilarity> similarities, final long maxTokens) {
    requireNonNull(similarities, "No similarities provided");

    long tokenSum = 0;
    int index = -1;
    for (final TableSimilarity tableSimilarity : similarities) {
      if (tableSimilarity == null) {
        continue;
      }
      final TextEmbedding embedding = tableSimilarity.getTable().getEmbedding();
      if (embedding == null) {
        continue;
      }
      final long tokenCount = embedding.getTokenCount();
      if (tokenSum + tokenCount >= maxTokens) {
        break;
      }
      tokenSum = tokenSum + tokenCount;
      index = index + 1;
    }
    if (index == -1) {
      return new ArrayList<>();
    }
    LOGGER.log(
        Level.CONFIG, new StringFormat("Limiting to %d tables, with %d tokens", index, tokenSum));
    return similarities.subList(0, index + 1);
  }
}
