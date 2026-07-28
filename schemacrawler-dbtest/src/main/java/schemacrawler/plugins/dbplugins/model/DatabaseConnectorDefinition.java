/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import schemacrawler.plugins.dbplugins.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents a YAML database plugin definition. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DatabaseConnectorDefinition(
    @NonNull DatabaseServerTypeDefinition databaseServerType,
    @NonNull String urlTemplate,
    @NonNull String urlPrefix,
    Set<String> allowedDriverProperties,
    StandardOptionsDefinition standardOptions,
    List<AdditionalOptionDefinition> additionalOptions,
    SchemaRetrievalDefinition schemaRetrieval,
    LimitDefinition limit) {

  public DatabaseConnectorDefinition() {
    this(null, null, null, null, null, null, null, null);
  }

  public DatabaseConnectorDefinition {
    databaseServerType =
        databaseServerType == null ? new DatabaseServerTypeDefinition() : databaseServerType;
    urlTemplate = isBlank(urlTemplate) ? "" : urlTemplate;
    urlPrefix = isBlank(urlPrefix) ? "" : urlPrefix;
    allowedDriverProperties =
        allowedDriverProperties == null ? Set.of() : Set.copyOf(allowedDriverProperties);
    standardOptions = standardOptions == null ? new StandardOptionsDefinition() : standardOptions;
    additionalOptions = additionalOptions == null ? List.of() : List.copyOf(additionalOptions);
    schemaRetrieval = schemaRetrieval == null ? new SchemaRetrievalDefinition() : schemaRetrieval;
    limit = limit == null ? new LimitDefinition() : limit;
  }

  @Override
  public String toString() {
    return JsonUtility.mapper.writeValueAsString(this);
  }
}
