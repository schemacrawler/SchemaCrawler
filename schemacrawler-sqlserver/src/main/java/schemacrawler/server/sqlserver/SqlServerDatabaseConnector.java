/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.sqlserver;

import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_over_schemas;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.metadata_over_schemas;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.primaryKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.procedureParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.proceduresRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.routineReferencesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.routinesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableAdditionalAttributesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableCheckConstraintsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnAdditionalAttributesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableConstraintColumnsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableConstraintsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.triggersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.viewInformationRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.viewTableUsageRetrievalStrategy;

import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class SqlServerDatabaseConnector extends DatabaseConnector {

  public SqlServerDatabaseConnector() {
    super(
        new DatabaseServerType("sqlserver", "Microsoft SQL Server"),
        url -> url != null && url.startsWith("jdbc:sqlserver:"),
        (informationSchemaViewsBuilder, connection) ->
            informationSchemaViewsBuilder.fromResourceFolder("/sqlserver.information_schema"),
        (schemaRetrievalOptionsBuilder, connection) ->
            schemaRetrievalOptionsBuilder
                .with(tableColumnsRetrievalStrategy, metadata_over_schemas)
                .with(primaryKeysRetrievalStrategy, data_dictionary_over_schemas)
                .with(foreignKeysRetrievalStrategy, data_dictionary_over_schemas)
                .with(indexesRetrievalStrategy, data_dictionary_over_schemas)
                .with(viewInformationRetrievalStrategy, data_dictionary_over_schemas)
                .with(viewTableUsageRetrievalStrategy, data_dictionary_over_schemas)
                .with(triggersRetrievalStrategy, data_dictionary_over_schemas)
                .with(tableConstraintsRetrievalStrategy, data_dictionary_over_schemas)
                .with(tableConstraintColumnsRetrievalStrategy, data_dictionary_over_schemas)
                .with(tableCheckConstraintsRetrievalStrategy, data_dictionary_over_schemas)
                .with(tableAdditionalAttributesRetrievalStrategy, data_dictionary_over_schemas)
                .with(
                    tableColumnAdditionalAttributesRetrievalStrategy, data_dictionary_over_schemas)
                .with(routinesRetrievalStrategy, data_dictionary_over_schemas)
                .with(routineReferencesRetrievalStrategy, data_dictionary_over_schemas)
                .with(proceduresRetrievalStrategy, data_dictionary_over_schemas)
                .with(procedureParametersRetrievalStrategy, data_dictionary_over_schemas)
                .with(functionsRetrievalStrategy, data_dictionary_over_schemas)
                .with(functionParametersRetrievalStrategy, data_dictionary_over_schemas),
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
