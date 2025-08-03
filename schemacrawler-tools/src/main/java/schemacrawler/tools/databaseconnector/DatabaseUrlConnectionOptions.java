/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.databaseconnector;

import us.fatehi.utility.datasource.DatabaseServerType;

public class DatabaseUrlConnectionOptions implements DatabaseConnectionOptions {

  private static DatabaseServerType lookupDatabaseServerType(final String connectionUrl) {
    final DatabaseConnectorRegistry databaseConnectorRegistry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final DatabaseConnector databaseConnector =
        databaseConnectorRegistry.findDatabaseConnectorFromUrl(connectionUrl);
    return databaseConnector.getDatabaseServerType();
  }

  private final DatabaseServerType databaseServerType;
  private final String connectionUrl;

  public DatabaseUrlConnectionOptions(final String connectionUrl) {
    databaseServerType = lookupDatabaseServerType(connectionUrl);
    this.connectionUrl = connectionUrl;
  }

  public String getConnectionUrl() {
    return connectionUrl;
  }

  @Override
  public DatabaseServerType getDatabaseServerType() {
    return databaseServerType;
  }
}
