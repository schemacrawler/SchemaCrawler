/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import schemacrawler.plugins.dbplugins.yaml.JsonUtility;
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
    host = host == null ? new StandardOptionDefinition() : host;
    port = port == null ? new StandardOptionDefinition() : port;
    database = database == null ? new StandardOptionDefinition() : database;
  }

  @Override
  public String toString() {
    return JsonUtility.mapper.writeValueAsString(this);
  }
}
