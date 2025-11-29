/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility.extensions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public class CapturedLogs extends Handler implements Iterable<LogRecord> {

  private final List<LogRecord> logs;

  CapturedLogs() {
    logs = new ArrayList<>();
  }

  public void clear() {
    logs.clear();
  }

  @Override
  public void close() {}

  public boolean contains(final Level level, final Pattern pattern) {
    for (final LogRecord logRecord : logs) {
      if (logRecord.getLevel().equals(level) && pattern.matcher(logRecord.getMessage()).matches()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void flush() {}

  @Override
  public Iterator<LogRecord> iterator() {
    return logs.iterator();
  }

  @Override
  public void publish(final LogRecord record) {
    logs.add(record);
  }
}
