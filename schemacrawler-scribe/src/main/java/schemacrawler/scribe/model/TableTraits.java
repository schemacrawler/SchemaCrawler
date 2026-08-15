/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.model;

import static schemacrawler.ermodel.utility.ERModelUtility.inferBridgeTable;
import static schemacrawler.loader.utility.TableRowCountsUtility.getRowCount;
import static schemacrawler.loader.utility.TableRowCountsUtility.hasRowCount;

import java.util.function.Function;
import schemacrawler.schema.Table;

public record TableTraits(
    Boolean noPrimaryKey,
    Boolean noForeignKeys,
    Boolean noIndexes,
    Boolean selfReferencing,
    Boolean hasTriggers,
    Boolean emptyTable,
    Boolean bridgeTable) {

  private static final Function<Boolean, Boolean> makeTrueOrNull =
      booleanValue -> booleanValue == null || !booleanValue ? null : Boolean.TRUE;

  public static TableTraits from(final Table table) {
    if (table == null) {
      return new TableTraits();
    }
    return new TableTraits(
        !table.hasPrimaryKey(),
        !table.hasForeignKeys(),
        !table.hasIndexes(),
        table.isSelfReferencing(),
        table.hasTriggers(),
        hasRowCount(table) && getRowCount(table) == 0,
        inferBridgeTable(table).toBoolean(false));
  }

  public TableTraits() {
    this(null, null, null, null, null, null, null);
  }

  public TableTraits {
    noPrimaryKey = makeTrueOrNull.apply(noPrimaryKey);
    noForeignKeys = makeTrueOrNull.apply(noForeignKeys);
    noIndexes = makeTrueOrNull.apply(noIndexes);
    selfReferencing = makeTrueOrNull.apply(selfReferencing);
    hasTriggers = makeTrueOrNull.apply(hasTriggers);
    emptyTable = makeTrueOrNull.apply(emptyTable);
    bridgeTable = makeTrueOrNull.apply(bridgeTable);
  }
}
