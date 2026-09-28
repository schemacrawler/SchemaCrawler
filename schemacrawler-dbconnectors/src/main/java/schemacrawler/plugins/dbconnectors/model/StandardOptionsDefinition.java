/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors.model;

import static java.util.Objects.requireNonNullElseGet;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import schemacrawler.plugins.dbconnectors.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents standard plugin options metadata. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record StandardOptionsDefinition(
    @Nullable @JsonProperty(required = false) StandardOptionDefinition host,
    @Nullable @JsonProperty(required = false) StandardOptionDefinition port,
    @Nullable @JsonProperty(required = false) StandardOptionDefinition database) {

  public StandardOptionsDefinition() {
    this(null, null, null);
  }

  public StandardOptionsDefinition {
    host = requireNonNullElseGet(host, StandardOptionDefinition::new);
    port = requireNonNullElseGet(port, StandardOptionDefinition::new);
    database = requireNonNullElseGet(database, StandardOptionDefinition::new);
  }

  @Override
  public String toString() {
    return JsonUtility.yamlMapper.writeValueAsString(this);
  }
}
