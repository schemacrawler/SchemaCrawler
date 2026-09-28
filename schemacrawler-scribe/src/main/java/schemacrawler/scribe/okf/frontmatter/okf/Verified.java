/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.okf;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Verified(@JsonIgnore TrustTier trustTier, @JsonIgnore Actor actor, Instant at) {
  public Verified {
    trustTier = requireNonNullElse(trustTier, TrustTier.unverified);
    requireNonNull(actor, "No actor provided");
    at = requireNonNullElseGet(at, Instant::now);
  }

  public Verified(final TrustTier trustTier, final Actor actor) {
    this(trustTier, actor, null);
  }

  @JsonProperty("by")
  public String by() {
    return "%s:%s".formatted(trustTier, actor.actor());
  }
}
