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

import static java.util.Objects.requireNonNull;

public final class TableSimilarity implements Comparable<TableSimilarity> {

  private final EmbeddedTable table;
  private final double similarity;

  public TableSimilarity(final EmbeddedTable table, final double similarity) {
    this.table = requireNonNull(table);
    this.similarity = similarity;
  }

  @Override
  public int compareTo(final TableSimilarity other) {
    // Null and instance checks
    if (other == null) {
      throw new NullPointerException("Cannot compare with null object");
    }

    // Reverse similarity - comparison results in descending order
    final int compare = Double.compare(other.similarity, similarity);
    return compare;
  }

  public double getSimilarity() {
    return similarity;
  }

  public EmbeddedTable getTable() {
    return table;
  }
}
