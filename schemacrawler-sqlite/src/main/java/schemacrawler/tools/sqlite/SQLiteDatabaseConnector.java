/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.sqlite;

import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class SQLiteDatabaseConnector extends DatabaseConnector {

  public SQLiteDatabaseConnector() {
    super(
        new DatabaseServerType("sqlite", "SQLite"),
        url -> url != null && url.startsWith("jdbc:sqlite:"),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/sqlite.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) ->
            schemaRetrievalOptionsBuilder.withIdentifierQuoteString("\""),
        limitOptionsBuilder -> {},
        () ->
            DatabaseConnectionSourceBuilder.builder("jdbc:sqlite:${database}")
                .withDefaultUrlx("application_id", "SchemaCrawler")
                .withDefaultUrlx("open_mode", "2"));
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
        .addOption(
            "server", String.class, "--server=sqlite%n" + "Loads SchemaCrawler plug-in for SQLite")
        .addOption("host", String.class, "Should be omitted")
        .addOption("port", Integer.class, "Should be omitted")
        .addOption("database", String.class, "SQLite database file path");
    return pluginCommand;
  }
}
