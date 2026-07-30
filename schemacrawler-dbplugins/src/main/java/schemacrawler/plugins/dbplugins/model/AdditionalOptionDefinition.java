/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.jspecify.annotations.NonNull;
import schemacrawler.plugins.dbplugins.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents an additional named JDBC driver option. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record AdditionalOptionDefinition(
    @NonNull @JsonProperty(required = true) String name,
    @NonNull @JsonProperty(required = true) CommandlineOptionType type,
    @NonNull @JsonProperty(value = "default", required = false) Object defaultValue,
    @NonNull @JsonProperty(required = true) List<String> help) {

  public AdditionalOptionDefinition {
    name = requireNotBlank(name, "No additional option name provided");
    type = requireNonNull(type, "No additional option type provided");
    help = help == null ? List.of() : List.copyOf(help);
  }

  public String stringDefault() {
    if (defaultValue == null) {
      return null;
    }
    return String.valueOf(defaultValue);
  }

  @Override
  public String toString() {
    return JsonUtility.mapper.writeValueAsString(this);
  }
}
