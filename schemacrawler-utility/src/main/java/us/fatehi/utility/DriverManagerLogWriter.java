/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import java.io.IOException;
import java.io.Writer;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

class DriverManagerLogWriter extends Writer {

  private static final Logger LOGGER = Logger.getLogger(DriverManager.class.getName());
  private static final Level LOG_LEVEL = Level.CONFIG;

  private final boolean shouldLog;
  private final StringBuilder buffer;

  public DriverManagerLogWriter() {
    shouldLog = LOGGER.isLoggable(LOG_LEVEL);
    buffer = new StringBuilder();
  }

  @Override
  public void close() throws IOException {
    flush();
  }

  @Override
  public void flush() throws IOException {
    if (!shouldLog) {
      return;
    }

    if (buffer.length() > 0) {
      LOGGER.log(LOG_LEVEL, buffer.toString());
      buffer.setLength(0);
    }
  }

  @Override
  public void write(final char[] cbuf, final int off, final int len) throws IOException {
    if (!shouldLog) {
      return;
    }

    for (int i = off; i < off + len; i++) {
      final char c = cbuf[i];
      if (c == '\n') {
        flush();
      } else {
        buffer.append(c);
      }
    }
  }
}
