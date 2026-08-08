/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.scribe.okf.frontmatter.SchemaCrawlerActor.schemaCrawlerActor;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.toSnakeCase;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
import schemacrawler.scribe.okf.frontmatter.GitHubPagesFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.OkfFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.OkfGeneratedRecord;
import schemacrawler.scribe.okf.frontmatter.OkfStatus;
import schemacrawler.scribe.okf.frontmatter.OkfVerifiedBy;
import schemacrawler.scribe.okf.frontmatter.OkfVerifiedRecord;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerCountsRecord;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerFrontMatterRecord;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.utility.MetaDataUtility;

public final class OkfFrontMatterSupport extends AbstractExecutionState {

  private static final String TAG_NO_PRIMARY_KEY = "no_primary_key";
  private static final String TAG_SELF_REFERENCING = "self_referencing";
  private static final String TAG_HAS_TRIGGERS = "has_triggers";
  private static final String TAG_EMPTY_TABLE = "empty_table";
  private static final String TAG_BRIDGE_TABLE = "bridge_table";

  private final OkfFrontMatterYamlUtility frontMatterYamlUtility;

  public OkfFrontMatterSupport() {
    frontMatterYamlUtility = new OkfFrontMatterYamlUtility();
  }

  public String frontMatter(final Routine routine) {
    return buildForRoutine(routine);
  }

  public String frontMatter(final Table table) {
    return buildForTable(table);
  }

  public String reportFrontMatter(final String providedTitle, final String providedDescription) {

    final String title = isBlank(providedTitle) ? "Report" : providedTitle;
    final String description = isBlank(providedDescription) ? "Report" : providedDescription;

    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            "report",
            title,
            description,
            null,
            List.of(),
            generated(),
            verified(),
            OkfStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord(title, description, false, true);

    return frontMatterYamlUtility.toYamlString(okfFrontMatter, gitHubPagesFrontMatter, null);
  }

  private String buildForRoutine(final Routine routine) {
    final DatabaseObjectDescription objectDescription = databaseObjectDescription(routine);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());

    final OkfFrontMatterRecord okfFrontMatter =
        okfFrontMatter(objectDescription, resourceFor("routines", routine.getFullName()), tags);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        gitHubPagesFrontMatter(objectDescription, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        schemaCrawlerFrontMatter(
            routine.getSchema().getFullName(),
            objectDescription.name(),
            completeType(routine),
            new SchemaCrawlerCountsRecord(
                null, null, null, null, null, routine.getParameters().size()),
            null);

    return frontMatterYamlUtility.toYamlString(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
  }

  private String buildForTable(final Table table) {
    final DatabaseObjectDescription objectDescription = databaseObjectDescription(table);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());

    if (!table.hasPrimaryKey()) {
      addTag(tags, TAG_NO_PRIMARY_KEY);
    }
    if (table.isSelfReferencing()) {
      addTag(tags, TAG_SELF_REFERENCING);
    }
    if (table.hasTriggers()) {
      addTag(tags, TAG_HAS_TRIGGERS);
    }

    final Long rowCount;
    if (TableRowCountsUtility.hasRowCount(table)) {
      rowCount = TableRowCountsUtility.getRowCount(table);
      if (rowCount == 0) {
        addTag(tags, TAG_EMPTY_TABLE);
      }
    } else {
      rowCount = null;
    }

    final String entityType = entityType(table, tags);

    final OkfFrontMatterRecord okfFrontMatter =
        okfFrontMatter(objectDescription, resourceFor("tables", table.getFullName()), tags);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        gitHubPagesFrontMatter(objectDescription, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        schemaCrawlerFrontMatter(
            table.getSchema().getFullName(),
            objectDescription.name(),
            completeType(table),
            new SchemaCrawlerCountsRecord(
                table.getColumns().size(),
                table.getReferencedTables().size(),
                table.getIndexes().size(),
                table.getTriggers().size(),
                rowCount,
                null),
            entityType);

    return frontMatterYamlUtility.toYamlString(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
  }

  private OkfFrontMatterRecord okfFrontMatter(
      final DatabaseObjectDescription objectDescription,
      final String resource,
      final List<String> tags) {
    return new OkfFrontMatterRecord(
        objectDescription.simpleTypeName(),
        objectDescription.fullName(),
        objectDescription.description(),
        resource,
        tags,
        generated(),
        verified(),
        OkfStatus.stable);
  }

  private GitHubPagesFrontMatterRecord gitHubPagesFrontMatter(
      final DatabaseObjectDescription objectDescription, final boolean showMiniToc) {
    return new GitHubPagesFrontMatterRecord(
        objectDescription.name(), objectDescription.intro(), showMiniToc, true);
  }

  private SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter(
      final String schema,
      final String name,
      final String completeType,
      final SchemaCrawlerCountsRecord counts,
      final String entityType) {
    return new SchemaCrawlerFrontMatterRecord(schema, name, completeType, counts, entityType);
  }

  private String completeType(final DatabaseObject databaseObject) {
    if (databaseObject instanceof final TypedObject typedObject) {
      return typedObject.getType().toString();
    }
    return null;
  }

  private DatabaseObjectDescription databaseObjectDescription(final DatabaseObject databaseObject) {
    final String simpleTypeName = MetaDataUtility.getSimpleTypeName(databaseObject).toString();
    final String name = databaseObject.getName();
    final String fullName = databaseObject.getFullName();

    final String intro;
    final String description;
    if (databaseObject.hasRemarks()) {
      intro = databaseObject.getRemarks();
      description = intro;
    } else {
      intro = "Description of %s %s".formatted(simpleTypeName, fullName);
      description = "Description of " + simpleTypeName;
    }

    return new DatabaseObjectDescription(simpleTypeName, name, fullName, description, intro);
  }

  private String entityType(final Table table, final List<String> tags) {
    if (!hasERModel()) {
      return null;
    }

    final ERModel model = getERModel();
    final Optional<Entity> lookupEntity = model.lookupEntity(table);
    if (lookupEntity.isPresent()) {
      final Entity entity = lookupEntity.get();
      final EntityType entityType = entity.getType();
      if (entityType != EntityType.unknown) {
        addTag(tags, entityType.name());
        if (model.lookupByBridgeTable(table).isPresent()) {
          addTag(tags, TAG_BRIDGE_TABLE);
        }
        return entityType.description();
      }
    }

    if (model.lookupByBridgeTable(table).isPresent()) {
      addTag(tags, TAG_BRIDGE_TABLE);
    }

    return null;
  }

  private OkfGeneratedRecord generated() {
    if (!hasCatalog()) {
      return null;
    }

    final CrawlInfo crawlInfo = getCatalog().getCrawlInfo();
    final Instant crawlTimestamp = crawlInfo.getCrawlTimestampInstant();

    return new OkfGeneratedRecord(schemaCrawlerActor(), crawlTimestamp);
  }

  private OkfVerifiedRecord verified() {
    return OkfVerifiedRecord.of(
        OkfVerifiedBy.machine_confirmed, schemaCrawlerActor(), Instant.now());
  }

  private String resourceFor(final String kind, final String fullName) {
    try {
      return new URI("catalog", kind, "/" + fullName, null).toString();
    } catch (final URISyntaxException e) {
      throw new IllegalArgumentException("Unable to create resource URI", e);
    }
  }

  private static void addTag(final List<String> tags, final String tag) {
    tags.add(toSnakeCase(tag));
  }

  private record DatabaseObjectDescription(
      String simpleTypeName, String name, String fullName, String description, String intro) {}
}
