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
  unverified,
  machine_confirmed,
  human_reviewed;

  public static OkfTrustTier fromString(final String value) {
    final String normalized = requireNonNull(value, "No trust tier provided").trim();
    for (final OkfTrustTier verifiedBy : values()) {
      if (verifiedBy.getValue().equalsIgnoreCase(normalized)
          || verifiedBy.name().equalsIgnoreCase(normalized.replace('-', '_'))) {
        return verifiedBy;
      }
    }
    throw new IllegalArgumentException("Unknown trust tier: " + value);
  }

  public String getValue() {
    return name().replace('_', '-');
  }

  @Override
  public String toString() {
    return getValue();
  }
}
