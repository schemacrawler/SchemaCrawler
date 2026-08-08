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
import schemacrawler.scribe.okf.frontmatter.TableAttributesRecord;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.utility.MetaDataUtility;

public final class OkfFrontMatterSupport extends AbstractExecutionState {

  private record DatabaseObjectDescription(
      String simpleTypeName,
      String completeType,
      String name,
      String fullName,
      String description,
      String intro,
      URI resource) {

    public DatabaseObjectDescription() {
      this(null, null, null, null, null, null, null);
    }

    public static DatabaseObjectDescription of(final DatabaseObject databaseObject) {
      if (databaseObject == null) {
        return new DatabaseObjectDescription();
      }
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

      final String completeType;
      if (databaseObject instanceof final TypedObject typedObject) {
        completeType = typedObject.getType().toString();
      } else {
        completeType = null;
      }

      URI resource;
      try {
        final String context;
        if (databaseObject instanceof Table) {
          context = "tables";
        } else if (databaseObject instanceof Routine) {
          context = "routines";
        } else {
          context = "unknowns";
        }
        final String path = "/" + String.join("/", databaseObject.getSchema().toString(), name);
        resource = new URI("catalog", context, path, null);
      } catch (final URISyntaxException e) {
        resource = null;
      }

      return new DatabaseObjectDescription(
          simpleTypeName, completeType, name, fullName, description, intro, resource);
    }
  }

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
    final DatabaseObjectDescription objectDescription = DatabaseObjectDescription.of(routine);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());

    final OkfFrontMatterRecord okfFrontMatter = okfFrontMatter(objectDescription, tags);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        gitHubPagesFrontMatter(objectDescription, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        schemaCrawlerFrontMatter(
            routine.getSchema().getFullName(),
            objectDescription.name(),
            objectDescription.completeType(),
            new SchemaCrawlerCountsRecord(
                null, null, null, null, null, routine.getParameters().size()),
            null);

    return frontMatterYamlUtility.toYamlString(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
  }

  private String buildForTable(final Table table) {
    final DatabaseObjectDescription objectDescription = DatabaseObjectDescription.of(table);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());
    final TableAttributesRecord tableAttributes =
        TableAttributesRecord.of(table, isBridgeTable(table));
    tags.addAll(frontMatterYamlUtility.toTags(tableAttributes));

    final String entityType = entityType(table, tags);
    final Long rowCount =
        TableRowCountsUtility.hasRowCount(table) ? TableRowCountsUtility.getRowCount(table) : null;

    final OkfFrontMatterRecord okfFrontMatter = okfFrontMatter(objectDescription, tags);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        gitHubPagesFrontMatter(objectDescription, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        schemaCrawlerFrontMatter(
            table.getSchema().getFullName(),
            objectDescription.name(),
            objectDescription.completeType(),
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
        return entityType.description();
      }
    }

    return null;
  }

  private boolean isBridgeTable(final Table table) {
    if (!hasERModel()) {
      return false;
    }
    final ERModel model = getERModel();
    return model.lookupByBridgeTable(table).isPresent();
  }

  private OkfGeneratedRecord generated() {
    if (!hasCatalog()) {
      return null;
    }

    final CrawlInfo crawlInfo = getCatalog().getCrawlInfo();
    final Instant crawlTimestamp = crawlInfo.getCrawlTimestampInstant();

    return new OkfGeneratedRecord(schemaCrawlerActor(), crawlTimestamp);
  }

  private GitHubPagesFrontMatterRecord gitHubPagesFrontMatter(
      final DatabaseObjectDescription objectDescription, final boolean showMiniToc) {
    return new GitHubPagesFrontMatterRecord(
        objectDescription.name(), objectDescription.intro(), showMiniToc, true);
  }

  private OkfFrontMatterRecord okfFrontMatter(
      final DatabaseObjectDescription objectDescription, final List<String> tags) {
    return new OkfFrontMatterRecord(
        objectDescription.simpleTypeName(),
        objectDescription.fullName(),
        objectDescription.description(),
        objectDescription.resource(),
        tags,
        generated(),
        verified(),
        OkfStatus.stable);
  }

  private SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter(
      final String schema,
      final String name,
      final String completeType,
      final SchemaCrawlerCountsRecord counts,
      final String entityType) {
    return new SchemaCrawlerFrontMatterRecord(schema, name, completeType, counts, entityType);
  }

  private OkfVerifiedRecord verified() {
    return OkfVerifiedRecord.of(
        OkfVerifiedBy.machine_confirmed, schemaCrawlerActor(), Instant.now());
  }

  private static void addTag(final List<String> tags, final String tag) {
    tags.add(toSnakeCase(tag));
  }
}
