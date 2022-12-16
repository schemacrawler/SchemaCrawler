/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test.utility;

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
