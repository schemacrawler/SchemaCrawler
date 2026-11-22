/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import picocli.CommandLine;
import schemacrawler.CommandLineExecutor;

public final class SchemaSpyMain implements CommandLineExecutor {

  public static void main(final String... args) {
    System.exit(new SchemaSpyMain().execute(args));
  }

  private final Function<String[], String[]> effectiveArgsResolver;

  /** Creates a SchemaSpy command-line executor. */
  public SchemaSpyMain() {
    this(PropertiesResolver::resolveEffectiveArgs);
  }

  SchemaSpyMain(final Function<String[], String[]> effectiveArgsResolver) {
    this.effectiveArgsResolver =
        requireNonNull(effectiveArgsResolver, "No effective arguments resolver provided");
  }

  @Override
  public int execute(final String... args) {
    requireNonNull(args, "No command-line arguments provided");
    try {
      final SchemaSpyCommand command = new SchemaSpyCommand();
      final CommandLine commandLine = new CommandLine(command);
      commandLine.setExecutionExceptionHandler(
          (exception, cmd, parseResult) -> {
            cmd.getErr().println(exception.getMessage());
            return 1;
          });
      return commandLine.execute(effectiveArgsResolver.apply(args));
    } catch (final RuntimeException e) {
      System.err.println(e.getMessage());
      return 1;
    }
  }
}
