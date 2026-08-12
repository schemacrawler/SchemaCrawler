/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps SchemaSpy load options to SchemaCrawler {@code --info-level} and {@code --load-row-counts}
 * argument tokens.
 *
 * <p>Row counts are loaded by default. Pass {@code -norows} to suppress them.
 */
final class LoadArgsMapper {

  /** Input record for load options. */
  record LoadArgs(boolean noRows) {}

  private final LoadArgs input;

  LoadArgsMapper(final LoadArgs input) {
    this.input = requireNonNull(input, "No load input provided");
  }

  /**
   * Returns argument tokens for the SchemaCrawler load group.
   *
   * <p>Covers: {@code --info-level}, {@code --load-row-counts}.
   *
   * @return load argument tokens
   */
  List<String> toArgs() {
    final List<String> args = new ArrayList<>();
    args.add("--info-level");
    args.add("maximum");
    if (!input.noRows()) {
      args.add("--load-row-counts");
      args.add("true");
    }
    return args;
  }
}
