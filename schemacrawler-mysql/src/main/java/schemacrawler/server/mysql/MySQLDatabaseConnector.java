/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.mysql;

import java.util.regex.Pattern;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class MySQLDatabaseConnector extends DatabaseConnector {

  public MySQLDatabaseConnector() {
    super(
        new DatabaseServerType("mysql", "MySQL"),
        url -> url != null && Pattern.matches("jdbc:(mysql|mariadb):.*", url),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/mysql.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) ->
            schemaRetrievalOptionsBuilder.withEnumDataTypeHelper(new MySQLEnumDataTypeHelper()),
        limitOptionsBuilder ->
            limitOptionsBuilder.includeSchemas(new RegularExpressionExclusionRule("sys|mysql")),
        () ->
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
                .withDefaultUrlx("useInformationSchema", true));
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
        .addOption(
            "server", String.class, "--server=mysql%n" + "Loads SchemaCrawler plug-in for MySQL")
        .addOption("host", String.class, "Host name%n" + "Optional, defaults to localhost")
        .addOption("port", Integer.class, "Port number%n" + "Optional, defaults to 3306")
        .addOption("database", String.class, "Database name");
    return pluginCommand;
  }
}
