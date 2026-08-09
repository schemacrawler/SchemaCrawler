/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.scribe.okf.OkfFrontMatterUtility.objectDescription;
import static schemacrawler.scribe.okf.OkfFrontMatterUtility.tableAttributes;
import static schemacrawler.scribe.okf.OkfFrontMatterUtility.verified;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.toSnakeCase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.scribe.okf.frontmatter.DatabaseObjectDescription;
import schemacrawler.scribe.okf.frontmatter.GitHubPagesFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.OkfFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.OkfGeneratedRecord;
import schemacrawler.scribe.okf.frontmatter.OkfStatus;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerActor;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.TableAttributesRecord;
import schemacrawler.tools.state.AbstractExecutionState;

public final class OkfFrontMatterSupport extends AbstractExecutionState {

  private static void addTag(final List<String> tags, final String tag) {
    tags.add(toSnakeCase(tag));
  }

  private final OkfFrontMatterYamlUtility frontMatterYamlUtility;

  public OkfFrontMatterSupport() {
    frontMatterYamlUtility = new OkfFrontMatterYamlUtility();
  }

  public String frontMatter(final Routine routine) {
    final DatabaseObjectDescription objectDescription = objectDescription(routine);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());

    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            objectDescription, tags, generated(), verified(), OkfStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord(objectDescription, true, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatterRecord(
            objectDescription, OkfFrontMatterUtility.routineCounts(routine), null);

    return frontMatterYamlUtility.toYamlString(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
  }

  public String frontMatter(final Table table) {
    final DatabaseObjectDescription objectDescription = objectDescription(table);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());
    final TableAttributesRecord tableAttributes = tableAttributes(table, isBridgeTable(table));
    tags.addAll(frontMatterYamlUtility.toTags(tableAttributes));

    final String entityType = entityType(table, tags);

    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            objectDescription, tags, generated(), verified(), OkfStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord(objectDescription, true, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatterRecord(
            objectDescription, OkfFrontMatterUtility.tableCounts(table), entityType);

    return frontMatterYamlUtility.toYamlString(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
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

  private OkfGeneratedRecord generated() {
    if (!hasCatalog()) {
      return null;
    }

    final CrawlInfo crawlInfo = getCatalog().getCrawlInfo();
    final Instant crawlTimestamp = crawlInfo.getCrawlTimestampInstant();

    return new OkfGeneratedRecord(new SchemaCrawlerActor(), crawlTimestamp);
  }

  private boolean isBridgeTable(final Table table) {
    if (!hasERModel()) {
      return false;
    }
    final ERModel model = getERModel();
    return model.lookupByBridgeTable(table).isPresent();
  }
}
