/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.TreeMap;
import org.jspecify.annotations.Nullable;
import schemacrawler.plugins.dbconnectors.yaml.JsonUtility;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents schema metadata retrieval overrides. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record SchemaRetrievalDefinition(
    @Nullable @JsonProperty(required = false)
        Map<SchemaInfoMetadataRetrievalStrategy, MetadataRetrievalStrategy> retrievalStrategies,
    @Nullable @JsonProperty(required = false) Boolean supportsCatalogs,
    @Nullable @JsonProperty(required = false) Boolean supportsSchemas) {

  public SchemaRetrievalDefinition() {
    this(null, null, null);
  }

  public SchemaRetrievalDefinition {
    retrievalStrategies =
        retrievalStrategies == null ? Map.of() : new TreeMap<>(retrievalStrategies);
  }

  @Override
  public String toString() {
    return JsonUtility.yamlMapper.writeValueAsString(this);
  }
}
