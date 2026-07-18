/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.renderer;

import static schemacrawler.scribe.renderer.JsonUtility.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToLongFunction;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schema.TypedObject;
import schemacrawler.utility.MetaDataUtility;

final class ScribeObjectFrontMatterBuilder {

  private ScribeObjectFrontMatterBuilder() {}

  static String build(
      final DatabaseObject object,
      final Optional<Catalog> catalog,
      final Optional<ERModel> erModel,
      final ToLongFunction<Table> rowCountLookup) {
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
    } else {
      frontMatter.put("description", "Description of " + simpleTypeName);
    }
    if (object instanceof final TypedObject typedObject) {
      frontMatter.put("complete_type", typedObject.getType().toString());
    }
    frontMatter.put("schema", object.getSchema().getFullName());
    frontMatter.put("name", object.getName());

    if (object instanceof final Table table) {
      frontMatter.put("resource", "catalog://tables/" + ScribeFormatting.encodeFullName(object));
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

      final long rowCount = rowCountLookup.applyAsLong(table);
      if (rowCount >= 0) {
        frontMatter.put("row_count", rowCount);
        if (rowCount == 0) {
          tags.add("empty_table");
        }
      }

      if (erModel.isPresent()) {
        final ERModel model = erModel.get();
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
    }

    if (object instanceof final Routine routine) {
      frontMatter.put("resource", "catalog://routines/" + ScribeFormatting.encodeFullName(object));
      frontMatter.put("parameter_count", routine.getParameters().size());
    }

    frontMatter.put("tags", tags);

    if (catalog.isPresent()) {
      final CrawlInfo crawlInfo = catalog.get().getCrawlInfo();
      frontMatter.put("timestamp", crawlInfo.getCrawlTimestamp());
      frontMatter.put("run_id", crawlInfo.getRunId());
    }

    return mapper.writeValueAsString(frontMatter);
  }
}
