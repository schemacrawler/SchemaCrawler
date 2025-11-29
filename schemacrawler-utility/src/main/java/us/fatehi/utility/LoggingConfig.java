/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LoggingConfig {

  /** Required for use in "java.util.logging.config.class" system property. */
  public LoggingConfig() {
    this(null);
  }

  public LoggingConfig(final Level level) {
    applyApplicationLogLevel(level);
  }

  /**
   * Sets the application-wide log level.
   *
   * @param applicationLogLevel Log level to set
   */
  private void applyApplicationLogLevel(final Level applicationLogLevel) {
    final Level logLevel;
    if (applicationLogLevel == null) {
      logLevel = Level.OFF;
    } else {
      logLevel = applicationLogLevel;
    }

    final LogManager logManager = LogManager.getLogManager();
    final List<String> loggerNames = Collections.list(logManager.getLoggerNames());
    for (final String loggerName : loggerNames) {
      final Logger logger = logManager.getLogger(loggerName);
      if (logger != null) {
        logger.setLevel(null);
        for (final Handler handler : logger.getHandlers()) {
          try {
            handler.setEncoding("UTF-8");
          } catch (final UnsupportedEncodingException e) {
            // Ignore exception
          }
          try {
            handler.setLevel(logLevel);
            if (logLevel == Level.OFF) {
              logger.removeHandler(handler);
            }
          } catch (final Exception e) {
            // Ignore exception
          }
        }
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);

    applySlf4jLogLevel(logLevel);
    applyPicocliLogLevel(logLevel);

    System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
    System.setProperty("polyglotimpl.AttachLibraryFailureAction", "ignore");

    // Prevent log4j JNDI lookups
    // https://nvd.nist.gov/vuln/detail/CVE-2021-44228
    System.setProperty("log4j2.formatMsgNoLookups", "true");

    // Set DriverManager logging
    DriverManager.setLogWriter(new PrintWriter(new DriverManagerLogWriter()));
  }

  /**
   * @param logLevel Log level to be set
   * @see <a href="https://picocli.info/#_tracing">picocli Tracing</a>
   */
  private void applyPicocliLogLevel(final Level logLevel) {

    if (logLevel == null || System.getProperty("picocli.trace") == null) {
      return;
    }

    final String picocliLogLevel;
    final String logLevelName = logLevel.getName();
    switch (logLevelName) {
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
   * @param logLevel Log level to be set
   * @see <a href="https://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html">SLF4J log levels</a>
   */
  private void applySlf4jLogLevel(final Level logLevel) {
    final String slf4jLogLevel;
    switch (logLevel.getName()) {
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
}
