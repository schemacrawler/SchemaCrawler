/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps SchemaSpy output options to SchemaCrawler {@code --command}, {@code --output-format}, {@code
 * --output-file}, and {@code --language} argument tokens.
 */
final class ExecuteArgsMapper {

  /** Input record for execute/output options. */
  record ExecuteArgs(String outputPath, String locale) {}

  private final ExecuteArgs input;

  ExecuteArgsMapper(final ExecuteArgs input) {
    this.input = requireNonNull(input, "No execute input provided");
  }

  /**
   * Returns argument tokens for the SchemaCrawler execute/output group.
   *
   * <p>Covers: {@code --command}, {@code --output-format}, {@code --output-file}, {@code
   * --language}.
   *
   * @return execute/output argument tokens
   */
  List<String> toArgs() {
    final List<String> args = new ArrayList<>();
    args.add("--command");
    args.add("scribe");
    args.add("--output-format");
    args.add("okf");
    args.add("--output-file");
    args.add(input.outputPath());
    if (!isBlank(input.locale())) {
      args.add("--language");
      args.add(input.locale());
    }
    return args;
  }
}
