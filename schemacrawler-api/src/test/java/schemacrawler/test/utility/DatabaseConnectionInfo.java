/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
