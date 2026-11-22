/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler;

public interface CommandLineExecutor {

  /**
   * Execute a command-line.
   *
   * @param args Command-line arguments
   * @return System exit code
   */
  int execute(final String... args);
}
