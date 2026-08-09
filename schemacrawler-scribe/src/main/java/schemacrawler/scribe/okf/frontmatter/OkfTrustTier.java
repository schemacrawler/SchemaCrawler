/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static java.util.Objects.requireNonNull;

public enum OkfTrustTier {
  unverified("unverified"),
  machine_confirmed("machine-confirmed"),
  human_reviewed("human-reviewed");

  private final String value;

  OkfTrustTier(final String value) {
    this.value = value;
  }

  public static OkfTrustTier fromString(final String value) {
    final String normalized = requireNonNull(value, "No trust tier provided").trim();
    for (final OkfTrustTier verifiedBy : values()) {
      if (verifiedBy.value.equalsIgnoreCase(normalized)
          || verifiedBy.name().equalsIgnoreCase(normalized.replace('-', '_'))) {
        return verifiedBy;
      }
    }
    throw new IllegalArgumentException("Unknown trust tier: " + value);
  }

  @Override
  public String toString() {
    return value;
  }
}
