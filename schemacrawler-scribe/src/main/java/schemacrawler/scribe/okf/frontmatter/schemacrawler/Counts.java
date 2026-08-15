/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.schemacrawler;

import java.util.function.Function;

public record Counts(
    Integer columnCount,
    Integer foreignKeyCount,
    Integer indexCount,
    Integer triggerCount,
    Long rowCount) {

  private static final Function<Integer, Integer> removeNegativeInteger =
      x -> x == null || x < 0 ? null : x;
  private static final Function<Long, Long> makeValidRowCount = x -> x == null || x <= 0 ? null : x;

  public Counts {
    columnCount = removeNegativeInteger.apply(columnCount);
    foreignKeyCount = removeNegativeInteger.apply(foreignKeyCount);
    indexCount = removeNegativeInteger.apply(indexCount);
    triggerCount = removeNegativeInteger.apply(triggerCount);
    rowCount = makeValidRowCount.apply(rowCount);
  }

  public Counts() {
    this(null, null, null, null, null);
  }
}
