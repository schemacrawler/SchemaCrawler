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
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

public final class TableSimilarityService {

  private static double[] convertListToArray(final List<Double> list) {
    requireNonNull(list, "No embedding provided");

    final int size = list.size();
    final double[] array = new double[size];

    for (int i = 0; i < size; i++) {
      final Double value = list.get(i);
      if (value == null) {
        throw new IllegalArgumentException("Embedding cannot contain null values");
      }
      array[i] = value;
    }

    return array;
  }

  private static double cosineSimilarity(final double[] vectorA, final double[] vectorB) {
    final RealVector v1 = new ArrayRealVector(vectorA);
    final RealVector v2 = new ArrayRealVector(vectorB);

    final double cosineSimilarity = (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm());
    return cosineSimilarity;
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

  public Collection<EmbeddedTable> query(final String prompt, final int topK) {
    requireNotBlank(prompt, "No prompt provided");

    final double[] promptEmbedding = convertListToArray(service.embed(prompt));

    final List<TableSimilarity> similarities = new ArrayList<>();
    for (final EmbeddedTable embeddedTable : allTables) {
      if (!embeddedTable.hasEmbedding()) {
        continue;
      }
      final double[] tableEmbedding = convertListToArray(embeddedTable.getEmbedding());
      final double cosineSimilarity = cosineSimilarity(promptEmbedding, tableEmbedding);
      similarities.add(new TableSimilarity(embeddedTable, cosineSimilarity));
    }
    Collections.sort(similarities);
    List<TableSimilarity> topKSimilarities;
    if (topK > 0) {
      topKSimilarities = similarities.subList(0, topK);
    } else {
      topKSimilarities = similarities;
    }

    final List<EmbeddedTable> matchedTables = new ArrayList<>();
    for (final TableSimilarity tableSimilarity : topKSimilarities) {
      matchedTables.add(tableSimilarity.getTable());
    }
    return matchedTables;
  }
}
