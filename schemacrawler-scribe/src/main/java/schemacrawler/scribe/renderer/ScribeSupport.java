/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.renderer;

import static java.util.Objects.requireNonNull;
import static schemacrawler.scribe.renderer.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.ermodel.model.Relationship;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.loader.utility.TableRowCountsUtility;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TypedObject;
import schemacrawler.schema.View;
import schemacrawler.scribe.command.options.SchemaScribeOptions;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import schemacrawler.tools.utility.AbstractTextSupport;
import schemacrawler.utility.MetaDataUtility;

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
  private final SchemaScribeOptions options;
  private final List<ForeignKey> allForeignKeys;
  private final Map<NamedObjectKey, List<ForeignKey>> childForeignKeysByTable;
  private final Map<NamedObjectKey, List<ForeignKey>> parentForeignKeysByTable;
  private final Map<NamedObjectKey, List<Table>> referencedTablesByTable;

  private final Map<NamedObjectKey, List<Table>> referencingTablesByTable;

  /**
   * Creates the Scribe support instance, transferring catalog, ER model, and connection state from
   * the given execution state, and building the cross-reference maps once.
   *
   * @param executionState Execution state providing the catalog and ER model
   * @param options Scribe options
   */
  public ScribeSupport(
      final ExecutionState executionState, final SchemaScribeOptions options, final Lints lints) {
    requireNonNull(executionState, "No execution state provided");
    this.options = requireNonNull(options, "No Scribe options provided");
    this.lints = requireNonNull(lints, "No lints provided");

    executionState.transferState(this);

    final Map<NamedObjectKey, List<ForeignKey>> child = new HashMap<>();
    final Map<NamedObjectKey, List<ForeignKey>> parent = new HashMap<>();
    final Map<NamedObjectKey, List<Table>> referenced = new HashMap<>();
    final Map<NamedObjectKey, List<Table>> referencing = new HashMap<>();
    final Map<NamedObjectKey, ForeignKey> deduplicatedForeignKeys = new LinkedHashMap<>();

    for (final Table table : getCatalog().getTables()) {
      final List<ForeignKey> imported = List.copyOf(table.getImportedForeignKeys());
      final List<ForeignKey> exported = List.copyOf(table.getExportedForeignKeys());
      child.put(table.key(), imported);
      parent.put(table.key(), exported);

      final List<Table> referencedTablesForTable = new ArrayList<>();
      for (final ForeignKey foreignKey : imported) {
        referencedTablesForTable.add(foreignKey.getPrimaryKeyTable());
        deduplicatedForeignKeys.putIfAbsent(foreignKey.key(), foreignKey);
      }
      referenced.put(table.key(), List.copyOf(referencedTablesForTable));

      final List<Table> referencingTablesForTable = new ArrayList<>();
      for (final ForeignKey foreignKey : exported) {
        referencingTablesForTable.add(foreignKey.getForeignKeyTable());
        deduplicatedForeignKeys.putIfAbsent(foreignKey.key(), foreignKey);
      }
      referencing.put(table.key(), List.copyOf(referencingTablesForTable));
    }

    childForeignKeysByTable = Map.copyOf(child);
    parentForeignKeysByTable = Map.copyOf(parent);
    referencedTablesByTable = Map.copyOf(referenced);
    referencingTablesByTable = Map.copyOf(referencing);
    allForeignKeys = List.copyOf(deduplicatedForeignKeys.values());

    messages = new ScribeMessages(options.getLocale());
  }

  /**
   * Gets all foreign keys in the catalog, deduplicated.
   *
   * @return Deduplicated foreign keys
   */
  public List<ForeignKey> allForeignKeys() {
    return allForeignKeys;
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
   * Gets all tables (not views), sorted alphabetically by full name.
   *
   * @return Sorted tables
   */
  public List<Table> allTables() {
    return sortedTables(table -> !isView(table));
  }

  /**
   * Gets all tables and views, sorted alphabetically by full name.
   *
   * @return Sorted tables and views
   */
  public List<Table> allTablesAndViews() {
    return sortedTables(table -> true);
  }

  /**
   * Gets all views, sorted alphabetically by full name.
   *
   * @return Sorted views
   */
  public List<Table> allViews() {
    return sortedTables(this::isView);
  }

  /**
   * Gets all many-to-many bridge tables in the ER model, sorted alphabetically by full name.
   *
   * @return Bridge tables, empty when no ER model is available
   */
  public List<Table> bridgeTables() {
    if (!hasERModel()) {
      return List.of();
    }
    final List<Table> tables = new ArrayList<>();
    for (final Table table : getCatalog().getTables()) {
      if (isBridgeTable(table)) {
        tables.add(table);
      }
    }
    tables.sort(Comparator.comparing(Table::getFullName));
    return List.copyOf(tables);
  }

  /**
   * Gets the routines that call a given routine. JDBC exposes no portable caller graph, so this
   * always returns an empty collection.
   *
   * @param routine Routine
   * @return Empty collection
   */
  public Collection<Routine> calledByRoutines(final Routine routine) {
    return List.of();
  }

  /**
   * Gets the foreign keys where the given table holds the foreign key (the child side).
   *
   * @param table Table
   * @return Child foreign keys, or an empty collection when the table is {@code null}
   */
  public Collection<ForeignKey> childForeignKeys(final Table table) {
    return lookup(childForeignKeysByTable, table);
  }

  /**
   * Gets the total number of columns across all tables and views.
   *
   * @return Column count
   */
  public int columnCount() {
    int count = 0;
    for (final Table table : getCatalog().getTables()) {
      count += table.getColumns().size();
    }
    return count;
  }

  /**
   * Gets the timestamp of the SchemaCrawler run that produced the catalog. Scribe does not generate
   * its own as-of date; this is the same value as {@link #runTimestamp()}.
   *
   * @return Crawl timestamp
   */
  public Instant crawlTimestamp() {
    return runTimestamp();
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

  public EntityModelType entityModelType(final Table table) {
    if (table == null || !hasERModel()) {
      return EntityModelType.unknown;
    }
    if (isBridgeTable(table)) {
      return EntityModelType.bridge_table;
    }
    final Optional<EntityType> optionalEntityType = entityType(table);
    if (optionalEntityType.isEmpty()) {
      return EntityModelType.unknown;
    }
    return switch (optionalEntityType.get()) {
      case strong_entity -> EntityModelType.strong_entity;
      case subtype -> EntityModelType.subtype;
      case weak_entity -> EntityModelType.weak_entity;
      case non_entity -> EntityModelType.non_entity;
      default -> EntityModelType.unknown;
    };
  }

  /**
   * Gets the entity type of a table's ER model entity.
   *
   * @param table Table
   * @return Entity type, empty when no ER model is available or the table is not modeled
   */
  public Optional<EntityType> entityType(final Table table) {
    if (table == null || !hasERModel()) {
      return Optional.empty();
    }
    return getERModel().lookupEntity(table).map(Entity::getType);
  }

  public String escapeMarkdown(final String input) {
    if (isBlank(input)) {
      return "";
    }

    final StringBuilder sb = new StringBuilder(input.length() * 2);
    input
        .replaceAll("\\R", " ")
        .codePoints()
        .forEach(
            cp -> {
              if (isMarkdownSpecial(cp)) {
                sb.append('\\');
              }
              sb.appendCodePoint(cp);
            });

    return sb.toString().replaceAll("\\R", "");
  }

  /**
   * Gets the deduplicated number of foreign keys across all tables.
   *
   * @return Foreign key count
   */
  public int foreignKeyCount() {
    return allForeignKeys.size();
  }

  public String frontMatter(final DatabaseObject object) {
    if (object == null) {
      return "";
    }

    final List<String> tags = new ArrayList<>();

    final Map<String, Object> frontMatter = new LinkedHashMap<>();
    final String simpleTypeName = MetaDataUtility.getSimpleTypeName(object).toString();
    tags.add(simpleTypeName);
    frontMatter.put("type", simpleTypeName);
    frontMatter.put("title", object.getFullName());
    if (object.hasRemarks()) {
      frontMatter.put("description", object.getRemarks());
    }
    if (object instanceof final TypedObject typedObject) {
      frontMatter.put("complete_type", typedObject.getType().toString());
    }
    frontMatter.put("schema", object.getSchema().getFullName());
    frontMatter.put("name", object.getName());

    if (object instanceof final Table table) {
      frontMatter.put("resource", "catalog://tables/" + encodeFullName(object));
      if (!table.hasPrimaryKey()) {
        tags.add("no_primary_key");
      }
      if (table.hasTriggers()) {
        tags.add("has_triggers");
      }

      frontMatter.put("column_count", table.getColumns().size());
      frontMatter.put("foreign_key_count", table.getReferencedTables().size());
      frontMatter.put("index_count", table.getIndexes().size());
      frontMatter.put("trigger_count", table.getTriggers().size());

      if (table.isSelfReferencing()) {
        tags.add("self_referencing");
      }

      final long rowCount = rowCount(table);
      if (rowCount >= 0) {
        frontMatter.put("row_count", rowCount);
        if (rowCount == 0) {
          tags.add("empty_table");
        }
      }
      if (hasERModel()) {
        final ERModel erModel = getERModel();
        final Optional<Entity> lookupEntity = erModel.lookupEntity(table);
        if (lookupEntity.isPresent()) {
          final Entity entity = lookupEntity.get();
          final EntityType entityType = entity.getType();
          if (entityType != EntityType.unknown) {
            frontMatter.put("entity_type", entityType.description());
            tags.add(entityType.name());
          }
        }
        if (erModel.lookupByBridgeTable(table).isPresent()) {
          tags.add("bridge_table");
        }
      }
    }

    if (object instanceof final Routine routine) {
      frontMatter.put("resource", "catalog://routines/" + encodeFullName(object));
      frontMatter.put("parameter_count", routine.getParameters().size());
    }

    frontMatter.put("tags", tags);

    if (hasCatalog()) {
      final Catalog catalog = getCatalog();
      final CrawlInfo crawlInfo = catalog.getCrawlInfo();
      frontMatter.put("timestamp", crawlInfo.getCrawlTimestamp());
      frontMatter.put("run_id", crawlInfo.getRunId());
    }

    return mapper.writeValueAsString(frontMatter);
  }

  /**
   * Checks whether any table in the catalog has a row count available. Row counts are only present
   * when the catalog was crawled with row-count loading enabled upstream (see {@link
   * #rowCount(Table)}); this is a catalog-wide convenience for renderers deciding whether to show a
   * row-count column/section at all.
   *
   * @return Whether any table has a row count available
   */
  public boolean hasRowCounts() {
    for (final Table table : getCatalog().getTables()) {
      if (TableRowCountsUtility.hasRowCount(table)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether a table is a many-to-many bridge table in the ER model.
   *
   * @param table Table
   * @return {@code false} when the table is {@code null} or no ER model is available
   */
  public boolean isBridgeTable(final Table table) {
    if (table == null || !hasERModel()) {
      return false;
    }
    return getERModel().lookupByBridgeTable(table).isPresent();
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
   * Gets tables that have neither imported nor exported foreign keys.
   *
   * @return Orphan tables, sorted alphabetically by full name
   */
  public List<Table> orphanTables() {
    final List<Table> orphans = new ArrayList<>();
    for (final Table table : allTables()) {
      if (childForeignKeys(table).isEmpty() && parentForeignKeys(table).isEmpty()) {
        orphans.add(table);
      }
    }
    return List.copyOf(orphans);
  }

  /**
   * Gets the foreign keys where the given table holds the referenced primary key (the parent side).
   *
   * @param table Table
   * @return Parent foreign keys, or an empty collection when the table is {@code null}
   */
  public Collection<ForeignKey> parentForeignKeys(final Table table) {
    return lookup(parentForeignKeysByTable, table);
  }

  /**
   * Gets the ordered primary key columns of a table.
   *
   * @param table Table
   * @return Primary key columns, empty if the table has no primary key
   */
  public List<Column> primaryKeyColumns(final Table table) {
    if (table == null || !table.hasPrimaryKey()) {
      return List.of();
    }
    return List.copyOf(table.getPrimaryKey().getConstrainedColumns());
  }

  /**
   * Gets the parent tables referenced by a table's imported foreign keys.
   *
   * @param table Table
   * @return Referenced tables, or an empty collection when the table is {@code null}
   */
  public Collection<Table> referencedTables(final Table table) {
    return lookup(referencedTablesByTable, table);
  }

  /**
   * Gets the child tables that reference a table via their imported foreign keys.
   *
   * @param table Table
   * @return Referencing tables, or an empty collection when the table is {@code null}
   */
  public Collection<Table> referencingTables(final Table table) {
    return lookup(referencingTablesByTable, table);
  }

  /**
   * Gets the ER model relationship type for a table reference.
   *
   * @param tableReference Table reference
   * @return Relationship cardinality, empty when no ER model is available
   */
  public Optional<RelationshipCardinality> relationshipType(final TableReference tableReference) {
    if (tableReference == null || !hasERModel()) {
      return Optional.empty();
    }
    return getERModel().lookupRelationship(tableReference).map(Relationship::getType);
  }

  /**
   * Gets the deduplicated number of routines in the catalog.
   *
   * @return Routine count
   */
  public int routineCount() {
    return getCatalog().getRoutines().size();
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

  /**
   * Gets the unique identifier of the SchemaCrawler run that produced the catalog.
   *
   * @return Run id
   */
  public String runId() {
    return getCatalog().getCrawlInfo().getRunId();
  }

  /**
   * Gets the timestamp of the SchemaCrawler run that produced the catalog.
   *
   * @return Run timestamp
   */
  public Instant runTimestamp() {
    return getCatalog().getCrawlInfo().getCrawlTimestampInstant();
  }

  public String sentenceCase(final String text) {
    if (isBlank(text)) {
      return "";
    }
    final String sentence = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    return sentence;
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
    int count = 0;
    for (final Table table : getCatalog().getTables()) {
      if (!isView(table)) {
        count++;
      }
    }
    return count;
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
   * Gets the views that use a table.
   *
   * @param table Table
   * @return Views that use the table
   */
  public Collection<Table> usedByViews(final Table table) {
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

  /**
   * Gets the number of views in the catalog.
   *
   * @return View count
   */
  public int viewCount() {
    int count = 0;
    for (final Table table : getCatalog().getTables()) {
      if (isView(table)) {
        count++;
      }
    }
    return count;
  }

  private String encodeFullName(final DatabaseObject databaseObject) {
    if (databaseObject == null) {
      return "";
    }
    return URLEncoder.encode(databaseObject.getFullName(), StandardCharsets.UTF_8)
        .replace("+", "%20");
  }

  private boolean isMarkdownSpecial(final int cp) {
    return cp == '\\'
        || cp == '`'
        || cp == '*'
        || cp == '_'
        || cp == '{'
        || cp == '}'
        || cp == '['
        || cp == ']'
        || cp == '('
        || cp == ')'
        || cp == '#'
        || cp == '+'
        || cp == '-'
        || cp == '.'
        || cp == '!'
        || cp == '|'
        || cp == '>';
  }

  private <T> Collection<T> lookup(final Map<NamedObjectKey, List<T>> map, final Table table) {
    if (table == null) {
      return List.of();
    }
    return map.getOrDefault(table.key(), List.of());
  }

  private List<Table> sortedTables(final Predicate<Table> filter) {
    final List<Table> tables = new ArrayList<>();
    for (final Table table : getCatalog().getTables()) {
      if (filter.test(table)) {
        tables.add(table);
      }
    }
    tables.sort(Comparator.comparing(Table::getFullName));
    return List.copyOf(tables);
  }
}
