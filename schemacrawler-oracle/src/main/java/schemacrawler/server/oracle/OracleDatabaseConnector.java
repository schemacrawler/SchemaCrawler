/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.oracle;

import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.primaryKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.procedureParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.proceduresRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tablesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.typeInfoRetrievalStrategy;

import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class OracleDatabaseConnector extends DatabaseConnector {

  public static final DatabaseServerType DB_SERVER_TYPE =
      new DatabaseServerType("oracle", "Oracle");

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseServerType dbServerType = DB_SERVER_TYPE;

    final DatabaseConnectionSourceBuilder connectionSourceBuilder =
        DatabaseConnectionSourceBuilder.builder("jdbc:oracle:thin:@//${host}:${port}/${database}")
            .withDefaultPort(1521)
            .withDefaultUrlx("remarksReporting", true)
            .withDefaultUrlx("restrictGetTables", true)
            .withDefaultUrlx("useFetchSizeWithLongColumn", true)
            .withConnectionInitializer(new OracleConnectionInitializer());

    final PluginCommand pluginCommand = PluginCommand.newDatabasePluginCommand(dbServerType);
    pluginCommand
        .addOption(
            "server", String.class, "--server=oracle%n" + "Loads SchemaCrawler plug-in for Oracle")
        .addOption("host", String.class, "Host name%n" + "Optional, defaults to localhost")
        .addOption("port", Integer.class, "Port number%n" + "Optional, defaults to 1521")
        .addOption(
            "database",
            String.class,
            "Oracle Service Name%n"
                + "You can use a query similar to the one below to find it.%n"
                + "SELECT GLOBAL_NAME FROM GLOBAL_NAME");

    return DatabaseConnectorOptionsBuilder.builder(dbServerType)
        .withHelpCommand(pluginCommand)
        .withUrlStartsWith("jdbc:oracle:")
        .withInformationSchemaViewsBuilder(new OracleInformationSchemaViewsBuilder())
        .withSchemaRetrievalOptionsBuilder(
            (schemaRetrievalOptionsBuilder, connection) ->
                schemaRetrievalOptionsBuilder
                    .with(typeInfoRetrievalStrategy, data_dictionary_all)
                    .with(tablesRetrievalStrategy, data_dictionary_all)
                    .with(tableColumnsRetrievalStrategy, data_dictionary_all)
                    .with(primaryKeysRetrievalStrategy, data_dictionary_all)
                    .with(foreignKeysRetrievalStrategy, data_dictionary_all)
                    .with(indexesRetrievalStrategy, data_dictionary_all)
                    .with(proceduresRetrievalStrategy, data_dictionary_all)
                    .with(procedureParametersRetrievalStrategy, data_dictionary_all)
                    .with(functionsRetrievalStrategy, data_dictionary_all)
                    .with(functionParametersRetrievalStrategy, data_dictionary_all))
        .withLimitOptionsBuilder(
            limitOptionsBuilder ->
                limitOptionsBuilder.includeSchemas(new OracleSchemaExclusionRule()))
        .withDatabaseConnectionSourceBuilder(() -> connectionSourceBuilder)
        .build();
  }

  public OracleDatabaseConnector() {
    super(databaseConnectorOptions());
    System.setProperty("oracle.jdbc.Trace", "true");
  }
}
