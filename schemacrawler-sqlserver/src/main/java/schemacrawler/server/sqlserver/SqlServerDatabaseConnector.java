/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.sqlserver;

import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;

public final class SqlServerDatabaseConnector extends DatabaseConnector {

  public SqlServerDatabaseConnector() {
    super(
        new DatabaseServerType("sqlserver", "Microsoft SQL Server"),
        url -> url != null && url.startsWith("jdbc:sqlserver:"),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/sqlserver.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) -> {},
        limitOptionsBuilder ->
            limitOptionsBuilder.includeSchemas(
                new RegularExpressionRule(
                    ".*\\.dbo", "model\\..*|master\\..*|msdb\\..*|tempdb\\..*|rdsadmin\\..*")),
        () ->
            DatabaseConnectionSourceBuilder.builder(
                    "jdbc:sqlserver://${host}:${port};databaseName=${database}")
                .withDefaultPort(1433)
                .withDefaultUrlx("applicationName", "SchemaCrawler")
                .withDefaultUrlx("encrypt", false)
                .withConnectionInitializer(new SqlServerConnectionInitializer()));
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
        .addOption(
            "server",
            String.class,
            "--server=mysql",
            "Loads SchemaCrawler plug-in for Microsoft SQL Server",
            "If you are using instance names, named pipes, or Windows authentication, "
                + "you will need to provide a database connection URL on "
                + "the SchemaCrawler command-line",
            "See https://www.schemacrawler.com/database-support.html")
        .addOption("host", String.class, "Host name", "Optional, defaults to localhost")
        .addOption("port", Integer.class, "Port number", "Optional, defaults to 1433")
        .addOption(
            "database",
            String.class,
            "Database name",
            "Be sure to also restrict your schemas to this database, "
                + "by using an additional option,",
            "--schemas=<database>.dbo");
    return pluginCommand;
  }
}
