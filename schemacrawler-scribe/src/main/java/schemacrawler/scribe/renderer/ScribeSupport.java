/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.renderer;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.ermodel.utility.ERModelUtility;
import schemacrawler.loader.ermodel.summary.ERModelStats;
import schemacrawler.loader.utility.TableRowCountsUtility;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.View;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import schemacrawler.tools.utility.AbstractTextSupport;

/**
 * Single source of truth for all catalog, ER model, lint, and message data used by Scribe
 * renderers. Built once by the Scribe command, and passed to renderers, which must not build their
 * own instance.
 */
public final class ScribeSupport extends AbstractTextSupport {

  public enum EntityModelType {
    unknown,
    non_entity,
    subtype,
    weak_entity,
    strong_entity,
    bridge_table,
    ;
  }

  private final Lints lints;
  private final ScribeMessages messages;
  private final ScribeOptions options;
  private final RelationshipsIndex relationsIndex;
  private final ScribeCatalogStats catalogStats;

  /**
   * Creates the Scribe support instance, transferring catalog, ER model, and connection state from
   * the given execution state, and building the cross-reference maps once.
   *
   * @param executionState Execution state providing the catalog and ER model
   * @param options Scribe options
   */
  public ScribeSupport(
      final ExecutionState executionState, final ScribeOptions options, final Lints lints) {
    requireNonNull(executionState, "No execution state provided");
    this.options = requireNonNull(options, "No Scribe options provided");
    this.lints = requireNonNull(lints, "No lints provided");

    executionState.transferState(this);
    relationsIndex = new RelationshipsIndex(getCatalog());
    catalogStats =
        new ScribeCatalogStats(
            getCatalog(), hasERModel() ? Optional.of(getERModel()) : Optional.empty());

    messages = new ScribeMessages(options.getLocale());
  }

  /**
   * Gets all routines, sorted alphabetically by full name.
   *
   * @return Sorted routines
   */
  public List<Routine> allRoutines() {
    final List<Routine> routines = new ArrayList<>(getCatalog().getRoutines());
    routines.sort(Comparator.comparing(Routine::getFullName));
    return List.copyOf(routines);
  }

  /**
   * Gets all tables and views, sorted alphabetically by full name.
   *
   * @return Sorted tables and views
   */
  public List<Table> allTables() {
    final List<Table> tables = new ArrayList<>(getCatalog().getTables());
    tables.sort(Comparator.comparing(Table::getFullName));
    return List.copyOf(tables);
  }

  /**
   * Gets the foreign keys where the given table holds the foreign key (the child side).
   *
   * @param table Table
   * @return Child foreign keys, or an empty collection when the table is {@code null}
   */
  public Collection<ForeignKey> childForeignKeys(final Table table) {
    return relationsIndex.childForeignKeys(table);
  }

  /**
   * Gets the timestamp of the SchemaCrawler run that produced the catalog. Scribe does not generate
   * its own as-of date.
   *
   * @return Crawl timestamp
   */
  public Instant crawlTimestamp() {
    return getCatalog().getCrawlInfo().getCrawlTimestampInstant();
  }

  /**
   * Gets the report title. Uses the report title from the options when set, otherwise a localized
   * default title.
   *
   * @return Database or report title
   */
  public String databaseTitle() {
    if (!isBlank(options.getTitle())) {
      return options.getTitle();
    }
    return messages.labelDatabaseSchema();
  }

  /**
   * Gets the ER model statistics, or {@code null} if no ER model is available.
   *
   * @return ER model stats, or {@code null}
   */
  public ERModelStats erModelStats() {
    return catalogStats.erModelStats().orElse(null);
  }

  public String escapeMarkdown(final String input) {
    return MarkdownFormattingHelper.escapeMarkdown(input);
  }

  /**
   * Gets the deduplicated number of foreign keys across all tables.
   *
   * @return Foreign key count
   */
  public int foreignKeyCount() {
    return catalogStats.foreignKeyCount();
  }

  /**
   * Checks whether a column is part of a foreign key.
   *
   * @param column Column
   * @return {@code false} when the column is {@code null}
   */
  public boolean isForeignKeyColumn(final Column column) {
    return column != null && column.isPartOfForeignKey();
  }

  /**
   * Whether lint results are included in the report.
   *
   * @return Whether lint is enabled
   */
  public boolean isLintEnabled() {
    return options.isIncludeLint();
  }

  /**
   * Checks whether a column is part of the primary key.
   *
   * @param column Column
   * @return {@code false} when the column is {@code null}
   */
  public boolean isPrimaryKeyColumn(final Column column) {
    return column != null && column.isPartOfPrimaryKey();
  }

  /**
   * Checks whether table is a view.
   *
   * @param table Table
   * @return {@code false} when the table is {@code null}
   */
  public boolean isView(final Table table) {
    if (table == null) {
      return false;
    }
    return table instanceof View || table.getTableType().isView();
  }

  /**
   * Gets the lint issues for a table.
   *
   * @param table Table
   * @return Lint issues, empty when lint is disabled or the table is {@code null}
   */
  public List<Lint<?>> lintIssues(final Table table) {
    if (table == null) {
      return List.of();
    }
    return lints.getLints(table);
  }

  /**
   * Gets the cached lint results.
   *
   * @return Lint results, an empty sentinel when lint is disabled
   */
  public Lints lints() {
    return lints;
  }

  public String localizedEntityModelType(final Table table) {
    return switch (entityModelType(table)) {
      case non_entity -> messages.valueEntityModelTypeNonEntity();
      case subtype -> messages.valueEntityModelTypeSubtype();
      case weak_entity -> messages.valueEntityModelTypeWeakEntity();
      case strong_entity -> messages.valueEntityModelTypeStrongEntity();
      case bridge_table -> messages.valueEntityModelTypeBridgeTable();
      default -> "";
    };
  }

  /**
   * Gets the Mermaid cardinality symbol for a table reference, from foreign key to primary key.
   *
   * @param foreignKey Table reference
   * @return Mermaid cardinality symbol, falling back to a default when cardinality is unknown
   */
  public String mermaidCardinality(final TableReference foreignKey) {
    return cardinalitySymbol(foreignKey);
  }

  /**
   * Gets the localized messages for this report.
   *
   * @return Scribe messages
   */
  public ScribeMessages messages() {
    return messages;
  }

  /**
   * Gets the foreign keys where the given table holds the referenced primary key (the parent side).
   *
   * @param table Table
   * @return Parent foreign keys, or an empty collection when the table is {@code null}
   */
  public Collection<ForeignKey> parentForeignKeys(final Table table) {
    return relationsIndex.parentForeignKeys(table);
  }

  /**
   * Gets the parent tables referenced by a table's imported foreign keys.
   *
   * @param table Table
   * @return Referenced tables, or an empty collection when the table is {@code null}
   */
  public Collection<Table> referencedTables(final Table table) {
    return relationsIndex.referencedTables(table);
  }

  /**
   * Gets the child tables that reference a table via their imported foreign keys.
   *
   * @param table Table
   * @return Referencing tables, or an empty collection when the table is {@code null}
   */
  public Collection<Table> referencingTables(final Table table) {
    return relationsIndex.referencingTables(table);
  }

  /**
   * Gets the deduplicated number of routines in the catalog.
   *
   * @return Routine count
   */
  public int routineCount() {
    return catalogStats.routineCount();
  }

  /**
   * Gets the routine definition (DDL body).
   *
   * @param routine Routine
   * @return Routine definition, or an empty string when unavailable
   */
  public String routineDefinition(final Routine routine) {
    if (routine == null || !routine.hasDefinition()) {
      return "";
    }
    return routine.getDefinition();
  }

  /**
   * Gets the row count for a table. Row counts are only available when the catalog was crawled with
   * row-count loading enabled upstream (the {@code load-row-counts} crawl {@code Config} key,
   * consumed by SchemaCrawler's {@code TableRowCountsLoader}); Scribe never queries the database
   * for counts itself.
   *
   * @param table Table
   * @return Row count, or {@code -1} if unavailable
   */
  public long rowCount(final Table table) {
    if (table == null || !TableRowCountsUtility.hasRowCount(table)) {
      return -1;
    }
    final long rowCount = TableRowCountsUtility.getRowCount(table);
    return rowCount;
  }

  public String sentenceCase(final String text) {
    return MarkdownFormattingHelper.sentenceCase(text);
  }

  /**
   * Gets a localized lint severity label for report headings.
   *
   * @param severity Lint severity
   * @return Localized severity label, or empty when severity is {@code null}
   */
  public String severityMessage(final LintSeverity severity) {
    if (severity == null) {
      return "";
    }

    return switch (severity) {
      case low -> messages.lintSeverityLow();
      case medium -> messages.lintSeverityMedium();
      case high -> messages.lintSeverityHigh();
      case critical -> messages.lintSeverityCritical();
    };
  }

  /**
   * Gets table attributes for template rendering.
   *
   * @param table Table
   * @return Attributes map, or an empty map when unavailable
   */
  public Map<String, Object> tableAttributes(final Table table) {
    if (table == null || table.getAttributes() == null) {
      return Map.of();
    }
    return table.getAttributes();
  }

  /**
   * Gets the number of tables (excluding views) in the catalog.
   *
   * @return Table count
   */
  public int tableCount() {
    return catalogStats.tableCount();
  }

  /**
   * Gets the table definition (DDL body).
   *
   * @param table Table
   * @return Table definition, or an empty string when unavailable
   */
  public String tableDefinition(final Table table) {
    if (table == null || !table.hasDefinition()) {
      return "";
    }
    return table.getDefinition();
  }

  /**
   * Gets other tables, views, or routines that reference or use a table.
   *
   * @param table Table
   * @return Union of referencing tables and views that use the table
   */
  public Collection<Table> usedByObjects(final Table table) {
    if (table == null) {
      return List.of();
    }
    final Set<Table> used = new LinkedHashSet<>(referencingTables(table));
    used.addAll(usedByViews(table));
    return List.copyOf(used);
  }

  /**
   * Gets the number of views in the catalog.
   *
   * @return View count
   */
  public int viewCount() {
    return catalogStats.viewCount();
  }

  private EntityModelType entityModelType(final Table table) {
    if (ERModelUtility.inferBridgeTable(table).toBoolean(false)) {
      return EntityModelType.bridge_table;
    }
    final EntityType entityType = ERModelUtility.inferEntityType(table);
    return switch (entityType) {
      case strong_entity -> EntityModelType.strong_entity;
      case subtype -> EntityModelType.subtype;
      case weak_entity -> EntityModelType.weak_entity;
      case non_entity -> EntityModelType.non_entity;
      default -> EntityModelType.unknown;
    };
  }

  private Collection<Table> usedByViews(final Table table) {
    if (table == null) {
      return List.of();
    }
    final List<Table> views = new ArrayList<>();
    for (final Table candidate : getCatalog().getTables()) {
      if (isView(candidate) && ((View) candidate).getTableUsage().contains(table)) {
        views.add(candidate);
      }
    }
    return List.copyOf(views);
  }
}
