/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.sqlserver;

import schemacrawler.plugins.dbconnectors.DatabaseConnectorDefinitionAdapter;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.yaml.DatabasePluginYamlDeserializer;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public final class SqlServerDatabaseConnector extends DatabaseConnector {

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseConnectorDefinition definition =
        new DatabasePluginYamlDeserializer()
            .parse(new ClasspathInputResource("dbconnectors/sqlserver.yaml"));

    final DatabaseConnectionSourceBuilder connectionSourceBuilder =
        DatabaseConnectorDefinitionAdapter.toConnectionSourceBuilder(definition)
            .withConnectionInitializer(new SqlServerConnectionInitializer());

    final DatabaseServerType dbServerType =
        DatabaseConnectorDefinitionAdapter.toOptionsBuilder(definition).toOptions().dbServerType();
    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    pluginCommand
        .addOption(
            "server",
            String.class,
            "--server=sqlserver",
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

    return DatabaseConnectorDefinitionAdapter.toOptionsBuilder(definition)
        .withHelpCommand(pluginCommand)
        .withDatabaseConnectionSourceBuilder(() -> connectionSourceBuilder)
        .build();
  }

  public SqlServerDatabaseConnector() {
    super(databaseConnectorOptions());
  }
}
