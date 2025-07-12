/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.databaseconnector;

public class DatabaseUrlConnectionOptions implements DatabaseConnectionOptions {

  private final String connectionUrl;

  public DatabaseUrlConnectionOptions(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  public String getConnectionUrl() {
    return connectionUrl;
  }

  @Override
  public DatabaseConnector getDatabaseConnector() {
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    return databaseConnectorRegistry.findDatabaseConnectorFromUrl(connectionUrl);
  }
}
