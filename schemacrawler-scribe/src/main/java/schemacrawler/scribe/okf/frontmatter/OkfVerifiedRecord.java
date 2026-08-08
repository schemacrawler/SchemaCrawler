/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.time.Instant;

public record OkfVerifiedRecord(String by, Instant at) {
  public OkfVerifiedRecord {
    by = requireNotBlank(trimToEmpty(by), "No verified.by value provided");
    at = requireNonNull(at, "No verified.at value provided");
    validateBy(by);
  }

  public static OkfVerifiedRecord of(
      final OkfVerifiedBy verifiedBy, final String actor, final Instant at) {
    return new OkfVerifiedRecord(
        requireNonNull(verifiedBy, "No verified.by trust tier provided").format(actor), at);
  }

  private static void validateBy(final String by) {
    final String[] verifiedByParts = by.split(":", 2);
    if (verifiedByParts.length != 2) {
      throw new IllegalArgumentException("verified.by should include trust tier and actor");
    }
    OkfVerifiedBy.fromString(verifiedByParts[0]);
    requireNotBlank(trimToEmpty(verifiedByParts[1]), "No verified actor provided");
  }
}
