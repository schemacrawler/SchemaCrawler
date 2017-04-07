/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SchemaCrawlerLogger
{

  public static SchemaCrawlerLogger getLogger(final String name)
  {
    return new SchemaCrawlerLogger(Logger.getLogger(name));
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

  public void log(final Level level, final String msg)
  {
    logger.log(level, msg);
  }

  public void log(final Level level,
                  final String sourceClass,
                  final String sourceMethod,
                  final Supplier<String> msg)
  {
    if (!isLoggable(level))
    {
      return;
    }
    final LogRecord lr = new LogRecord(level, msg.get());
    lr.setSourceClassName(sourceClass);
    lr.setSourceMethodName(sourceMethod);
    logger.log(lr);
  }

  public void log(final Level level, final String msg, final Throwable thrown)
  {
    logger.log(level, msg, thrown);
  }

  public void log(final Level level, final Supplier<String> msgSupplier)
  {
    if (!logger.isLoggable(level))
    {
      return;
    }

    logger.log(level, msgSupplier.get());
  }

  public void log(final Level level,
                  final Supplier<String> msgSupplier,
                  final Throwable thrown)
  {
    if (!logger.isLoggable(level))
    {
      return;
    }

    logger.log(level, msgSupplier.get(), thrown);
  }

}
