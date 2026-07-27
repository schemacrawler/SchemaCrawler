/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.yaml;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.plugins.dbplugins.model.AdditionalOptionDefinition;
import schemacrawler.plugins.dbplugins.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbplugins.model.DatabaseServerTypeDefinition;
import schemacrawler.plugins.dbplugins.model.LimitDefinition;
import schemacrawler.plugins.dbplugins.model.SchemaRetrievalDefinition;
import schemacrawler.plugins.dbplugins.model.StandardOptionDefinition;
import schemacrawler.plugins.dbplugins.model.StandardOptionsDefinition;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.string.StringFormat;

/** Parses YAML plugin definitions. */
public final class DatabasePluginYamlDeserializer {

  private static final Logger LOGGER =
      Logger.getLogger(DatabasePluginYamlDeserializer.class.getName());

  private static final Set<String> ROOT_FIELDS =
      Set.of(
          "database-connector",
          "standard-options",
          "additional-options",
          "schema-retrieval",
          "limit");
  private static final Set<String> CONNECTOR_FIELDS =
      Set.of("database-server-type", "url-template", "url-prefix", "allowed-driver-properties");

  private static final Set<String> DATABASE_SERVER_TYPE_FIELDS = Set.of("server", "name");
  private static final Set<String> STANDARD_OPTIONS_FIELDS =
      Set.of("server", "host", "port", "database");
  private static final Set<String> STANDARD_OPTION_FIELDS = Set.of("help", "default");
  private static final Set<String> LIMIT_FIELDS =
      Set.of("include-schemas", "exclude-schemas", "include-catalogs", "exclude-catalogs");
  private static final Set<String> SCHEMA_RETRIEVAL_FIELDS =
      Set.of("supports-catalogs", "supports-schemas");
  private static final Set<String> ADDITIONAL_OPTION_FIELDS =
      Set.of("name", "type", "urlx-key", "default", "help");

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

  private static void ensureAllowedFields(
      final JsonNode node, final Set<String> allowedFields, final String sourceDescription) {
    for (final String fieldName : node.propertyNames()) {
      if (!allowedFields.contains(fieldName)) {
        throw new ConfigurationException(
            "Unknown YAML field <%s> in <%s>".formatted(fieldName, sourceDescription));
      }
    }
  }

  private static MetadataRetrievalStrategy lookupMetadataRetrievalStrategy(
      final String value, final String sourceDescription) {
    final String normalizedValue =
        requireNonNull(value, "No schema retrieval strategy value provided")
            .toLowerCase()
            .replace('-', '_');
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
    return value.replace("-", "").replace("_", "").toLowerCase();
  }

  private static String optionalText(final JsonNode node, final String fieldName) {
    final JsonNode field = node.get(fieldName);
    if (field == null || field.isNull()) {
      return null;
    }
    final String value = field.asString();
    return isBlank(value) ? null : value;
  }

  private static AdditionalOptionDefinition readAdditionalOption(
      final JsonNode node, final String sourceDescription) {
    ensureAllowedFields(node, ADDITIONAL_OPTION_FIELDS, sourceDescription);
    final String name = requiredTextField(node, "name", sourceDescription);
    final String type = requiredTextField(node, "type", sourceDescription);
    if (!node.has("default")) {
      throw new ConfigurationException(
          "Missing required <default> field in additional-option in <%s>"
              .formatted(sourceDescription));
    }
    final String defaultValue = valueToString(node.get("default"));
    final List<String> help = readStringList(node, "help");
    return new AdditionalOptionDefinition(name, type, defaultValue, help);
  }

  private static Set<String> readAllowedDriverProperties(final JsonNode node) {
    final JsonNode field = node.get("allowed-driver-properties");
    if (field == null || field.isNull()) {
      return Set.of();
    }
    if (!field.isArray()) {
      throw new ConfigurationException("Invalid list value for <allowed-driver-properties>");
    }
    final List<String> values = new ArrayList<>();
    field.forEach(entry -> values.add(entry.asString()));
    return Set.copyOf(values);
  }

  private static DatabaseServerTypeDefinition readDatabaseServerTypeDefinition(
      final JsonNode node, final String sourceDescription) {
    final JsonNode field = requiredObjectField(node, "database-server-type", sourceDescription);
    ensureAllowedFields(field, DATABASE_SERVER_TYPE_FIELDS, sourceDescription);
    return new DatabaseServerTypeDefinition(
        requiredTextField(field, "server", sourceDescription),
        requiredTextField(field, "name", sourceDescription));
  }

  private static LimitDefinition readLimitDefinition(
      final JsonNode node, final String sourceDescription) {
    final JsonNode field = node.get("limit");
    if (field == null || field.isNull()) {
      return new LimitDefinition(null, null, null, null);
    }
    if (!field.isObject()) {
      throw new ConfigurationException("Invalid object value for <limit>");
    }
    ensureAllowedFields(field, LIMIT_FIELDS, sourceDescription);
    final String includeSchemas = optionalText(field, "include-schemas");
    final String excludeSchemas = optionalText(field, "exclude-schemas");
    final String includeCatalogs = optionalText(field, "include-catalogs");
    final String excludeCatalogs = optionalText(field, "exclude-catalogs");
    if (!isBlank(includeSchemas) && !isBlank(excludeSchemas)) {
      throw new ConfigurationException(
          "Conflicting schema limit values in <%s>".formatted(sourceDescription));
    }
    if (!isBlank(includeCatalogs) && !isBlank(excludeCatalogs)) {
      throw new ConfigurationException(
          "Conflicting catalog limit values in <%s>".formatted(sourceDescription));
    }
    return new LimitDefinition(includeSchemas, excludeSchemas, includeCatalogs, excludeCatalogs);
  }

  private static SchemaRetrievalDefinition readSchemaRetrievalDefinition(
      final JsonNode node, final String sourceDescription) {
    final JsonNode field = node.get("schema-retrieval");
    if (field == null || field.isNull()) {
      return new SchemaRetrievalDefinition(null, null, null);
    }
    if (!field.isObject()) {
      throw new ConfigurationException("Invalid object value for <schema-retrieval>");
    }
    final Map<String, String> strategies = new LinkedHashMap<>();
    for (final String propertyName : field.propertyNames()) {
      if (SCHEMA_RETRIEVAL_FIELDS.contains(propertyName)) {
        continue;
      }
      final String strategyValue = requiredTextField(field, propertyName, sourceDescription);
      lookupSchemaRetrievalStrategy(propertyName, sourceDescription);
      lookupMetadataRetrievalStrategy(strategyValue, sourceDescription);
      strategies.put(propertyName, strategyValue);
    }
    return new SchemaRetrievalDefinition(
        strategies,
        readTriState(field, "supports-catalogs", sourceDescription),
        readTriState(field, "supports-schemas", sourceDescription));
  }

  private static List<String> readStringList(final JsonNode node, final String fieldName) {
    final JsonNode field = node.get(fieldName);
    if (field == null || field.isNull()) {
      return List.of();
    }
    if (!field.isArray()) {
      throw new ConfigurationException("Invalid list value for <%s>".formatted(fieldName));
    }
    final List<String> values = new ArrayList<>();
    field.forEach(entry -> values.add(entry.asString()));
    return List.copyOf(values);
  }

  private static StandardOptionDefinition readStandardOptionDefinition(
      final JsonNode node, final String fieldName, final String sourceDescription) {
    final JsonNode field = node.get(fieldName);
    if (field == null || field.isNull()) {
      return new StandardOptionDefinition(null, null);
    }
    if (!field.isObject()) {
      throw new ConfigurationException("Invalid object value for <%s>".formatted(fieldName));
    }
    ensureAllowedFields(field, STANDARD_OPTION_FIELDS, sourceDescription);
    return new StandardOptionDefinition(
        valueToString(field.get("default")), readStringList(field, "help"));
  }

  private static StandardOptionsDefinition readStandardOptionsDefinition(
      final JsonNode node, final String sourceDescription) {
    final JsonNode field = node.get("standard-options");
    if (field == null || field.isNull()) {
      return new StandardOptionsDefinition();
    }
    if (!field.isObject()) {
      throw new ConfigurationException("Invalid object value for <standard-options>");
    }
    ensureAllowedFields(field, STANDARD_OPTIONS_FIELDS, sourceDescription);
    return new StandardOptionsDefinition(
        readStandardOptionDefinition(field, "server", sourceDescription),
        readStandardOptionDefinition(field, "host", sourceDescription),
        readStandardOptionDefinition(field, "port", sourceDescription),
        readStandardOptionDefinition(field, "database", sourceDescription));
  }

  private static Boolean readTriState(
      final JsonNode node, final String fieldName, final String sourceDescription) {
    final JsonNode field = node.get(fieldName);
    if (field == null || field.isNull()) {
      return null;
    }
    if (field.isBoolean()) {
      return field.asBoolean();
    }
    final String value = field.asString();
    if ("auto".equalsIgnoreCase(value)) {
      return null;
    }
    if ("true".equalsIgnoreCase(value)) {
      return Boolean.TRUE;
    }
    if ("false".equalsIgnoreCase(value)) {
      return Boolean.FALSE;
    }
    throw new ConfigurationException(
        "Invalid tri-state value for <%s> in <%s>".formatted(fieldName, sourceDescription));
  }

  private static JsonNode requiredObjectField(
      final JsonNode node, final String fieldName, final String sourceDescription) {
    final JsonNode field = node.get(fieldName);
    if (field == null || field.isNull() || !field.isObject()) {
      throw new ConfigurationException(
          "Missing or invalid <%s> section in <%s>".formatted(fieldName, sourceDescription));
    }
    return field;
  }

  private static String requiredTextField(
      final JsonNode node, final String fieldName, final String sourceDescription) {
    final JsonNode field = node.get(fieldName);
    if (field == null || field.isNull() || !field.isValueNode()) {
      throw new ConfigurationException(
          "Missing or invalid <%s> field in <%s>".formatted(fieldName, sourceDescription));
    }
    final String value = field.asString();
    if (isBlank(value)) {
      throw new ConfigurationException(
          "Missing or invalid <%s> field in <%s>".formatted(fieldName, sourceDescription));
    }
    return value;
  }

  private static String valueToString(final JsonNode node) {
    if (node == null || node.isNull()) {
      return null;
    }
    if (node.isValueNode()) {
      return node.asString();
    }
    return node.toString();
  }

  public DatabaseConnectorDefinition parse(final InputResource inputResource) {
    requireNonNull(inputResource, "No input resource provided");
    LOGGER.log(Level.FINE, new StringFormat("Parsing <%s>", inputResource));
    try (final Reader reader = inputResource.openNewInputReader(UTF_8)) {
      final JsonNode root = JsonUtility.mapper.readTree(reader);
      return parseRoot(inputResource.toString(), root);
    } catch (final Exception e) {
      LOGGER.log(
          Level.WARNING,
          "Could not read database connector definition from <%s>".formatted(inputResource),
          e);
      return new DatabaseConnectorDefinition();
    }
  }

  private DatabaseConnectorDefinition parseRoot(
      final String sourceDescription, final JsonNode root) {
    if (root == null || !root.isObject()) {
      throw new ConfigurationException("Invalid YAML root in <%s>".formatted(sourceDescription));
    }
    ensureAllowedFields(root, ROOT_FIELDS, sourceDescription);
    final JsonNode databaseConnector =
        requiredObjectField(root, "database-connector", sourceDescription);
    ensureAllowedFields(databaseConnector, CONNECTOR_FIELDS, sourceDescription);

    final DatabaseServerTypeDefinition databaseServerType =
        readDatabaseServerTypeDefinition(databaseConnector, sourceDescription);
    final String urlTemplate =
        requiredTextField(databaseConnector, "url-template", sourceDescription);
    final String urlPrefix = requiredTextField(databaseConnector, "url-prefix", sourceDescription);
    final StandardOptionsDefinition standardOptions =
        readStandardOptionsDefinition(root, sourceDescription);
    final Set<String> allowedDriverProperties = readAllowedDriverProperties(databaseConnector);
    final List<AdditionalOptionDefinition> additionalOptions =
        readAdditionalOptions(root, sourceDescription);
    final SchemaRetrievalDefinition schemaRetrieval =
        readSchemaRetrievalDefinition(root, sourceDescription);
    final LimitDefinition limit = readLimitDefinition(root, sourceDescription);

    return new DatabaseConnectorDefinition(
        databaseServerType,
        urlTemplate,
        urlPrefix,
        allowedDriverProperties,
        standardOptions,
        additionalOptions,
        schemaRetrieval,
        limit);
  }

  private List<AdditionalOptionDefinition> readAdditionalOptions(
      final JsonNode node, final String sourceDescription) {
    final JsonNode field = node.get("additional-options");
    if (field == null || field.isNull()) {
      return List.of();
    }
    if (!field.isArray()) {
      throw new ConfigurationException("Invalid list value for <additional-options>");
    }
    final List<AdditionalOptionDefinition> additionalOptions = new ArrayList<>();
    field.forEach(
        option -> {
          if (!option.isObject()) {
            throw new ConfigurationException(
                "Invalid additional option entry in <%s>".formatted(sourceDescription));
          }
          ensureAllowedFields(option, ADDITIONAL_OPTION_FIELDS, sourceDescription);
          additionalOptions.add(readAdditionalOption(option, sourceDescription));
        });
    return List.copyOf(additionalOptions);
  }
}
