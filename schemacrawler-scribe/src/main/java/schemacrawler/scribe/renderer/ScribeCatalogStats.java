/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.renderer;

import static java.util.Objects.requireNonNull;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;

final class ScribeCatalogStats {

  private final Catalog catalog;
  private final ScribeRelationsIndex relationsIndex;

  ScribeCatalogStats(final Catalog catalog, final ScribeRelationsIndex relationsIndex) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
    this.relationsIndex = requireNonNull(relationsIndex, "No relations index provided");
  }

  int foreignKeyCount() {
    return relationsIndex.foreignKeyCount();
  }

  int routineCount() {
    return catalog.getRoutines().size();
  }

  int tableCount() {
    int count = 0;
    for (final Table table : catalog.getTables()) {
      if (!isView(table)) {
        count++;
      }
    }
    return count;
  }

  int viewCount() {
    int count = 0;
    for (final Table table : catalog.getTables()) {
      if (isView(table)) {
        count++;
      }
    }
    return count;
  }

  private static boolean isView(final Table table) {
    return table instanceof View || table.getTableType().isView();
  }
}
