/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.server.postgresql;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;

public final class PostgreSQLDatabaseConnector extends DatabaseConnector {

  public PostgreSQLDatabaseConnector() {
    super(
        new DatabaseServerType("postgresql", "PostgreSQL"),
        url -> url != null && url.startsWith("jdbc:postgresql:"),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/postgresql.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) ->
            schemaRetrievalOptionsBuilder.withEnumDataTypeHelper(
                new PostgreSQLEnumDataTypeHelper()),
        limitOptionsBuilder ->
            limitOptionsBuilder.includeSchemas(
                new RegularExpressionExclusionRule("pg_catalog|information_schema")),
        () ->
            DatabaseConnectionSourceBuilder.builder("jdbc:postgresql://${host}:${port}/${database}")
                .withDefaultPort(5432)
                .withDefaultUrlx("ApplicationName", "SchemaCrawler")
                .withDefaultUrlx("loggerLevel", "DEBUG"));
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
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
    return pluginCommand;
  }
}
