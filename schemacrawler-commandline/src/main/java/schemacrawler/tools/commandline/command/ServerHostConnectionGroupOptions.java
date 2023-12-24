/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.command;

import java.util.Map;

import picocli.CommandLine.Option;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;

public class ServerHostConnectionGroupOptions implements ConnectionOptions {

  @Option(
      names = {"--database"},
      description = "Database name")
  private String database;

  @Option(
      names = {"--server"},
      required = true,
      description = {
        "Where <database system identifier> is a database for "
            + "which a SchemaCrawler plug-in is available",
        "Use one of ${COMPLETION-CANDIDATES}"
      },
      completionCandidates = AvailableServers.class,
      paramLabel = "<database system identifier>")
  private String databaseSystemIdentifier;

  @Option(
      names = {"--host"},
      description = "Database server host")
  private String host;

  @Option(
      names = {"--port"},
      description = "Database server port")
  private Integer port;

  @Option(
      names = {"--urlx"},
      description = "JDBC URL additional properties")
  private Map<String, String> urlx;

  @Override
  public DatabaseConnectionOptions toDatabaseConnectionOptions() {
    return new DatabaseServerHostConnectionOptions(
        databaseSystemIdentifier, host, port, database, urlx);
  }
}
