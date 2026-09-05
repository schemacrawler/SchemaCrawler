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
import schemacrawler.importance.options.ImportanceOptions;
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

  /** Gets the complete importance report using the supplied inclusion and limit options. */
  public ImportanceReport report(final ImportanceOptions options) {
    requireNonNull(options, "No importance options provided");
    final InclusionRule tableInclusionRule = options.getTableInclusionRule();
    final List<ImportanceReportEntry> tables =
        reportTables(tableInclusionRule, options.getMaxImportantTables());
    final List<CommunityReportEntry> communities =
        limit(
            reportCommunities(tableInclusionRule, options.getMaxCommunitySize()),
            options.getMaxCommunities());

    return new ImportanceReport(communities, tables);
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

  private static <T> List<T> limit(final List<T> entries, final int maximum) {
    return maximum > 0 && entries.size() > maximum
        ? List.copyOf(entries.subList(0, maximum))
        : List.copyOf(entries);
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
    return limit(entries, maxTables);
  }
}
