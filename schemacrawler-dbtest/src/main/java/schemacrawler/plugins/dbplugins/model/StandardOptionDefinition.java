/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import schemacrawler.plugins.dbplugins.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents help and default values for a standard option. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record StandardOptionDefinition(
    @JsonProperty(value = "default") Object defaultValue, List<String> help) {

  public StandardOptionDefinition() {
    this(null, null);
  }

  public StandardOptionDefinition {
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
