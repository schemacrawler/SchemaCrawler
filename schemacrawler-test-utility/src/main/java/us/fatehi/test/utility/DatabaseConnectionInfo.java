/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import static us.fatehi.test.utility.Utility.requireNotBlank;

import java.io.Serial;
import java.io.Serializable;

public record DatabaseConnectionInfo(String host, int port, String database, String connectionUrl)
    implements Serializable {

  @Serial private static final long serialVersionUID = 3513025340881301828L;

  public DatabaseConnectionInfo {
    host = requireNotBlank(host, "No host provided");
    if (port <= 0 || port > 65535) {
      throw new IllegalArgumentException("Bad port number provided, " + port);
    }
    database = requireNotBlank(database, "No database provided");
    connectionUrl = requireNotBlank(connectionUrl, "No database connection url provided");
  }
}
