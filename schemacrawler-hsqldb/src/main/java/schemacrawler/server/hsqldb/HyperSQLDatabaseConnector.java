/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.server.hsqldb;

import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnPrivilegesRetrievalStrategy;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;

public final class HyperSQLDatabaseConnector extends DatabaseConnector {

  public HyperSQLDatabaseConnector() {
    super(
        new DatabaseServerType("hsqldb", "HyperSQL DataBase"),
        url -> url != null && url.startsWith("jdbc:hsqldb:"),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/hsqldb.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) ->
            schemaRetrievalOptionsBuilder.with(
                tableColumnPrivilegesRetrievalStrategy, data_dictionary_all),
        limitOptionsBuilder -> {},
        () ->
            DatabaseConnectionSourceBuilder.builder(
                    "jdbc:hsqldb:hsql://${host}:${port}/${database}")
                .withDefaultPort(9001)
                .withDefaultUrlx("readonly", true)
                .withDefaultUrlx("hsqldb.lock_file", false));
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
        .addOption(
            "server",
            String.class,
            "--server=hsqldb%n" + "Loads SchemaCrawler plug-in for HyperSQL")
        .addOption("host", String.class, "Host name%n" + "Optional, defaults to localhost")
        .addOption("port", Integer.class, "Port number%n" + "Optional, defaults to 9001")
        .addOption("database", String.class, "Database name");
    return pluginCommand;
  }
}
