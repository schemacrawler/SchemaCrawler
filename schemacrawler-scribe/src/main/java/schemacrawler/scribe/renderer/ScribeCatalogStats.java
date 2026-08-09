/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.renderer;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.loader.catalog.summary.CatalogStats;
import schemacrawler.loader.catalog.summary.CatalogStatsUtility;
import schemacrawler.loader.ermodel.summary.ERModelStats;
import schemacrawler.loader.ermodel.summary.ERModelStatsUtility;
import schemacrawler.schema.Catalog;

final class ScribeCatalogStats {

  private final CatalogStats catalogStats;
  private final Optional<ERModelStats> erModelStats;

  ScribeCatalogStats(final Catalog catalog, final Optional<ERModel> erModel) {
    catalogStats = CatalogStatsUtility.from(requireNonNull(catalog, "No catalog provided"));
    erModelStats = requireNonNull(erModel, "No ER model provided").map(ERModelStatsUtility::from);
  }

  Optional<ERModelStats> erModelStats() {
    return erModelStats;
  }

  int foreignKeyCount() {
    return catalogStats.counts().foreignKeyCount();
  }

  int routineCount() {
    return catalogStats.counts().routines();
  }

  int tableCount() {
    return catalogStats.counts().tableCount();
  }

  int viewCount() {
    return catalogStats.counts().viewCount();
  }
}
