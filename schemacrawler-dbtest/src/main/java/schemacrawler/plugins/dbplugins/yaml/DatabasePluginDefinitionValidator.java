/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.yaml;

import static us.fatehi.utility.Utility.isBlank;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import schemacrawler.plugins.dbplugins.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbplugins.model.LimitDefinition;
import schemacrawler.plugins.dbplugins.model.SchemaRetrievalDefinition;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;

final class DatabasePluginDefinitionValidator {

  private static final Map<String, SchemaInfoMetadataRetrievalStrategy>
      SCHEMA_RETRIEVAL_STRATEGY_LOOKUP = createSchemaRetrievalStrategyLookup();

  private static Map<String, SchemaInfoMetadataRetrievalStrategy>
      createSchemaRetrievalStrategyLookup() {
    final Map<String, SchemaInfoMetadataRetrievalStrategy> strategies = new LinkedHashMap<>();
    for (final SchemaInfoMetadataRetrievalStrategy strategy :
        SchemaInfoMetadataRetrievalStrategy.values()) {
      strategies.put(normalize(strategy.name()), strategy);
      strategies.put(normalize(strategy.getKey()), strategy);
    }
    return Map.copyOf(strategies);
  }

  private static MetadataRetrievalStrategy lookupMetadataRetrievalStrategy(
      final String value, final String sourceDescription) {
    final String normalizedValue = value.toLowerCase(Locale.ENGLISH).replace('-', '_');
    try {
      return MetadataRetrievalStrategy.valueOf(normalizedValue);
    } catch (final IllegalArgumentException e) {
      throw new ConfigurationException(
          "Unknown metadata retrieval strategy <%s> in <%s>".formatted(value, sourceDescription),
          e);
    }
  }

  private static SchemaInfoMetadataRetrievalStrategy lookupSchemaRetrievalStrategy(
      final String key, final String sourceDescription) {
    final SchemaInfoMetadataRetrievalStrategy strategy =
        SCHEMA_RETRIEVAL_STRATEGY_LOOKUP.get(normalize(key));
    if (strategy == null) {
      throw new ConfigurationException(
          "Unknown schema retrieval strategy <%s> in <%s>".formatted(key, sourceDescription));
    }
    return strategy;
  }

  private static String normalize(final String value) {
    if (value == null) {
      return "";
    }
    return value.replace("-", "").replace("_", "").toLowerCase(Locale.ENGLISH);
  }

  DatabaseConnectorDefinition toDatabaseConnectorDefinition(
      final DatabaseConnectorDefinition definition, final String sourceDescription) {
    if (definition == null) {
      throw new ConfigurationException("Missing YAML root in <%s>".formatted(sourceDescription));
    }
    if (definition.databaseServerType().toDatabaseServerType().isUnknownDatabaseSystem()) {
      throw new ConfigurationException("Missing database server type");
    }
    validateSchemaRetrieval(definition.schemaRetrieval(), sourceDescription);
    validateLimit(definition.limit(), sourceDescription);
    return definition;
  }

  private void validateLimit(final LimitDefinition limit, final String sourceDescription) {
    final LimitDefinition safeLimit = limit == null ? new LimitDefinition() : limit;
    if (!isBlank(safeLimit.includeSchemas()) && !isBlank(safeLimit.excludeSchemas())) {
      throw new ConfigurationException(
          "Conflicting schema limit values in <%s>".formatted(sourceDescription));
    }
    if (!isBlank(safeLimit.includeCatalogs()) && !isBlank(safeLimit.excludeCatalogs())) {
      throw new ConfigurationException(
          "Conflicting catalog limit values in <%s>".formatted(sourceDescription));
    }
  }

  private void validateSchemaRetrieval(
      final SchemaRetrievalDefinition schemaRetrieval, final String sourceDescription) {
    if (schemaRetrieval == null || schemaRetrieval.isEmpty()) {
      return;
    }
    for (final Map.Entry<SchemaInfoMetadataRetrievalStrategy, String> entry :
        schemaRetrieval.retrievalStrategies().entrySet()) {
      final String strategyValue = entry.getValue();
      if (isBlank(strategyValue)) {
        throw new ConfigurationException(
            "Missing or invalid schema retrieval strategy value for <%s> in <%s>"
                .formatted(entry.getKey(), sourceDescription));
      }
      lookupMetadataRetrievalStrategy(strategyValue, sourceDescription);
    }
  }
}
