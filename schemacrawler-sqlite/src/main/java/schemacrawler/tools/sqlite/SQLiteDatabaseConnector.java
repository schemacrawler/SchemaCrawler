/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.sqlite;

import schemacrawler.plugins.dbconnectors.DatabaseConnectorDefinitionAdapter;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.yaml.DatabasePluginYamlDeserializer;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public final class SQLiteDatabaseConnector extends DatabaseConnector {

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseConnectorDefinition definition =
        new DatabasePluginYamlDeserializer()
            .parse(new ClasspathInputResource("dbconnectors/sqlite.yaml"));
    return DatabaseConnectorDefinitionAdapter.toOptionsBuilder(definition).build();
  }

  public SQLiteDatabaseConnector() {
    super(databaseConnectorOptions());
  }
}
