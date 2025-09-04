/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import java.util.Optional;
import java.util.logging.Level;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import us.fatehi.utility.LoggingConfig;

public final class TestDebugLoggingExtension implements BeforeAllCallback {

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {
    final String logLevelString = findValue(context);
    final Level level = Level.parse(logLevelString);
    new LoggingConfig(level);
  }

  private String findValue(final ExtensionContext context) {
    final Optional<TestDebugLogging> logLevelAnnotation =
        findAnnotation(context.getTestClass(), TestDebugLogging.class);
    return logLevelAnnotation.map(TestDebugLogging::value).orElse("CONFIG");
  }
}
