/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.databaseconnector;

import java.util.Map;
import us.fatehi.utility.datasource.DatabaseServerType;

public class DatabaseServerHostConnectionOptions implements DatabaseConnectionOptions {

  private static DatabaseServerType lookupDatabaseServerType(
      final String databaseSystemIdentifier) {
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final DatabaseConnector databaseConnector =
        databaseConnectorRegistry.findDatabaseConnectorFromDatabaseSystemIdentifier(
            databaseSystemIdentifier);
    return databaseConnector.getDatabaseServerType();
  }

  private final DatabaseServerType databaseServerType;
  private final String host;
  private final Integer port;
  private final Map<String, String> urlx;
  private final String database;

  public DatabaseServerHostConnectionOptions(
      final String databaseSystemIdentifier,
      final String host,
      final Integer port,
      final String database,
      final Map<String, String> urlx) {
    databaseServerType = lookupDatabaseServerType(databaseSystemIdentifier);
    this.host = host;
    this.port = port;
    this.database = database;
    this.urlx = urlx;
  }

  public String getDatabase() {
    return database;
  }

  @Override
  public DatabaseServerType getDatabaseServerType() {
    return databaseServerType;
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
