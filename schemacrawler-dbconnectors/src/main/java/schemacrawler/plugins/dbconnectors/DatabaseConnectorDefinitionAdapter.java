/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.plugins.dbconnectors.model.AdditionalOptionDefinition;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.model.LimitDefinition;
import schemacrawler.plugins.dbconnectors.model.StandardOptionsDefinition;
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

  private final DatabaseConnectorDefinition definition;

  public DatabaseConnectorDefinitionAdapter(final DatabaseConnectorDefinition definition) {
    this.definition = requireNonNull(definition, "No database connector definition provided");
  }

  /**
   * Creates a pre-populated connection source builder from a YAML connector definition.
   *
   * <p>The returned builder includes URL template defaults, standard option defaults, allowed
   * driver properties, and all additional option urlx defaults.
   */
  public DatabaseConnectionSourceBuilder toConnectionSourceBuilder() {

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

  public DatabaseConnectorOptions toDatabaseConnectorOptions() {
    return toDatabaseConnectorOptionsBuilder().build();
  }

  /**
   * Creates a pre-populated options builder from a YAML connector definition.
   *
   * <p>The returned builder includes all declarative settings, including URL matching, CLI help,
   * schema retrieval overrides, limit options, connection source defaults, and information schema
   * SQL loading by the folder naming convention.
   */
  public DatabaseConnectorOptionsBuilder toDatabaseConnectorOptionsBuilder() {
    final DatabaseServerType dbServerType = definition.databaseServerType().toDatabaseServerType();
    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    addStandardOptions(pluginCommand, definition.standardOptions(), dbServerType);
    addAdditionalOptions(pluginCommand, definition.additionalOptions());

    return DatabaseConnectorOptionsBuilder.builder(dbServerType)
        .withHelpCommand(pluginCommand)
        .withDatabaseConnectionSourceBuilder(this::toConnectionSourceBuilder)
        .withSchemaRetrievalOptionsBuilder(
            (builder, connection) -> applySchemaRetrievalOverrides(builder, definition))
        .withLimitOptionsBuilder(builder -> applyLimitOptions(builder, definition.limit()))
        .withInformationSchemaViewsFromResourceFolder(
            "/%s.information_schema".formatted(definition.databaseServerType().server()));
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
    if (limit == null || !limit.hasValues()) {
      return;
    }
    builder.includeSchemas(
        new RegularExpressionRule(limit.includeSchemas(), limit.excludeSchemas()));
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
    for (final Map.Entry<SchemaInfoMetadataRetrievalStrategy, MetadataRetrievalStrategy> entry :
        schemaRetrieval.retrievalStrategies().entrySet()) {
      builder.with(entry.getKey(), entry.getValue());
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

  private String[] helpLinesOrDefault(final List<String> helpLines, final String... defaults) {
    if (helpLines == null || helpLines.isEmpty()) {
      return defaults;
    }
    return helpLines.toArray(String[]::new);
  }
}
