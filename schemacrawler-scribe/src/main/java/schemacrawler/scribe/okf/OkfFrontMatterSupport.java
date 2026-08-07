/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.scribe.renderer.JsonUtility.yamlMapper;
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
import schemacrawler.schemacrawler.Version;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.utility.MetaDataUtility;

public final class OkfFrontMatterSupport extends AbstractExecutionState {

  public String frontMatter(final Routine routine) {
    final Map<String, Object> frontMatter = build(routine);
    return yamlMapper.writeValueAsString(frontMatter);
  }

  public String frontMatter(final Table table) {
    final Map<String, Object> frontMatter = build(table);
    return yamlMapper.writeValueAsString(frontMatter);
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
      frontMatter.put("runId", crawlInfo.getRunId());
      frontMatter.put("generatedBy", Version.version().toString());
    }

    // GitHub tags
    // https://docs.github.com/en/contributing/writing-for-github-docs/using-yaml-frontmatter
    frontMatter.put("shortTitle", title);
    frontMatter.put("intro", description);
    frontMatter.put("showMiniToc", false);
    frontMatter.put("allowTitleToDifferFromFilename", true);

    return yamlMapper.writeValueAsString(frontMatter);
  }

  private Map<String, Object> build(final Routine routine) {

    final Map<String, Object> frontMatter = buildBasicDatabaseObjectMap(routine);
    if (routine == null) {
      return frontMatter;
    }

    final List<String> tags = new ArrayList<>((List<String>) frontMatter.get("tags"));

    frontMatter.put("resource", "catalog://routines/" + encodeFullName(routine));

    final Map<String, Object> counts = new LinkedHashMap<>();
    counts.put("parameterCount", routine.getParameters().size());

    frontMatter.put("counts", counts);

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
      tags.add("noPrimaryKey");
    }
    if (table.isSelfReferencing()) {
      tags.add("selfReferencing");
    }
    if (table.hasTriggers()) {
      tags.add("hasTriggers");
    }

    final Map<String, Object> counts = new LinkedHashMap<>();
    counts.put("columnCount", table.getColumns().size());
    counts.put("foreignKeyCount", table.getReferencedTables().size());
    counts.put("indexCount", table.getIndexes().size());
    counts.put("triggerCount", table.getTriggers().size());

    if (TableRowCountsUtility.hasRowCount(table)) {
      final long rowCount = TableRowCountsUtility.getRowCount(table);
      if (rowCount == 0) {
        tags.add("emptyTable");
      }
      counts.put("rowCount", rowCount);
    }
    frontMatter.put("counts", counts);

    if (hasERModel()) {
      final ERModel model = getERModel();
      final Optional<Entity> lookupEntity = model.lookupEntity(table);
      if (lookupEntity.isPresent()) {
        final Entity entity = lookupEntity.get();
        final EntityType entityType = entity.getType();
        if (entityType != EntityType.unknown) {
          frontMatter.put("entityType", entityType.description());
          tags.add(entityType.name());
        }
      }
      if (model.lookupByBridgeTable(table).isPresent()) {
        tags.add("bridgeTable");
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
      frontMatter.put("completeType", typedObject.getType().toString());
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
      frontMatter.put("runId", crawlInfo.getRunId());
      frontMatter.put("generatedBy", Version.version().toString());
    }

    return frontMatter;
  }
}
