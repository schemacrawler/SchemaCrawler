/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.service;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import schemacrawler.importance.cache.DatabaseObjectNodeId;
import schemacrawler.importance.cache.SchemaGraphCache;
import schemacrawler.importance.cache.TableImportance;
import schemacrawler.importance.report.ImportanceReportEntry;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;

/** Builds filtered, deterministically ordered importance reports from a schema graph cache. */
public final class ReportService {

  private final SchemaGraphCache cache;

  public ReportService(final SchemaGraphCache cache) {
    this.cache = requireNonNull(cache, "No schema graph cache provided");
  }

  /**
   * Gets importance report entries for tables and views selected by an inclusion rule.
   *
   * @param tableInclusionRule rule applied to table and view full names
   * @return immutable entries sorted by descending betweenness centrality and then full name
   */
  public List<ImportanceReportEntry> report(final InclusionRule tableInclusionRule) {
    requireNonNull(tableInclusionRule, "No table inclusion rule provided");

    final List<ImportanceReportEntry> entries = new ArrayList<>();
    for (final DatabaseObjectNodeId nodeId : cache.getTableViewNodes()) {
      final DatabaseObject databaseObject = cache.getObjectByNodeId(nodeId);
      if (!(databaseObject instanceof final Table table)
          || !tableInclusionRule.test(table.getFullName())) {
        continue;
      }

      final TableImportance importance = table.getAttribute(TableImportance.class.getName());
      if (importance != null) {
        entries.add(
            new ImportanceReportEntry(
                nodeId,
                table.getFullName(),
                importance.graphMetrics(),
                importance.tableCounts(),
                importance.tableTraits()));
      }
    }
    entries.sort(
        Comparator.comparing(
                (ImportanceReportEntry entry) -> entry.graphMetrics().betweennessCentrality())
            .reversed()
            .thenComparing(ImportanceReportEntry::tableFullName));
    return List.copyOf(entries);
  }
}
