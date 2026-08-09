/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static us.fatehi.utility.Utility.requireNotBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.time.Instant;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OkfVerifiedRecord(String by, Instant at) {
  public OkfVerifiedRecord {
    by = requireNotBlank(trimToEmpty(by), "No verified.by value provided");
    at = at == null ? Instant.now() : at;
    validateBy(by);
  }

  public OkfVerifiedRecord(final OkfTrustTier trustTier, final String actor) {
    this("%s:%s".formatted(trustTier, actor), null);
  }

  private static void validateBy(final String by) {
    final String[] verifiedByParts = by.split(":", 2);
    if (verifiedByParts.length != 2) {
      throw new IllegalArgumentException("verified.by should include trust tier and actor");
    }
    OkfTrustTier.fromString(verifiedByParts[0]);
    requireNotBlank(trimToEmpty(verifiedByParts[1]), "No verified actor provided");
  }
}
