/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.scribe.renderer.JsonUtility.mapper;
import static schemacrawler.scribe.renderer.MarkdownFormattingHelper.encodeFullName;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.loader.utility.TableRowCountsUtility;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schema.TypedObject;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.utility.MetaDataUtility;

public final class OkfFrontMatterSupport extends AbstractExecutionState {

  public String frontMatter(final Routine routine) {
    final Map<String, Object> frontMatter = build(routine);
    return mapper.writeValueAsString(frontMatter);
  }

  public String frontMatter(final Table table) {
    final Map<String, Object> frontMatter = build(table);
    return mapper.writeValueAsString(frontMatter);
  }

  public String reportFrontMatter(final String providedTitle, final String providedDescription) {

    final String title = isBlank(providedTitle) ? "Report" : providedTitle;
    final String description = isBlank(providedDescription) ? "Report" : providedDescription;

    final Map<String, Object> frontMatter = new LinkedHashMap<>();
    frontMatter.put("type", "report");
    frontMatter.put("title", title);
    frontMatter.put("description", description);

    if (hasCatalog()) {
      final CrawlInfo crawlInfo = getCatalog().getCrawlInfo();
      frontMatter.put("timestamp", crawlInfo.getCrawlTimestamp());
      frontMatter.put("run_id", crawlInfo.getRunId());
    }

    return mapper.writeValueAsString(frontMatter);
  }

  private Map<String, Object> build(final Routine routine) {

    final Map<String, Object> frontMatter = buildBasicDatabaseObjectMap(routine);
    if (routine == null) {
      return frontMatter;
    }

    final List<String> tags = new ArrayList<>((List<String>) frontMatter.get("tags"));

    frontMatter.put("resource", "catalog://routines/" + encodeFullName(routine));
    frontMatter.put("parameter_count", routine.getParameters().size());

    frontMatter.put("tags", tags);

    return frontMatter;
  }

  private Map<String, Object> build(final Table table) {

    final Map<String, Object> frontMatter = buildBasicDatabaseObjectMap(table);
    if (table == null) {
      return frontMatter;
    }

    final List<String> tags = new ArrayList<>((List<String>) frontMatter.get("tags"));

    frontMatter.put("resource", "catalog://tables/" + encodeFullName(table));
    if (!table.hasPrimaryKey()) {
      tags.add("no_primary_key");
    }
    if (table.isSelfReferencing()) {
      tags.add("self_referencing");
    }
    if (table.hasTriggers()) {
      tags.add("has_triggers");
    }

    final Map<String, Object> counts = new LinkedHashMap<>();
    counts.put("column_count", table.getColumns().size());
    counts.put("foreign_key_count", table.getReferencedTables().size());
    counts.put("index_count", table.getIndexes().size());
    counts.put("trigger_count", table.getTriggers().size());

    if (TableRowCountsUtility.hasRowCount(table)) {
      final long rowCount = TableRowCountsUtility.getRowCount(table);
      if (rowCount == 0) {
        tags.add("empty_table");
      }
      counts.put("row_count", rowCount);
    }
    frontMatter.put("counts", counts);

    if (hasERModel()) {
      final ERModel model = getERModel();
      final Optional<Entity> lookupEntity = model.lookupEntity(table);
      if (lookupEntity.isPresent()) {
        final Entity entity = lookupEntity.get();
        final EntityType entityType = entity.getType();
        if (entityType != EntityType.unknown) {
          frontMatter.put("entity_type", entityType.description());
          tags.add(entityType.name());
        }
      }
      if (model.lookupByBridgeTable(table).isPresent()) {
        tags.add("bridge_table");
      }
    }

    frontMatter.put("tags", tags);

    return frontMatter;
  }

  private Map<String, Object> buildBasicDatabaseObjectMap(final DatabaseObject object) {

    final Map<String, Object> frontMatter = new LinkedHashMap<>();
    if (object == null) {
      return frontMatter;
    }

    final String simpleTypeName = MetaDataUtility.getSimpleTypeName(object).toString();
    final String name = object.getName();
    final String fullName = object.getFullName();
    final String description;
    if (object.hasRemarks()) {
      description = object.getRemarks();
    } else {
      description = "Description of %s %s".formatted(simpleTypeName, fullName);
    }

    final List<String> tags = new ArrayList<>();

    tags.add(simpleTypeName);
    frontMatter.put("type", simpleTypeName);
    frontMatter.put("title", fullName);
    if (object.hasRemarks()) {
      frontMatter.put("description", description);
    } else {
      frontMatter.put("description", "Description of " + simpleTypeName);
    }
    if (object instanceof final TypedObject typedObject) {
      frontMatter.put("complete_type", typedObject.getType().toString());
    }
    frontMatter.put("schema", object.getSchema().getFullName());
    frontMatter.put("name", name);

    // GitHub tags
    // https://docs.github.com/en/contributing/writing-for-github-docs/using-yaml-frontmatter
    frontMatter.put("shortTitle", name);
    frontMatter.put("intro", description);
    frontMatter.put("showMiniToc", true);
    frontMatter.put("allowTitleToDifferFromFilename", true);

    frontMatter.put("tags", tags);

    if (hasCatalog()) {
      final CrawlInfo crawlInfo = getCatalog().getCrawlInfo();
      frontMatter.put("timestamp", crawlInfo.getCrawlTimestamp());
      frontMatter.put("run_id", crawlInfo.getRunId());
    }

    return frontMatter;
  }
}
