/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.sqlite;

import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class SQLiteDatabaseConnector extends DatabaseConnector {

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseServerType dbServerType = new DatabaseServerType("sqlite", "SQLite");

    final DatabaseConnectionSourceBuilder connectionSourceBuilder =
        DatabaseConnectionSourceBuilder.builder("jdbc:sqlite:${database}")
            .withDefaultUrlx("application_id", "SchemaCrawler")
            .withDefaultUrlx("open_mode", "2");

    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    pluginCommand
        .addOption(
            "server", String.class, "--server=sqlite%n" + "Loads SchemaCrawler plug-in for SQLite")
        .addOption("host", String.class, "Should be omitted")
        .addOption("port", Integer.class, "Should be omitted")
        .addOption("database", String.class, "SQLite database file path");

    return DatabaseConnectorOptionsBuilder.builder(dbServerType)
        .withHelpCommand(pluginCommand)
        .withUrlStartsWith("jdbc:sqlite:")
        .withInformationSchemaViewsFromResourceFolder("/sqlite.information_schema")
        .withSchemaRetrievalOptionsBuilder(
            (schemaRetrievalOptionsBuilder, connection) ->
                schemaRetrievalOptionsBuilder.withIdentifierQuoteString("\""))
        .withDatabaseConnectionSourceBuilder(() -> connectionSourceBuilder)
        .build();
  }

  public SQLiteDatabaseConnector() {
    super(databaseConnectorOptions());
  }
}
