/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import picocli.CommandLine.ArgGroup;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;

public class DatabaseConnectionGroupOptions {

  @ArgGroup(exclusive = false, heading = "%nFor connecting to specific databases, use%n")
  private ServerHostConnectionGroupOptions databaseConfigConnectionOptions;

  @ArgGroup(
      exclusive = false,
      heading = "%nIf your database does not have a " + "SchemaCrawler plug-in, use%n")
  private UrlConnectionGroupOptions databaseUrlConnectionOptions;

  DatabaseConnectionOptions getDatabaseConnectionOptions() {
    if (databaseConfigConnectionOptions != null) {
      return databaseConfigConnectionOptions.toDatabaseConnectionOptions();
    } else {
      return databaseUrlConnectionOptions.toDatabaseConnectionOptions();
    }
  }
}
