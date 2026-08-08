/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static java.util.Objects.requireNonNull;

public enum OkfVerifiedBy {
  unverified("unverified"),
  machine_confirmed("machine-confirmed"),
  human_reviewed("human-reviewed");

  private final String value;

  OkfVerifiedBy(final String value) {
    this.value = value;
  }

  public static OkfVerifiedBy fromString(final String value) {
    final String normalized = requireNonNull(value, "No verified.by trust tier provided").trim();
    for (final OkfVerifiedBy verifiedBy : values()) {
      if (verifiedBy.value.equalsIgnoreCase(normalized)
          || verifiedBy.name().equalsIgnoreCase(normalized.replace('-', '_'))) {
        return verifiedBy;
      }
    }
    throw new IllegalArgumentException("Unknown verified.by trust tier: " + value);
  }

  @Override
  public String toString() {
    return value;
  }
}
