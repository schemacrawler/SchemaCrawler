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

public final class InteractiveShellOptions {

  @Option(
      names = {"--interactive", "--shell"},
      description = "Start SchemaCrawler interactive shell")
  private boolean interactive;

  @Unmatched private String[] otherOptions;

  public boolean isInteractive() {
    return interactive;
  }
}
