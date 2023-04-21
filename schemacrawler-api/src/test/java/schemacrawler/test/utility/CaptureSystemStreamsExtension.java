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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

final class CaptureSystemStreamsExtension
    implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final String DEFAULT_CHARSET = Charset.defaultCharset().name();

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
    System.setOut(new PrintStream(out, true, DEFAULT_CHARSET));

    System.err.flush();
    System.setErr(new PrintStream(err, true, DEFAULT_CHARSET));
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
