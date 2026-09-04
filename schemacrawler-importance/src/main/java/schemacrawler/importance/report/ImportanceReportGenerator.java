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
   * Gets importance report entries for tables and views selected by an inclusion rule.
   *
   * @param tableInclusionRule rule applied to table and view full names
   * @return immutable entries sorted by descending importance score, then descending betweenness
   *     centrality, then full name
   */
  public List<ImportanceReportEntry> report(final InclusionRule tableInclusionRule) {
    requireNonNull(tableInclusionRule, "No table inclusion rule provided");

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
    return List.copyOf(entries);
  }
}
