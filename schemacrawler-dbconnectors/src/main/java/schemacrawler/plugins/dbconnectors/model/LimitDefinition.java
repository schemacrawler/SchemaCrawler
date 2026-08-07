/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors.model;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import schemacrawler.plugins.dbconnectors.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents catalog and schema limit patterns. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record LimitDefinition(
    @Nullable @JsonProperty(required = false) String includeSchemas,
    @Nullable @JsonProperty(required = false) String excludeSchemas) {

  public LimitDefinition() {
    this(null, null);
  }

  public LimitDefinition {
    // Null means use defaults for include and exclude

    if (includeSchemas != null) {
      requireNonNull(includeSchemas, "No include schema pattern provided");
    }
    if (excludeSchemas != null) {
      requireNonNull(excludeSchemas, "No exclude schema pattern provided");
    }
  }

  public boolean hasValues() {
    return includeSchemas != null || excludeSchemas != null;
  }

  @Override
  public String toString() {
    return JsonUtility.yamlMapper.writeValueAsString(this);
  }
}
