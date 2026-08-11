/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.oracle;

import java.util.function.Supplier;
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

    // The connection builder is mutable and request-specific values (host, database, user, etc.)
    // are applied during connection creation. Build a fresh instance each time to prevent
    // concurrent requests from sharing and mutating the same builder state.
    final Supplier<DatabaseConnectionSourceBuilder> connectionSourceBuilderSupplier =
        () ->
            new DatabaseConnectorDefinitionAdapter(definition)
                .toConnectionSourceBuilder()
                .withConnectionInitializer(new OracleConnectionInitializer());

    return new DatabaseConnectorDefinitionAdapter(definition)
        .toDatabaseConnectorOptionsBuilder()
        .withDatabaseConnectionSourceBuilder(connectionSourceBuilderSupplier)
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
