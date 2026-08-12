/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static java.util.Objects.requireNonNull;

import picocli.CommandLine;

public final class SchemaSpyMain {

  public static int execute(final String... args) {
    requireNonNull(args, "No command-line arguments provided");
    try {
      final SchemaSpyCommand command = new SchemaSpyCommand();
      final CommandLine commandLine = new CommandLine(command);
      commandLine.setExecutionExceptionHandler(
          (exception, cmd, parseResult) -> {
            cmd.getErr().println(exception.getMessage());
            return 1;
          });
      return commandLine.execute(resolveEffectiveArgs(args));
    } catch (final RuntimeException e) {
      System.err.println(e.getMessage());
      return 1;
    }
  }

  public static void main(final String[] args) {
    System.exit(execute(args));
  }

  static String[] resolveEffectiveArgs(final String... args) {
    return PropertiesResolver.resolveEffectiveArgs(args);
  }

  private SchemaSpyMain() {
    // Utility class
  }
}
