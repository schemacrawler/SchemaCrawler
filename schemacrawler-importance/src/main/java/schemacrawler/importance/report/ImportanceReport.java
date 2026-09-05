/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.report;

import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * Top-level container record holding domain communities and table importance entries.
 *
 * @param communities detected domain communities
 * @param tables table importance entries
 */
public record ImportanceReport(
    List<CommunityReportEntry> communities, List<ImportanceReportEntry> tables) {

  public ImportanceReport {
    requireNonNull(communities, "No communities provided");
    requireNonNull(tables, "No tables provided");
    communities = List.copyOf(communities);
    tables = List.copyOf(tables);
  }
}
