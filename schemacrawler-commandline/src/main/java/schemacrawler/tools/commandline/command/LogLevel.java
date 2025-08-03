/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import java.util.logging.Level;

public enum LogLevel {
  OFF(Level.OFF),
  SEVERE(Level.SEVERE),
  WARNING(Level.WARNING),
  INFO(Level.INFO),
  CONFIG(Level.CONFIG),
  FINE(Level.FINE),
  FINER(Level.FINER),
  FINEST(Level.FINEST),
  ALL(Level.ALL);

  private final Level level;

  LogLevel(final Level level) {
    this.level = level;
  }

  public Level getLevel() {
    return level;
  }
}
