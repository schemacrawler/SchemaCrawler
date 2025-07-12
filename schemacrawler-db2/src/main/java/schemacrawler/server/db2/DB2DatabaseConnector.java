/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.server.db2;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;

public final class DB2DatabaseConnector extends DatabaseConnector {

  public DB2DatabaseConnector() {
    super(
        new DatabaseServerType("db2", "IBM DB2"),
        url -> url != null && url.startsWith("jdbc:db2:"),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/db2.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) -> {},
        limitOptionsBuilder -> {},
        () ->
            DatabaseConnectionSourceBuilder.builder("jdbc:db2://${host}:${port}/${database}")
                .withDefaultPort(50000)
                .withDefaultUrlx("retrieveMessagesFromServerOnGetMessage", true));
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
        .addOption(
            "server", String.class, "--server=db2%n" + "Loads SchemaCrawler plug-in for IBM DB2")
        .addOption("host", String.class, "Host name%n" + "Optional, defaults to localhost")
        .addOption("port", Integer.class, "Port number%n" + "Optional, defaults to 50000")
        .addOption("database", String.class, "Database name");
    return pluginCommand;
  }
}
