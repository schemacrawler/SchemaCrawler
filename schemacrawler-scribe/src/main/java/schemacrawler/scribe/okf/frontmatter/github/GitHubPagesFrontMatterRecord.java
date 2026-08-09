/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.github;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.trimToEmpty;

import schemacrawler.scribe.okf.frontmatter.DatabaseObjectDescription;
import tools.jackson.databind.PropertyNamingStrategies.LowerCamelCaseStrategy;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(LowerCamelCaseStrategy.class)
public record GitHubPagesFrontMatterRecord(
    String shortTitle, String intro, boolean showMiniToc, boolean allowTitleToDifferFromFilename) {

  public GitHubPagesFrontMatterRecord {
    shortTitle = trimToEmpty(shortTitle);
    intro = trimToEmpty(intro);
  }

  public GitHubPagesFrontMatterRecord(
      final DatabaseObjectDescription objectDescription,
      final boolean showMiniToc,
      final boolean allowTitleToDifferFromFilename) {
    this(
        requireNonNull(objectDescription, "No database object description provided").name(),
        objectDescription.intro(),
        showMiniToc,
        allowTitleToDifferFromFilename);
  }
}
