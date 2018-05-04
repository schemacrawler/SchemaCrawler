/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
package sf.util;


import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SchemaCrawlerLogger
{

  private final static String loggerClass = SchemaCrawlerLogger.class.getName();

  public static SchemaCrawlerLogger getLogger(final String name)
  {
    return new SchemaCrawlerLogger(Logger.getLogger(name));
  }

  private static void updateSource(final LogRecord lr, final int depth)
  {
    final StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
    if (steArray == null)
    {
      return;
    }
    for (int i = 1; i < steArray.length; i++)
    {
      final StackTraceElement ste = steArray[i];
      if (!loggerClass.equals(ste.getClassName()))
      {
        final int index = i + depth;
        if (index >= 0 && index < steArray.length)
        {
          final StackTraceElement ste_i = steArray[index];
          lr.setSourceMethodName(ste_i.getMethodName());
          lr.setSourceClassName(ste_i.getClassName());
          break;
        }
      }
    }
  }

  private final Logger logger;

  private SchemaCrawlerLogger(final Logger logger)
  {
    this.logger = logger;
  }

  public boolean isLoggable(final Level level)
  {
    return logger.isLoggable(level);
  }

  public void log(final Level level,
                  final int depth,
                  final String msg,
                  final Throwable thrown)
  {
    requireNonNull(level, "No log level provided");

    if (!logger.isLoggable(level))
    {
      return;
    }

    final LogRecord lr = new LogRecord(level, msg);
    updateSource(lr, depth);
    if (thrown != null)
    {
      lr.setThrown(thrown);
    }

    logger.log(lr);
  }

  public void log(final Level level, final String msg)
  {
    log(level, msg, null);
  }

  public void log(final Level level, final String msg, final Throwable thrown)
  {
    log(level, 0, msg, thrown);
  }

  public void log(final Level level, final Supplier<String> msgSupplier)
  {
    if (!logger.isLoggable(level))
    {
      return;
    }
    log(level, msgSupplier.get());
  }

  public void log(final Level level,
                  final Supplier<String> msgSupplier,
                  final Throwable thrown)
  {
    if (!logger.isLoggable(level))
    {
      return;
    }
    log(level, msgSupplier.get(), thrown);
  }

}
