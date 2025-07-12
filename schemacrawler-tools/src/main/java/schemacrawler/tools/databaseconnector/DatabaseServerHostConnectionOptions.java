/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.databaseconnector;

import java.util.Map;

public class DatabaseServerHostConnectionOptions implements DatabaseConnectionOptions {

  private final String database;
  private final String databaseSystemIdentifier;
  private final String host;
  private final Integer port;
  private final Map<String, String> urlx;

  public DatabaseServerHostConnectionOptions(
      final String databaseSystemIdentifier,
      final String host,
      final Integer port,
      final String database,
      final Map<String, String> urlx) {
    this.databaseSystemIdentifier = databaseSystemIdentifier;
    this.host = host;
    this.port = port;
    this.database = database;
    this.urlx = urlx;
  }

  public String getDatabase() {
    return database;
  }

  @Override
  public DatabaseConnector getDatabaseConnector() {
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    return databaseConnectorRegistry.findDatabaseConnectorFromDatabaseSystemIdentifier(
        databaseSystemIdentifier);
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }

  public Map<String, String> getUrlx() {
    return urlx;
  }
}
