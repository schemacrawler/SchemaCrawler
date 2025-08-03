/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import picocli.CommandLine.Option;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseUrlConnectionOptions;

public class UrlConnectionGroupOptions implements ConnectionOptions {

  @Option(
      names = {"--url"},
      required = true,
      description = "JDBC connection URL to the database")
  private String connectionUrl;

  @Override
  public DatabaseConnectionOptions toDatabaseConnectionOptions() {
    return new DatabaseUrlConnectionOptions(connectionUrl);
  }
}
