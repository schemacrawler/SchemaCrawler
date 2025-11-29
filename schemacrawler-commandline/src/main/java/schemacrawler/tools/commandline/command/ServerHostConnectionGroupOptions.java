/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
