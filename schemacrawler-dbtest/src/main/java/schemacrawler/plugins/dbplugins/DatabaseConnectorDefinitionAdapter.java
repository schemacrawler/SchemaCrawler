/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.plugins.dbplugins.model.AdditionalOptionDefinition;
import schemacrawler.plugins.dbplugins.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbplugins.model.HelpDefinition;
import schemacrawler.plugins.dbplugins.model.LimitDefinition;
import schemacrawler.plugins.dbplugins.model.UrlPropertyDefinition;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

/** Adapts {@link DatabaseConnectorDefinition} into {@link DatabaseConnectorOptions}. */
public final class DatabaseConnectorDefinitionAdapter {

  public static DatabaseConnectorOptions toDatabaseConnectorOptions(
      final DatabaseConnectorDefinition definition) {
    return new DatabaseConnectorDefinitionAdapter().toDatabaseConnectorOptionsInternal(definition);
  }

  private final Map<String, Class<?>> optionTypes;

  private final Map<String, SchemaInfoMetadataRetrievalStrategy> strategyLookup;

  public DatabaseConnectorDefinitionAdapter() {
    optionTypes =
        Map.of("string", String.class, "integer", Integer.class, "boolean", Boolean.class);
    strategyLookup = createStrategyLookup();
  }

  private void addAdditionalOptions(
      final PluginCommand pluginCommand, final List<AdditionalOptionDefinition> additionalOptions) {
    for (final AdditionalOptionDefinition additionalOption : additionalOptions) {
      pluginCommand.addOption(
          additionalOption.name(),
          lookupOptionType(additionalOption.type()),
          additionalOption.help().toArray(String[]::new));
    }
  }

  private void addStandardOptions(
      final PluginCommand pluginCommand,
      final HelpDefinition helpDefinition,
      final DatabaseServerType dbServerType) {
    pluginCommand.addOption(
        "server",
        String.class,
        helpLinesOrDefault(
            helpDefinition.server(),
            "--server=%s".formatted(dbServerType.getDatabaseSystemIdentifier()),
            "Loads SchemaCrawler plug-in for %s".formatted(dbServerType.getDatabaseSystemName())));
    pluginCommand.addOption(
        "host", String.class, helpLinesOrDefault(helpDefinition.host(), "Host name"));
    pluginCommand.addOption(
        "port", Integer.class, helpLinesOrDefault(helpDefinition.port(), "Port number"));
    pluginCommand.addOption(
        "database", String.class, helpLinesOrDefault(helpDefinition.database(), "Database name"));
  }

  private void applyLimitOptions(
      final schemacrawler.schemacrawler.LimitOptionsBuilder builder, final LimitDefinition limit) {
    if (limit == null) {
      return;
    }
    if (limit.includeSchemas() != null) {
      builder.includeSchemas(new RegularExpressionInclusionRule(limit.includeSchemas()));
    } else if (limit.excludeSchemas() != null) {
      builder.includeSchemas(new RegularExpressionExclusionRule(limit.excludeSchemas()));
    }
  }

  private void applySchemaRetrievalOverrides(
      final SchemaRetrievalOptionsBuilder builder, final DatabaseConnectorDefinition definition) {
    final var schemaSupport = definition.schemaSupport();
    if (schemaSupport.supportsCatalogs() != null) {
      if (schemaSupport.supportsCatalogs()) {
        builder.withSupportsCatalogs();
      } else {
        builder.withDoesNotSupportCatalogs();
      }
    }
    if (schemaSupport.supportsSchemas() != null) {
      if (schemaSupport.supportsSchemas()) {
        builder.withSupportsSchemas();
      } else {
        builder.withDoesNotSupportSchemas();
      }
    }
    final String identifierQuoteString = definition.identifierQuoteString();
    if (identifierQuoteString != null) {
      builder.withIdentifierQuoteString(identifierQuoteString);
    }
    for (final Map.Entry<String, String> entry :
        definition.schemaRetrieval().strategies().entrySet()) {
      final SchemaInfoMetadataRetrievalStrategy strategy = lookupStrategy(entry.getKey());
      final MetadataRetrievalStrategy metadataRetrievalStrategy =
          lookupMetadataRetrievalStrategy(entry.getValue());
      builder.with(strategy, metadataRetrievalStrategy);
    }
  }

  private Map<String, SchemaInfoMetadataRetrievalStrategy> createStrategyLookup() {
    final Map<String, SchemaInfoMetadataRetrievalStrategy> strategies = new LinkedHashMap<>();
    for (final SchemaInfoMetadataRetrievalStrategy strategy :
        SchemaInfoMetadataRetrievalStrategy.values()) {
      strategies.put(normalize(strategy.name()), strategy);
      strategies.put(normalize(strategy.getKey()), strategy);
    }
    return Map.copyOf(strategies);
  }

  private DatabaseConnectionSourceBuilder databaseConnectionSourceBuilder(
      final DatabaseConnectorDefinition definition) {
    final DatabaseConnectionSourceBuilder connectionSourceBuilder =
        DatabaseConnectionSourceBuilder.builder(definition.urlTemplate());
    if (definition.defaultPort() != null) {
      connectionSourceBuilder.withDefaultPort(definition.defaultPort());
    }
    if (!definition.allowedDriverProperties().isEmpty()) {
      connectionSourceBuilder.withAdditionalDriverProperties(definition.allowedDriverProperties());
    }
    if (!definition.defaultUrlProperties().isEmpty()) {
      for (final UrlPropertyDefinition defaultUrlProperty : definition.defaultUrlProperties()) {
        connectionSourceBuilder.withDefaultUrlx(
            defaultUrlProperty.name(), defaultUrlProperty.value());
      }
    }
    return connectionSourceBuilder;
  }

  private String[] helpLinesOrDefault(final List<String> helpLines, final String... defaults) {
    if (helpLines == null || helpLines.isEmpty()) {
      return defaults;
    }
    return helpLines.toArray(String[]::new);
  }

  private MetadataRetrievalStrategy lookupMetadataRetrievalStrategy(final String value) {
    try {
      return MetadataRetrievalStrategy.valueOf(value.toLowerCase(Locale.ENGLISH).replace('-', '_'));
    } catch (final IllegalArgumentException e) {
      throw new ConfigurationException(
          "Unknown metadata retrieval strategy <%s>".formatted(value), e);
    }
  }

  private Class<?> lookupOptionType(final String type) {
    final Class<?> optionType = optionTypes.get(normalize(type));
    if (optionType == null) {
      throw new ConfigurationException("Unknown additional option type <%s>".formatted(type));
    }
    return optionType;
  }

  private SchemaInfoMetadataRetrievalStrategy lookupStrategy(final String name) {
    final SchemaInfoMetadataRetrievalStrategy strategy = strategyLookup.get(normalize(name));
    if (strategy == null) {
      throw new ConfigurationException("Unknown schema retrieval strategy <%s>".formatted(name));
    }
    return strategy;
  }

  private String normalize(final String value) {
    return value == null ? "" : value.replace("-", "").replace("_", "").toLowerCase(Locale.ENGLISH);
  }

  private DatabaseConnectorOptions toDatabaseConnectorOptionsInternal(
      final DatabaseConnectorDefinition definition) {
    final DatabaseServerType dbServerType =
        new DatabaseServerType(definition.server(), definition.name());

    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    addStandardOptions(pluginCommand, definition.help(), dbServerType);
    addAdditionalOptions(pluginCommand, definition.additionalOptions());

    return DatabaseConnectorOptionsBuilder.builder(dbServerType)
        .withHelpCommand(pluginCommand)
        .withUrlStartsWith(definition.urlPrefix())
        .withDatabaseConnectionSourceBuilder(() -> databaseConnectionSourceBuilder(definition))
        .withSchemaRetrievalOptionsBuilder(
            (builder, connection) -> applySchemaRetrievalOverrides(builder, definition))
        .withLimitOptionsBuilder(builder -> applyLimitOptions(builder, definition.limit()))
        .build();
  }
}
