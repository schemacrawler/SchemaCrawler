/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import picocli.CommandLine.Option;
import picocli.CommandLine.Unmatched;

public final class ConnectionTestOptions {

  @Option(
      names = {"--connection-test", "-T"},
      description = "Test database connection and show environment information")
  private boolean connectionTest;

  @Unmatched private String[] otherOptions;

  public boolean isConnectionTest() {
    return connectionTest;
  }
}
