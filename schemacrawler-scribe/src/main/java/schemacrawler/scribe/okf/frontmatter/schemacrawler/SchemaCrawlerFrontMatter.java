/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.schemacrawler;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.trimToEmpty;

import schemacrawler.scribe.okf.frontmatter.DatabaseObjectDescription;
import schemacrawler.tools.utility.TableCounts;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SchemaCrawlerFrontMatter(
    String schema, String name, String completeType, TableCounts counts, String entityType) {

  public SchemaCrawlerFrontMatter {
    schema = trimToEmpty(schema);
    name = trimToEmpty(name);
    completeType = trimToEmpty(completeType);
    entityType = trimToEmpty(entityType);
  }

  public SchemaCrawlerFrontMatter(
      final DatabaseObjectDescription objectDescription,
      final TableCounts counts,
      final String entityType) {
    this(
        requireNonNull(objectDescription, "No database object description provided").schemaName(),
        objectDescription.name(),
        objectDescription.completeType(),
        counts,
        entityType);
  }
}
