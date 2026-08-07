/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors.model;

import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import schemacrawler.plugins.dbconnectors.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents a YAML database plugin definition. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DatabaseConnectorDefinition(
    @NonNull @JsonProperty(required = true) DatabaseServerTypeDefinition databaseServerType,
    @NonNull @JsonProperty(required = true) String urlTemplate,
    @Nullable @JsonProperty(required = false) List<String> allowedDriverProperties,
    @Nullable @JsonProperty(required = false) StandardOptionsDefinition standardOptions,
    @Nullable @JsonProperty(required = false) List<AdditionalOptionDefinition> additionalOptions,
    @Nullable @JsonProperty(required = false) SchemaRetrievalDefinition schemaRetrieval,
    @Nullable @JsonProperty(required = false) LimitDefinition limit) {

  public DatabaseConnectorDefinition() {
    this(null, null, null, null, null, null, null);
  }

  public DatabaseConnectorDefinition {
    databaseServerType =
        databaseServerType == null ? new DatabaseServerTypeDefinition() : databaseServerType;
    urlTemplate = isBlank(urlTemplate) ? "" : urlTemplate;
    allowedDriverProperties =
        allowedDriverProperties == null ? List.of() : List.copyOf(allowedDriverProperties);
    standardOptions = standardOptions == null ? new StandardOptionsDefinition() : standardOptions;
    additionalOptions = additionalOptions == null ? List.of() : List.copyOf(additionalOptions);
    schemaRetrieval = schemaRetrieval == null ? new SchemaRetrievalDefinition() : schemaRetrieval;
    limit = limit == null ? new LimitDefinition() : limit;

    // Validate allowedDriverProperties
    if (!allowedDriverProperties.equals(Set.copyOf(allowedDriverProperties))) {
      new IllegalArgumentException("Allowed driver properties list should have duplicate values");
    }
  }

  @Override
  public String toString() {
    return JsonUtility.mapper.writeValueAsString(this);
  }
}
