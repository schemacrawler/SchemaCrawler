/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.trimToEmpty;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SchemaCrawlerFrontMatterRecord(
    String schema,
    String name,
    String completeType,
    SchemaCrawlerCountsRecord counts,
    String entityType) {

  public SchemaCrawlerFrontMatterRecord {
    schema = trimToEmpty(schema);
    name = trimToEmpty(name);
    completeType = trimToEmpty(completeType);
    entityType = trimToEmpty(entityType);
  }

  public SchemaCrawlerFrontMatterRecord(
      final DatabaseObjectDescription objectDescription,
      final SchemaCrawlerCountsRecord counts,
      final String entityType) {
    this(
        requireNonNull(objectDescription, "No database object description provided").schemaName(),
        objectDescription.name(),
        objectDescription.completeType(),
        counts,
        entityType);
  }
}
