/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model;

import static java.util.Objects.requireNonNull;

import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;

/** Immutable table-only metadata, topology metrics, and composite importance score. */
public record TableImportance(
    int importanceScore,
    TableImportanceMetrics importanceMetrics,
    TableTraits tableTraits,
    TableCounts tableCounts)
    implements Comparable<TableImportance> {

  public TableImportance {
    if (importanceScore < 0 || importanceScore > 100) {
      throw new IllegalArgumentException("Importance score is out of range");
    }
    requireNonNull(importanceMetrics, "No table importance metrics provided");
    requireNonNull(tableTraits, "No table traits provided");
    requireNonNull(tableCounts, "No table counts provided");
  }

  @Override
  public int compareTo(final TableImportance other) {
    if (other == null) {
      return 1;
    }
    // NOTE: Descending order
    int compare;
    compare = Integer.compare(other.importanceScore, importanceScore);
    if (compare != 0) {
      return compare;
    }
    compare =
        Double.compare(
            other.importanceMetrics().betweennessCentrality(),
            importanceMetrics().betweennessCentrality());
    return compare;
  }
}
