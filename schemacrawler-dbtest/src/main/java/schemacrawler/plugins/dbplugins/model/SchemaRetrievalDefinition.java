/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import schemacrawler.plugins.dbplugins.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents schema metadata retrieval overrides. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record SchemaRetrievalDefinition(
    @Nullable @JsonProperty(required = false) Map<String, String> strategies,
    @Nullable @JsonProperty(required = false) Boolean supportsCatalogs,
    @Nullable @JsonProperty(required = false) Boolean supportsSchemas) {

  public SchemaRetrievalDefinition() {
    this(null, null, null);
  }

  public SchemaRetrievalDefinition {
    strategies = strategies == null ? Map.of() : Map.copyOf(strategies);
  }

  public boolean isEmpty() {
    return strategies.isEmpty() && supportsCatalogs == null && supportsSchemas == null;
  }

  @Override
  public String toString() {
    return JsonUtility.mapper.writeValueAsString(this);
  }
}
