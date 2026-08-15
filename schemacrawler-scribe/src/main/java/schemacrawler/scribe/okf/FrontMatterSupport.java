/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.scribe.okf.FrontMatterUtility.objectDescription;
import static schemacrawler.scribe.okf.FrontMatterUtility.verified;
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
import schemacrawler.scribe.okf.frontmatter.github.GitHubPagesFrontMatter;
import schemacrawler.scribe.okf.frontmatter.okf.Actor;
import schemacrawler.scribe.okf.frontmatter.okf.Generated;
import schemacrawler.scribe.okf.frontmatter.okf.LifecycleStatus;
import schemacrawler.scribe.okf.frontmatter.okf.OkfFrontMatter;
import schemacrawler.scribe.okf.frontmatter.okf.SchemaCrawlerActor;
import schemacrawler.scribe.okf.frontmatter.schemacrawler.Counts;
import schemacrawler.scribe.okf.frontmatter.schemacrawler.SchemaCrawlerFrontMatter;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.tools.utility.TableTraits;

public final class FrontMatterSupport extends AbstractExecutionState {

  private static void addTag(final List<String> tags, final String tag) {
    tags.add(toSnakeCase(tag));
  }

  private final FrontMatterYamlUtility frontMatterYamlUtility;

  public FrontMatterSupport() {
    frontMatterYamlUtility = new FrontMatterYamlUtility();
  }

  public String frontMatter(final Routine routine) {
    final DatabaseObjectDescription objectDescription = objectDescription(routine);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());

    final OkfFrontMatter okfFrontMatter =
        new OkfFrontMatter(
            objectDescription, tags, generated(), verified(), LifecycleStatus.stable);
    final GitHubPagesFrontMatter gitHubPagesFrontMatter =
        new GitHubPagesFrontMatter(objectDescription, true, true);
    final SchemaCrawlerFrontMatter schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatter(objectDescription, null, null);

    return frontMatterYamlUtility.toYamlString(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
  }

  public String frontMatter(final Table table) {
    final DatabaseObjectDescription objectDescription = objectDescription(table);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());
    final TableTraits tableAttributes = TableTraits.from(table);
    tags.addAll(frontMatterYamlUtility.toTags(tableAttributes));

    final String entityType = entityType(table, tags);

    final OkfFrontMatter okfFrontMatter =
        new OkfFrontMatter(
            objectDescription, tags, generated(), verified(), LifecycleStatus.stable);
    final GitHubPagesFrontMatter gitHubPagesFrontMatter =
        new GitHubPagesFrontMatter(objectDescription, true, true);
    final SchemaCrawlerFrontMatter schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatter(objectDescription, Counts.from(table), entityType);

    return frontMatterYamlUtility.toYamlString(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
  }

  public String reportFrontMatter(final String providedTitle, final String providedDescription) {

    final String title = isBlank(providedTitle) ? "Report" : providedTitle;
    final String description = isBlank(providedDescription) ? "Report" : providedDescription;

    final OkfFrontMatter okfFrontMatter =
        new OkfFrontMatter(
            "report",
            title,
            description,
            null,
            List.of(),
            generated(),
            verified(),
            LifecycleStatus.stable);
    final GitHubPagesFrontMatter gitHubPagesFrontMatter =
        new GitHubPagesFrontMatter(title, description, false, true);

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

  private Generated generated() {
    if (!hasCatalog()) {
      return null;
    }

    final CrawlInfo crawlInfo = getCatalog().getCrawlInfo();
    final Instant crawlTimestamp = crawlInfo.getCrawlTimestampInstant();

    final Actor scActor = new SchemaCrawlerActor().toActor();
    return new Generated(scActor, crawlTimestamp);
  }
}
