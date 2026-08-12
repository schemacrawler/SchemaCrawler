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
import java.util.Locale;
import schemacrawler.tools.commandline.command.LogLevel;

/**
 * Maps SchemaSpy logging options to SchemaCrawler {@code --log-level} and {@code --config-file}
 * argument tokens.
 */
final class LogArgsMapper {

  /** Input record for logging options. */
  record LogArgs(String logLevel, boolean debug) {}

  private final LogArgs input;

  LogArgsMapper(final LogArgs input) {
    this.input = requireNonNull(input, "No log input provided");
  }

  /**
   * Returns argument tokens for the SchemaCrawler log/config group.
   *
   * <p>Covers: {@code --log-level}, {@code --config-file}.
   *
   * @return log/config argument tokens
   */
  List<String> toArgs() {
    final List<String> args = new ArrayList<>();
    args.add("--log-level");
    args.add(toLogLevel().name());
    return args;
  }

  private LogLevel toLogLevel() {
    if (input.debug()) {
      return LogLevel.FINE;
    }
    if (isBlank(input.logLevel())) {
      return LogLevel.INFO;
    }
    final String normalized = input.logLevel().trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "TRACE", "FINEST" -> LogLevel.FINEST;
      case "FINE" -> LogLevel.FINE;
      case "DEBUG" -> LogLevel.CONFIG;
      case "WARN", "WARNING" -> LogLevel.WARNING;
      case "ERROR", "SEVERE" -> LogLevel.SEVERE;
      default -> LogLevel.OFF;
    };
  }
}
