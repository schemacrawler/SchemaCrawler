/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.postgresql;

import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.metadata_over_schemas;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class PostgreSQLDatabaseConnector extends DatabaseConnector {

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseServerType dbServerType = new DatabaseServerType("postgresql", "PostgreSQL");

    final DatabaseConnectionSourceBuilder connectionSourceBuilder =
        DatabaseConnectionSourceBuilder.builder("jdbc:postgresql://${host}:${port}/${database}")
            .withDefaultPort(5432)
            .withDefaultUrlx("ApplicationName", "SchemaCrawler")
            .withDefaultUrlx("loggerLevel", "DEBUG");

    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    pluginCommand
        .addOption(
            "server",
            String.class,
            "--server=postgresql%n" + "Loads SchemaCrawler plug-in for PostgreSQL")
        .addOption(
            "host",
            String.class,
            "Host name%n"
                + "Optional, uses the PGHOSTADDR and PGHOST environmental variables "
                + "if available, or defaults to localhost")
        .addOption(
            "port",
            Integer.class,
            "Port number%n"
                + "Optional, uses the PGPORT environmental variable "
                + "if available, or defaults to 5432")
        .addOption(
            "database",
            String.class,
            "Database name%n"
                + "Optional, uses the PGDATABASE environmental variable if available");

    return DatabaseConnectorOptionsBuilder.builder(dbServerType)
        .withHelpCommand(pluginCommand)
        .withUrlStartsWith("jdbc:postgresql:")
        .withInformationSchemaViewsFromResourceFolder("/postgresql.information_schema")
        .withSchemaRetrievalOptionsBuilder(
            (schemaRetrievalOptionsBuilder, connection) ->
                schemaRetrievalOptionsBuilder
                    .with(tableColumnsRetrievalStrategy, metadata_over_schemas)
                    .withEnumDataTypeHelper(new PostgreSQLEnumDataTypeHelper()))
        .withLimitOptionsBuilder(
            limitOptionsBuilder ->
                limitOptionsBuilder.includeSchemas(
                    new RegularExpressionExclusionRule("pg_catalog|information_schema")))
        .withDatabaseConnectionSourceBuilder(() -> connectionSourceBuilder)
        .build();
  }

  public PostgreSQLDatabaseConnector() {
    super(databaseConnectorOptions());
  }
}
