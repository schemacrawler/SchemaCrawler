/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import java.util.function.Function;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TableAttributesRecord(
    Boolean noPrimaryKey,
    Boolean selfReferencing,
    Boolean hasTriggers,
    Boolean emptyTable,
    Boolean bridgeTable) {

  private static final Function<Boolean, Boolean> makeTrueOrNull =
      booleanValue -> booleanValue == null || !booleanValue ? null : Boolean.TRUE;

  public TableAttributesRecord() {
    this(null, null, null, null, null);
  }

  public TableAttributesRecord {
    noPrimaryKey = makeTrueOrNull.apply(noPrimaryKey);
    selfReferencing = makeTrueOrNull.apply(selfReferencing);
    hasTriggers = makeTrueOrNull.apply(hasTriggers);
    emptyTable = makeTrueOrNull.apply(emptyTable);
    bridgeTable = makeTrueOrNull.apply(bridgeTable);
  }
}
