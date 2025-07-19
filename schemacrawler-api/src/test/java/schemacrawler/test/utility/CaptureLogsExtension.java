/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

final class CaptureLogsExtension
    implements ParameterResolver, BeforeEachCallback, AfterEachCallback {

  private static final Level MAXIMUM_LEVEL = Level.CONFIG;

  private CapturedLogs logs;

  @Override
  public void afterEach(final ExtensionContext context) throws Exception {
    logs.close();
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    logs = new CapturedLogs();
    applyApplicationLogLevel(MAXIMUM_LEVEL);
  }

  @Override
  public Object resolveParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return logs;
  }

  @Override
  public boolean supportsParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Parameter parameter = parameterContext.getParameter();
    return parameter.getType().equals(CapturedLogs.class);
  }

  private void applyApplicationLogLevel(final Level logLevel) {
    requireNonNull(logLevel, "No log level provided");

    final LogManager logManager = LogManager.getLogManager();
    final List<String> loggerNames = Collections.list(logManager.getLoggerNames());
    for (final String loggerName : loggerNames) {
      final Logger logger = logManager.getLogger(loggerName);
      if (logger != null) {
        for (final Handler handler : logger.getHandlers()) {
          logger.removeHandler(handler);
        }
        if (loggerName.startsWith("schemacrawler.") || loggerName.startsWith("us.fatehi.")) {
          logger.setLevel(logLevel);
          logger.addHandler(logs);
        } else {
          logger.setLevel(Level.OFF);
        }
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);
  }
}
