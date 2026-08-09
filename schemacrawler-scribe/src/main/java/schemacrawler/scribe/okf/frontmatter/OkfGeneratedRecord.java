/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OkfGeneratedRecord(@JsonIgnore OkfActor actor, Instant at) {

  public OkfGeneratedRecord {
    actor = requireNonNull(actor, "No generated.by actor provided");
    at = at == null ? Instant.now() : at;
  }

  @JsonProperty("by")
  public String by() {
    return actor.toString();
  }
}
