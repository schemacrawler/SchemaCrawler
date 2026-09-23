/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.report;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.filter.NamedObjectFilters;
import schemacrawler.importance.model.DatabaseObjectVertexId;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.importance.model.TableCluster;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.importance.options.ImportanceOptions;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Table;

/** Builds filtered, deterministically ordered importance reports from an importance model. */
public final class ImportanceReportGenerator {

  private static final Comparator<ImportanceReportEntry> IMPORTANCE_REPORT_ENTRY_COMPARATOR =
      Comparator.comparing(ImportanceReportEntry::tableImportance)
          .thenComparing(ImportanceReportEntry::tableFullName);

  private static <T> List<T> limit(final List<T> entries, final int maximum) {
    if (maximum == 0) {
      return List.of();
    }
    if (maximum < 0) {
      return List.copyOf(entries);
    }
    final int limit = Math.min(entries.size(), maximum);
    return List.copyOf(entries.subList(0, limit));
  }

  private final ImportanceModel importanceModel;

  public ImportanceReportGenerator(final ImportanceModel importanceModel) {
    this.importanceModel = requireNonNull(importanceModel, "No importance model provided");
  }

  /** Gets the complete importance report using the supplied inclusion and limit options. */
  public ImportanceReport report(final ImportanceOptions options) {
    requireNonNull(options, "No importance options provided");
    final InclusionRule tableInclusionRule = options.getTableInclusionRule();
    final List<ImportanceReportEntry> tables =
        reportTables(tableInclusionRule, options.getMaxImportantTables());
    final List<ClusterReportEntry> tableClusters =
        limit(
            reportTableClusters(tableInclusionRule, options.getMaxClusterSize()),
            options.getMaxClusters());

    return new ImportanceReport(tableClusters, tables);
  }

  private List<ClusterReportEntry> reportTableClusters(
      final InclusionRule tableInclusionRule, final int maxClusterSize) {
    final List<TableCluster> tableClusters = importanceModel.getTableClusters();
    // Quote-tolerant matching, consistent with the rest of the codebase - a regular expression
    // inclusion rule is tested against both the quoted (displayed) and unquoted full name.
    final NamedObjectFilter<Table> tableFilter = NamedObjectFilters.fullName(tableInclusionRule);

    final List<ClusterReportEntry> entries = new ArrayList<>();
    for (final TableCluster tableCluster : tableClusters) {
      final Table anchorTable =
          importanceModel.lookupTableByVertexId(tableCluster.anchorVertexId()).orElse(null);
      final String anchorFullName =
          anchorTable != null
              ? anchorTable.getFullName()
              : tableCluster.anchorVertexId().key().toString();

      boolean matchesInclusionRule = false;
      // Members that cannot be resolved to a table have no full name available, and are
      // dropped from the cluster rather than falling back to a synthetic name.
      final List<DatabaseObjectVertexId> resolvedMembers = new ArrayList<>();
      final List<String> resolvedFullNames = new ArrayList<>();

      for (final DatabaseObjectVertexId memberId : tableCluster.memberVertexIds()) {
        final Table memberTable = importanceModel.lookupTableByVertexId(memberId).orElse(null);
        if (memberTable == null) {
          continue;
        }
        resolvedMembers.add(memberId);
        resolvedFullNames.add(memberTable.getFullName());
        if (tableFilter.test(memberTable)) {
          matchesInclusionRule = true;
        }
      }

      if (!matchesInclusionRule) {
        continue;
      }

      final int totalSize = resolvedMembers.size();
      final List<DatabaseObjectVertexId> truncatedMembers;
      final List<String> truncatedFullNames;

      if (maxClusterSize > 0 && totalSize > maxClusterSize) {
        truncatedMembers = resolvedMembers.subList(0, maxClusterSize);
        truncatedFullNames = resolvedFullNames.subList(0, maxClusterSize);
      } else {
        truncatedMembers = resolvedMembers;
        truncatedFullNames = resolvedFullNames;
      }

      entries.add(
          new ClusterReportEntry(
              tableCluster.id(),
              tableCluster.anchorVertexId(),
              anchorFullName,
              totalSize,
              truncatedMembers,
              truncatedFullNames));
    }
    return List.copyOf(entries);
  }

  private List<ImportanceReportEntry> reportTables(
      final InclusionRule tableInclusionRule, final int maxTables) {
    final NamedObjectFilter<Table> tableFilter = NamedObjectFilters.fullName(tableInclusionRule);
    final List<ImportanceReportEntry> entries = new ArrayList<>();
    for (final DatabaseObjectVertexId vertexId : importanceModel.getTableVertexIds()) {
      final Table table = importanceModel.lookupTableByVertexId(vertexId).orElse(null);
      if (table == null || !tableFilter.test(table)) {
        continue;
      }

      final TableImportance importance = table.getAttribute(TableImportance.class.getName());
      if (importance != null) {
        entries.add(new ImportanceReportEntry(vertexId, table.getFullName(), importance));
      }
    }
    entries.sort(IMPORTANCE_REPORT_ENTRY_COMPARATOR);

    // Limit number of tables returned
    return limit(entries, maxTables);
  }
}
