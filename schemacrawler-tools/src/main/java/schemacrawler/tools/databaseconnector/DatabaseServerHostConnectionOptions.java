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
    return databaseConnectorRegistry.findDatabaseConnectorFromDatabaseSystemIdentifier(databaseSystemIdentifier);
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
