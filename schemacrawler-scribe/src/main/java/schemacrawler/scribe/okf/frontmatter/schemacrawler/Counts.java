/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.schemacrawler;

import java.util.function.Function;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Counts(
    Integer columnCount,
    Integer foreignKeyCount,
    Integer indexCount,
    Integer triggerCount,
    Long rowCount,
    Integer parameterCount) {

  private static final Function<Integer, Integer> makePostiveInteger =
      x -> x == null || x <= 0 ? null : x;
  private static final Function<Long, Long> makePostiveLong = x -> x == null || x <= 0 ? null : x;

  public Counts {
    columnCount = makePostiveInteger.apply(columnCount);
    foreignKeyCount = makePostiveInteger.apply(foreignKeyCount);
    indexCount = makePostiveInteger.apply(indexCount);
    triggerCount = makePostiveInteger.apply(triggerCount);
    rowCount = makePostiveLong.apply(rowCount);
    parameterCount = makePostiveInteger.apply(parameterCount);
  }

  public Counts() {
    this(null, null, null, null, null, null);
  }
}
