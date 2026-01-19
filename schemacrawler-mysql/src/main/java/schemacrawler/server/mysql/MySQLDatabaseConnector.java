/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.mysql;

import java.util.regex.Pattern;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class MySQLDatabaseConnector extends DatabaseConnector {

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseServerType dbServerType = new DatabaseServerType("mysql", "MySQL");

    final DatabaseConnectionSourceBuilder connectionSourceBuilder =
        DatabaseConnectionSourceBuilder.builder("jdbc:mysql://${host}:${port}/${database}")
            .withDefaultPort(3306)
            .withDefaultUrlx("allowPublicKeyRetrieval", true)
            .withDefaultUrlx("nullNamePatternMatchesAll", true)
            .withDefaultUrlx("getProceduresReturnsFunctions", false)
            .withDefaultUrlx("noAccessToProcedureBodies", true)
            .withDefaultUrlx("logger", "Jdk14Logger")
            .withDefaultUrlx("dumpQueriesOnException", true)
            .withDefaultUrlx("dumpMetadataOnColumnNotFound", true)
            .withDefaultUrlx("maxQuerySizeToLog", "4096")
            .withDefaultUrlx("disableMariaDbDriver", true)
            .withDefaultUrlx("useInformationSchema", true);

    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    pluginCommand
        .addOption(
            "server", String.class, "--server=mysql%n" + "Loads SchemaCrawler plug-in for MySQL")
        .addOption("host", String.class, "Host name%n" + "Optional, defaults to localhost")
        .addOption("port", Integer.class, "Port number%n" + "Optional, defaults to 3306")
        .addOption("database", String.class, "Database name");

    return DatabaseConnectorOptionsBuilder.builder(dbServerType)
        .withHelpCommand(pluginCommand)
        .withUrlSupportPredicate(
            url -> url != null && Pattern.matches("jdbc:(mysql|mariadb):.*", url))
        .withInformationSchemaViewsFromResourceFolder("/mysql.information_schema")
        .withSchemaRetrievalOptionsBuilder(
            (schemaRetrievalOptionsBuilder, connection) ->
                schemaRetrievalOptionsBuilder.withEnumDataTypeHelper(new MySQLEnumDataTypeHelper()))
        .withLimitOptionsBuilder(
            limitOptionsBuilder ->
                limitOptionsBuilder.includeSchemas(new RegularExpressionExclusionRule("sys|mysql")))
        .withDatabaseConnectionSourceBuilder(() -> connectionSourceBuilder)
        .build();
  }

  public MySQLDatabaseConnector() {
    super(databaseConnectorOptions());
  }
}
