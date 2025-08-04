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

  public DatabaseUrlConnectionOptions(final String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  public String getConnectionUrl() {
    return connectionUrl;
  }
}
