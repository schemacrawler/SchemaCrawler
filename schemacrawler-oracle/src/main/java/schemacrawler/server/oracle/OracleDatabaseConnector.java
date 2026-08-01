/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.oracle;

import schemacrawler.plugins.dbconnectors.DatabaseConnectorDefinitionAdapter;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.yaml.DatabasePluginYamlDeserializer;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseServerType;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public final class OracleDatabaseConnector extends DatabaseConnector {

  public static final DatabaseServerType DB_SERVER_TYPE =
      new DatabaseServerType("oracle", "Oracle");

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseConnectorDefinition definition =
        new DatabasePluginYamlDeserializer()
            .parse(new ClasspathInputResource("dbconnectors/oracle.yaml"));

    final DatabaseConnectionSourceBuilder connectionSourceBuilder =
        DatabaseConnectorDefinitionAdapter.toConnectionSourceBuilder(definition)
            .withConnectionInitializer(new OracleConnectionInitializer());

    return DatabaseConnectorDefinitionAdapter.toOptionsBuilder(definition)
        .withDatabaseConnectionSourceBuilder(() -> connectionSourceBuilder)
        .withInformationSchemaViewsBuilder(new OracleInformationSchemaViewsBuilder())
        .withLimitOptionsBuilder(
            limitOptionsBuilder ->
                limitOptionsBuilder.includeSchemas(new OracleSchemaExclusionRule()))
        .build();
  }

  public OracleDatabaseConnector() {
    super(databaseConnectorOptions());
    System.setProperty("oracle.jdbc.Trace", "true");
  }
}
