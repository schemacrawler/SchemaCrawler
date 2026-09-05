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
import schemacrawler.importance.model.DatabaseObjectNodeId;
import schemacrawler.importance.model.SchemaCommunity;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.model.TableImportance;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;

/** Builds filtered, deterministically ordered importance reports from a schema graph model. */
public final class ImportanceReportGenerator {

  private static final Comparator<ImportanceReportEntry> IMPORTANCE_REPORT_ENTRY_COMPARATOR =
      Comparator.comparing(ImportanceReportEntry::tableImportance)
          .thenComparing(ImportanceReportEntry::tableFullName);

  private final SchemaGraphModel schemaGraphModel;

  public ImportanceReportGenerator(final SchemaGraphModel schemaGraphModel) {
    this.schemaGraphModel = requireNonNull(schemaGraphModel, "No schema graph model provided");
  }

  /**
   * Gets the complete importance report including communities and table entries.
   *
   * @param tableInclusionRule rule applied to table and view full names
   * @return consolidated importance report
   */
  public ImportanceReport report(final InclusionRule tableInclusionRule) {
    return report(tableInclusionRule, 0, 5);
  }

  /**
   * Gets the complete importance report with capped table entries.
   *
   * @param tableInclusionRule rule applied to table and view full names
   * @param maxTables maximum number of table entries to return (default 5, <=0 for unlimited)
   * @return consolidated importance report
   */
  public ImportanceReport report(final InclusionRule tableInclusionRule, final int maxTables) {
    return report(tableInclusionRule, maxTables, 5);
  }

  /**
   * Gets the complete importance report with capped table entries and capped community sizes.
   *
   * @param tableInclusionRule rule applied to table and view full names
   * @param maxTables maximum number of table entries to return (default 5, <=0 for unlimited)
   * @param maxCommunitySize maximum member tables to list per community (default 5, <=0 for
   *     unlimited)
   * @return consolidated importance report
   */
  public ImportanceReport report(
      final InclusionRule tableInclusionRule, final int maxTables, final int maxCommunitySize) {
    requireNonNull(tableInclusionRule, "No table inclusion rule provided");

    final List<ImportanceReportEntry> tables = reportTables(tableInclusionRule, maxTables);
    final List<CommunityReportEntry> communities =
        reportCommunities(tableInclusionRule, maxCommunitySize);

    return new ImportanceReport(communities, tables);
  }

  private List<ImportanceReportEntry> reportTables(
      final InclusionRule tableInclusionRule, final int maxTables) {
    final List<ImportanceReportEntry> entries = new ArrayList<>();
    for (final DatabaseObjectNodeId nodeId : schemaGraphModel.getTableNodes()) {
      final DatabaseObject databaseObject = schemaGraphModel.getObjectByNodeId(nodeId);
      if (!(databaseObject instanceof final Table table)
          || !tableInclusionRule.test(table.getFullName())) {
        continue;
      }

      final TableImportance importance = table.getAttribute(TableImportance.class.getName());
      if (importance != null) {
        entries.add(new ImportanceReportEntry(nodeId, table.getFullName(), importance));
      }
    }
    entries.sort(IMPORTANCE_REPORT_ENTRY_COMPARATOR);

    // Limit number of tables returned
    final List<ImportanceReportEntry> result =
        maxTables > 0 && entries.size() > maxTables ? entries.subList(0, maxTables) : entries;

    return List.copyOf(result);
  }

  private List<CommunityReportEntry> reportCommunities(
      final InclusionRule tableInclusionRule, final int maxCommunitySize) {
    final List<SchemaCommunity> schemaCommunities = schemaGraphModel.getCommunities();

    final List<CommunityReportEntry> entries = new ArrayList<>();
    for (final SchemaCommunity community : schemaCommunities) {
      final DatabaseObject anchorObj = schemaGraphModel.getObjectByNodeId(community.anchorNode());
      final String anchorFullName =
          anchorObj != null ? anchorObj.getFullName() : community.anchorNode().key().toString();

      boolean matchesInclusionRule = false;
      final List<DatabaseObjectNodeId> allMembers = community.memberNodes();
      final List<String> allFullNames = new ArrayList<>();

      for (final DatabaseObjectNodeId memberId : allMembers) {
        final DatabaseObject memberObj = schemaGraphModel.getObjectByNodeId(memberId);
        final String fullName =
            memberObj != null ? memberObj.getFullName() : memberId.key().toString();
        allFullNames.add(fullName);
        if (tableInclusionRule.test(fullName)) {
          matchesInclusionRule = true;
        }
      }

      if (!matchesInclusionRule) {
        continue;
      }

      final int totalSize = allMembers.size();
      final List<DatabaseObjectNodeId> truncatedMembers;
      final List<String> truncatedFullNames;

      if (maxCommunitySize > 0 && totalSize > maxCommunitySize) {
        truncatedMembers = allMembers.subList(0, maxCommunitySize);
        truncatedFullNames = allFullNames.subList(0, maxCommunitySize);
      } else {
        truncatedMembers = allMembers;
        truncatedFullNames = allFullNames;
      }

      entries.add(
          new CommunityReportEntry(
              community.id(),
              community.anchorNode(),
              anchorFullName,
              totalSize,
              truncatedMembers,
              truncatedFullNames));
    }
    return List.copyOf(entries);
  }
}
