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
public record TableAttributes(
    Boolean noPrimaryKey,
    Boolean selfReferencing,
    Boolean hasTriggers,
    Boolean emptyTable,
    Boolean bridgeTable) {

  private static final Function<Boolean, Boolean> makeTrueOrNull =
      booleanValue -> booleanValue == null || !booleanValue ? null : Boolean.TRUE;

  public TableAttributes() {
    this(null, null, null, null, null);
  }

  public TableAttributes {
    noPrimaryKey = makeTrueOrNull.apply(noPrimaryKey);
    selfReferencing = makeTrueOrNull.apply(selfReferencing);
    hasTriggers = makeTrueOrNull.apply(hasTriggers);
    emptyTable = makeTrueOrNull.apply(emptyTable);
    bridgeTable = makeTrueOrNull.apply(bridgeTable);
  }
}
