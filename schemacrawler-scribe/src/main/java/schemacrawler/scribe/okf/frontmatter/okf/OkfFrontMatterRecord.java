/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.okf;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import static us.fatehi.utility.Utility.toSnakeCase;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import schemacrawler.scribe.okf.frontmatter.DatabaseObjectDescription;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OkfFrontMatterRecord(
    String type,
    String title,
    String description,
    URI resource,
    List<String> tags,
    GeneratedRecord generated,
    VerifiedRecord verified,
    LifecycleStatus status) {

  public OkfFrontMatterRecord {
    type = requireNotBlank(trimToEmpty(type), "No type provided");
    title = requireNotBlank(trimToEmpty(title), "No title provided");
    description = trimToEmpty(description);
    tags = normalizeTags(tags);
    verified = requireNonNull(verified, "No verified front-matter provided");
    status = status == null ? LifecycleStatus.stable : status;
  }

  public OkfFrontMatterRecord(
      final DatabaseObjectDescription objectDescription,
      final List<String> tags,
      final GeneratedRecord generated,
      final VerifiedRecord verified,
      final LifecycleStatus status) {
    this(
        requireNonNull(objectDescription, "No database object description provided")
            .simpleTypeName(),
        objectDescription.fullName(),
        objectDescription.description(),
        objectDescription.resource(),
        tags,
        generated,
        verified,
        status);
  }

  private static List<String> normalizeTags(final List<String> tags) {
    final List<String> safeTags = tags == null ? List.of() : List.copyOf(tags);
    final Set<String> uniqueTags = new LinkedHashSet<>();
    for (final String tag : safeTags) {
      uniqueTags.add(toSnakeCase(requireNotBlank(trimToEmpty(tag), "Tag should be non-blank")));
    }
    return List.copyOf(uniqueTags);
  }
}
