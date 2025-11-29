/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility.extensions;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import us.fatehi.test.utility.TestOutputStream;

final class CaptureSystemStreamsExtension
    implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
  private static final String UTF_8_CHARSET = StandardCharsets.UTF_8.toString();

  private TestOutputStream err;
  private TestOutputStream out;

  @Override
  public void afterTestExecution(final ExtensionContext context) throws Exception {
    System.out.flush();
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, DEFAULT_CHARSET));
    out = null;

    System.err.flush();
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, DEFAULT_CHARSET));
    err = null;
  }

  @Override
  public void beforeTestExecution(final ExtensionContext context) throws Exception {
    if (err != null) {
      fail("STDERR CORRUPTION");
    }
    if (out != null) {
      fail("STDOUT CORRUPTION");
    }

    out = new TestOutputStream();
    err = new TestOutputStream();

    System.out.flush();
    System.setOut(new PrintStream(out, true, UTF_8_CHARSET));

    System.err.flush();
    System.setErr(new PrintStream(err, true, UTF_8_CHARSET));
  }

  @Override
  public Object resolveParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {

    final Parameter parameter = parameterContext.getParameter();
    if (isSystemStreamParameter(parameter)) {
      return new CapturedSystemStreams(out, err);
    } else {
      throw new ParameterResolutionException("Could not resolve " + parameter);
    }
  }

  @Override
  public boolean supportsParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Parameter parameter = parameterContext.getParameter();
    return isSystemStreamParameter(parameter);
  }

  private boolean isSystemStreamParameter(final Parameter parameter) {
    return parameter.getType().equals(CapturedSystemStreams.class);
  }
}
