/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.offline;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;

public final class OfflineDatabaseConnector extends DatabaseConnector {

  public static final DatabaseServerType DB_SERVER_TYPE =
      new DatabaseServerType("offline", "SchemaCrawler Offline Catalog Snapshot");

  public OfflineDatabaseConnector() {
    super(
        DB_SERVER_TYPE,
        url -> url != null && url.startsWith("jdbc:offline:"),
        (informationSchemaViewsBuilder, connection) -> {},
        (schemaRetrievalOptionsBuilder, connection) -> {},
        limitOptionsBuilder -> {},
        () -> DatabaseConnectionSourceBuilder.builder("jdbc:offline:${database}"));
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
        .addOption(
            "server",
            String.class,
            "--server=offline%n" + "Loads SchemaCrawler plug-in for offline catalog snapshots")
        .addOption("host", String.class, "Should be omitted")
        .addOption("port", Integer.class, "Should be omitted")
        .addOption(
            "database", String.class, "File name and location of the database metadata snapshot");
    return pluginCommand;
  }
}
