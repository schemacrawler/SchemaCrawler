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
import schemacrawler.plugins.dbplugins.model.LimitDefinition;
import schemacrawler.plugins.dbplugins.model.StandardOptionsDefinition;
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

  private final Map<String, SchemaInfoMetadataRetrievalStrategy> strategyLookup;

  public DatabaseConnectorDefinitionAdapter() {
    strategyLookup = createStrategyLookup();
  }

  private void addAdditionalOptions(
      final PluginCommand pluginCommand, final List<AdditionalOptionDefinition> additionalOptions) {
    for (final AdditionalOptionDefinition additionalOption : additionalOptions) {
      pluginCommand.addOption(
          additionalOption.name(),
          additionalOption.type().optionClass(),
          additionalOption.help().toArray(String[]::new));
    }
  }

  private void addStandardOptions(
      final PluginCommand pluginCommand,
      final StandardOptionsDefinition standardOptions,
      final DatabaseServerType dbServerType) {
    pluginCommand.addOption(
        "server",
        String.class,
        "--server=%s".formatted(dbServerType.getDatabaseSystemIdentifier()),
        "Loads SchemaCrawler plug-in for %s".formatted(dbServerType.getDatabaseSystemName()));
    pluginCommand.addOption(
        "host", String.class, helpLinesOrDefault(standardOptions.host().help(), "Host name"));
    pluginCommand.addOption(
        "port", Integer.class, helpLinesOrDefault(standardOptions.port().help(), "Port number"));
    pluginCommand.addOption(
        "database",
        String.class,
        helpLinesOrDefault(standardOptions.database().help(), "Database name"));
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
    final var schemaRetrieval = definition.schemaRetrieval();
    final Boolean supportsCatalogs = schemaRetrieval.supportsCatalogs();
    if (supportsCatalogs != null) {
      if (supportsCatalogs) {
        builder.withSupportsCatalogs();
      } else {
        builder.withDoesNotSupportCatalogs();
      }
    }
    final Boolean supportsSchemas = schemaRetrieval.supportsSchemas();
    if (supportsSchemas != null) {
      if (supportsSchemas) {
        builder.withSupportsSchemas();
      } else {
        builder.withDoesNotSupportSchemas();
      }
    }
    for (final Map.Entry<String, String> entry : schemaRetrieval.strategies().entrySet()) {
      final SchemaInfoMetadataRetrievalStrategy strategy = lookupStrategy(entry.getKey());
      final MetadataRetrievalStrategy metadataRetrievalStrategy =
          lookupMetadataRetrievalStrategy(entry.getValue());
      builder.with(strategy, metadataRetrievalStrategy);
    }
  }

  private void applyStandardOptionDefaults(
      final DatabaseConnectionSourceBuilder connectionSourceBuilder,
      final StandardOptionsDefinition standardOptions) {
    final String defaultHost = standardOptions.host().stringDefault();
    if (defaultHost != null) {
      connectionSourceBuilder.withDefaultHost(defaultHost);
    }
    final String defaultDatabase = standardOptions.database().stringDefault();
    if (defaultDatabase != null) {
      connectionSourceBuilder.withDefaultDatabase(defaultDatabase);
    }
    final String defaultPort = standardOptions.port().stringDefault();
    if (defaultPort != null) {
      try {
        connectionSourceBuilder.withDefaultPort(Integer.parseInt(defaultPort));
      } catch (final NumberFormatException e) {
        throw new ConfigurationException(
            "Invalid standard option default for <port> <%s>".formatted(defaultPort), e);
      }
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
    applyStandardOptionDefaults(connectionSourceBuilder, definition.standardOptions());
    if (!definition.allowedDriverProperties().isEmpty()) {
      connectionSourceBuilder.withAdditionalDriverProperties(definition.allowedDriverProperties());
    }
    if (!definition.additionalOptions().isEmpty()) {
      for (final AdditionalOptionDefinition additionalOption : definition.additionalOptions()) {
        final String stringDefault = additionalOption.stringDefault();
        if (stringDefault != null) {
          final String urlxKey = additionalOption.name();
          connectionSourceBuilder.withDefaultUrlx(
              urlxKey == null ? additionalOption.name() : urlxKey, stringDefault);
        }
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
    final DatabaseServerType dbServerType = definition.databaseServerType().toDatabaseServerType();

    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    addStandardOptions(pluginCommand, definition.standardOptions(), dbServerType);
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
