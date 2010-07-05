/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.options;


import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Options;

public class ApplicationOptions
  implements Options
{

  private static final long serialVersionUID = -2497570007150087268L;

  private Level applicationLogLevel;
  private boolean showHelp;

  /**
   * Sets the application-wide log level.
   */
  public void applyApplicationLogLevel()
  {
    if (applicationLogLevel == null)
    {
      return;
    }

    final LogManager logManager = LogManager.getLogManager();
    final List<String> loggerNames = Collections.list(logManager
      .getLoggerNames());
    for (final String loggerName: loggerNames)
    {
      final Logger logger = logManager.getLogger(loggerName);
      if (logger != null)
      {
        logger.setLevel(null);
        final Handler[] handlers = logger.getHandlers();
        for (final Handler handler: handlers)
        {
          handler.setLevel(applicationLogLevel);
        }
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(applicationLogLevel);
  }

  public Level getApplicationLogLevel()
  {
    return applicationLogLevel;
  }

  public boolean isShowHelp()
  {
    return showHelp;
  }

  public void setApplicationLogLevel(final Level applicationLogLevel)
  {
    this.applicationLogLevel = applicationLogLevel;
  }

  public void setShowHelp(final boolean showHelp)
  {
    this.showHelp = showHelp;
  }

}
