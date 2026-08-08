/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static us.fatehi.utility.Utility.trimToEmpty;

public record GitHubPagesFrontMatterRecord(
    String shortTitle, String intro, boolean showMiniToc, boolean allowTitleToDifferFromFilename) {

  public GitHubPagesFrontMatterRecord {
    shortTitle = trimToEmpty(shortTitle);
    intro = trimToEmpty(intro);
  }
}
