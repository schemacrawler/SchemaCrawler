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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.loader.utility.TableRowCountsUtility;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.scribe.okf.frontmatter.DatabaseObjectDescription;
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

public final class OkfFrontMatterSupport extends AbstractExecutionState {

  private static void addTag(final List<String> tags, final String tag) {
    tags.add(toSnakeCase(tag));
  }

  private final OkfFrontMatterYamlUtility frontMatterYamlUtility;

  public OkfFrontMatterSupport() {
    frontMatterYamlUtility = new OkfFrontMatterYamlUtility();
  }

  public String frontMatter(final Routine routine) {
    final DatabaseObjectDescription objectDescription = DatabaseObjectDescription.of(routine);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());

    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            objectDescription, tags, generated(), verified(), OkfStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord(objectDescription, true, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatterRecord(
            objectDescription,
            new SchemaCrawlerCountsRecord(
                null, null, null, null, null, routine.getParameters().size()),
            null);

    return frontMatterYamlUtility.toYamlString(
        okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
  }

  public String frontMatter(final Table table) {
    final DatabaseObjectDescription objectDescription = DatabaseObjectDescription.of(table);

    final List<String> tags = new ArrayList<>();
    addTag(tags, objectDescription.simpleTypeName());
    final TableAttributesRecord tableAttributes =
        TableAttributesRecord.of(table, isBridgeTable(table));
    tags.addAll(frontMatterYamlUtility.toTags(tableAttributes));

    final String entityType = entityType(table, tags);
    final Long rowCount =
        TableRowCountsUtility.hasRowCount(table) ? TableRowCountsUtility.getRowCount(table) : null;

    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            objectDescription, tags, generated(), verified(), OkfStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord(objectDescription, true, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatterRecord(
            objectDescription,
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

    return new OkfGeneratedRecord(schemaCrawlerActor(), crawlTimestamp);
  }

  private boolean isBridgeTable(final Table table) {
    if (!hasERModel()) {
      return false;
    }
    final ERModel model = getERModel();
    return model.lookupByBridgeTable(table).isPresent();
  }

  private OkfVerifiedRecord verified() {
    return OkfVerifiedRecord.of(
        OkfVerifiedBy.machine_confirmed, schemaCrawlerActor(), Instant.now());
  }
}
