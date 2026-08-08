/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.scribe.okf.frontmatter.SchemaCrawlerActor.schemaCrawlerActor;
import static schemacrawler.scribe.renderer.MarkdownFormattingHelper.encodeFullName;
import static us.fatehi.utility.Utility.isBlank;

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
import schemacrawler.scribe.okf.frontmatter.OkfVerifiedRecord;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerCountsRecord;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerFrontMatterRecord;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.utility.MetaDataUtility;

public final class OkfFrontMatterSupport extends AbstractExecutionState {

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

    return frontMatterYamlUtility.toYaml(okfFrontMatter, gitHubPagesFrontMatter, null);
  }

  private String buildForRoutine(final Routine routine) {
    final DatabaseObjectDescription objectDescription = databaseObjectDescription(routine);

    final List<String> tags = new ArrayList<>();
    tags.add(objectDescription.simpleTypeName());

    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            objectDescription.simpleTypeName(),
            objectDescription.fullName(),
            objectDescription.description(),
            "catalog://routines/" + encodeFullName(routine),
            tags,
            generated(),
            verified(),
            OkfStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord(
            objectDescription.name(), objectDescription.intro(), true, true);
    final SchemaCrawlerCountsRecord counts =
        new SchemaCrawlerCountsRecord(null, null, null, null, null, routine.getParameters().size());
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatterRecord(
            routine.getSchema().getFullName(),
            objectDescription.name(),
            completeType(routine),
            counts,
            null);

    return frontMatterYamlUtility.toYaml(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
  }

  private String buildForTable(final Table table) {
    final DatabaseObjectDescription objectDescription = databaseObjectDescription(table);

    final List<String> tags = new ArrayList<>();
    tags.add(objectDescription.simpleTypeName());

    if (!table.hasPrimaryKey()) {
      tags.add("noPrimaryKey");
    }
    if (table.isSelfReferencing()) {
      tags.add("selfReferencing");
    }
    if (table.hasTriggers()) {
      tags.add("hasTriggers");
    }

    final Long rowCount;
    if (TableRowCountsUtility.hasRowCount(table)) {
      rowCount = TableRowCountsUtility.getRowCount(table);
      if (rowCount == 0) {
        tags.add("emptyTable");
      }
    } else {
      rowCount = null;
    }

    final String entityType = entityType(table, tags);

    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            objectDescription.simpleTypeName(),
            objectDescription.fullName(),
            objectDescription.description(),
            "catalog://tables/" + encodeFullName(table),
            tags,
            generated(),
            verified(),
            OkfStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord(
            objectDescription.name(), objectDescription.intro(), true, true);
    final SchemaCrawlerCountsRecord counts =
        new SchemaCrawlerCountsRecord(
            table.getColumns().size(),
            table.getReferencedTables().size(),
            table.getIndexes().size(),
            table.getTriggers().size(),
            rowCount,
            null);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatterRecord(
            table.getSchema().getFullName(),
            objectDescription.name(),
            completeType(table),
            counts,
            entityType);

    return frontMatterYamlUtility.toYaml(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
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
        tags.add(entityType.name());
        if (model.lookupByBridgeTable(table).isPresent()) {
          tags.add("bridgeTable");
        }
        return entityType.description();
      }
    }

    if (model.lookupByBridgeTable(table).isPresent()) {
      tags.add("bridgeTable");
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
    return new OkfVerifiedRecord(schemaCrawlerActor(), Instant.now());
  }

  private record DatabaseObjectDescription(
      String simpleTypeName, String name, String fullName, String description, String intro) {}
}
