/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.utility;

import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serializable;

public class DatabaseConnectionInfo implements Serializable {

  private static final long serialVersionUID = 3513025340881301828L;

  private final String host;
  private final int port;
  private final String database;
  private final String url;

  public DatabaseConnectionInfo(
      final String host, final int port, final String database, final String url) {
    this.host = requireNotBlank(host, "No host provided");
    if (port <= 0 || port > 65535) {
      throw new IllegalArgumentException("Bad port number provided, " + port);
    }
    this.port = port;
    this.database = requireNotBlank(database, "No database provided");
    this.url = requireNotBlank(url, "No url provided");
  }

  public String getConnectionUrl() {
    return url;
  }

  public String getDatabase() {
    return database;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }
}
