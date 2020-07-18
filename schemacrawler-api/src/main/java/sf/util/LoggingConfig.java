/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.lang.management.ManagementFactory.getPlatformMBeanServer;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LoggingConfig
{

  /**
   * Required for use in "java.util.logging.config.class" system property.
   */
  public LoggingConfig()
  {
    this(null);
  }

  public LoggingConfig(final Level level)
  {
    applyApplicationLogLevel(level);
  }

  /**
   * @param logLevel
   *   Log level to be set
   * @see <a href="https://picocli.info/#_tracing">picocli Tracing</a>
   */
  private void applyPicocliLogLevel(final Level logLevel)
  {
    final String picocliLogLevel;
    final String logLevelName = logLevel.getName();
    switch (logLevelName)
    {
      case "OFF":
        picocliLogLevel = "OFF";
        break;
      case "SEVERE":
      case "WARNING":
        picocliLogLevel = "WARN";
        break;
      case "CONFIG":
      case "INFO":
        picocliLogLevel = "INFO";
        break;
      default:
        picocliLogLevel = "DEBUG";
        break;
    }

    System.setProperty("picocli.trace", picocliLogLevel);
  }

  /**
   * @param logLevel
   *   Log level to be set
   * @see <a href="https://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html">SLF4J
   *   log levels</a>
   */
  private void applySlf4jLogLevel(final Level logLevel)
  {
    final String slf4jLogLevel;
    switch (logLevel.getName())
    {
      case "OFF":
        slf4jLogLevel = "off";
        break;
      case "SEVERE":
        slf4jLogLevel = "error";
        break;
      case "WARNING":
        slf4jLogLevel = "warn";
        break;
      case "CONFIG":
        slf4jLogLevel = "debug";
        break;
      case "INFO":
        slf4jLogLevel = "info";
        break;
      default:
        slf4jLogLevel = "trace";
        break;
    }

    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", slf4jLogLevel);
  }

  /**
   * Sets the application-wide log level.
   *
   * @param applicationLogLevel
   *   Log level to set
   */
  private void applyApplicationLogLevel(final Level applicationLogLevel)
  {
    final Level logLevel;
    if (applicationLogLevel == null)
    {
      logLevel = Level.OFF;
    }
    else
    {
      logLevel = applicationLogLevel;
    }

    final LogManager logManager = LogManager.getLogManager();
    final List<String> loggerNames =
      Collections.list(logManager.getLoggerNames());
    for (final String loggerName : loggerNames)
    {
      final Logger logger = logManager.getLogger(loggerName);
      if (logger != null)
      {
        logger.setLevel(null);
        for (final Handler handler : logger.getHandlers())
        {
          try
          {
            handler.setEncoding("UTF-8");
          }
          catch (final UnsupportedEncodingException e)
          {
            // Ignore exception
          }
          handler.setLevel(logLevel);
        }
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);

    applySlf4jLogLevel(logLevel);
    applyPicocliLogLevel(logLevel);
    applyOracleLogLevel(logLevel);
  }

  private void applyOracleLogLevel(final Level logLevel)
  {
    try
    {
      // https://docs.oracle.com/cd/B28359_01/java.111/b31224/diagnose.htm
      final String loader = Thread
        .currentThread()
        .getContextClassLoader()
        .toString()
        .replaceAll("[,=:\"]+", "");
      final ObjectName name =
        new ObjectName("com.oracle.jdbc:type=diagnosability,name=" + loader);

      final MBeanServer mbs = getPlatformMBeanServer();

      final boolean oracleLogging;
      final String logLevelName = logLevel.getName();
      switch (logLevelName)
      {
        case "OFF":
        case "SEVERE":
        case "WARNING":
        case "CONFIG":
        case "INFO":
          oracleLogging = false;
          break;
        default:
          oracleLogging = true;
      }

      mbs.setAttribute(name, new Attribute("LoggingEnabled", oracleLogging));
    }
    catch (final Exception e)
    {
      // Ignore
    }
  }

}
