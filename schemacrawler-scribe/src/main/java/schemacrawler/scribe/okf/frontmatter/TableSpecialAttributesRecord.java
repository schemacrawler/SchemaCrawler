/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static schemacrawler.loader.utility.TableRowCountsUtility.hasRowCount;

import java.util.function.Function;
import schemacrawler.schema.Table;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TableSpecialAttributesRecord(
    Boolean noPrimaryKey,
    Boolean selfReferencing,
    Boolean hasTriggers,
    Boolean emptyTable,
    Boolean bridgeTable) {

  private static final Function<Boolean, Boolean> makeTrueOrNull =
      booleanValue -> (booleanValue == null || !booleanValue) ? null : Boolean.TRUE;

  public TableSpecialAttributesRecord() {
    this(null, null, null, null, null);
  }

  public TableSpecialAttributesRecord {
    noPrimaryKey = makeTrueOrNull.apply(noPrimaryKey);
    selfReferencing = makeTrueOrNull.apply(selfReferencing);
    hasTriggers = makeTrueOrNull.apply(hasTriggers);
    emptyTable = makeTrueOrNull.apply(emptyTable);
    bridgeTable = makeTrueOrNull.apply(bridgeTable);
  }

  public static TableSpecialAttributesRecord of(final Table table, final boolean isBridgeTable) {
    if (table == null) {
      return new TableSpecialAttributesRecord();
    }
    return new TableSpecialAttributesRecord(
        table.hasPrimaryKey(),
        table.isSelfReferencing(),
        table.hasTriggers(),
        hasRowCount(table),
        isBridgeTable);
  }
}
