/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

final class AssertNoSystemErrOutputExtension
    implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final String DEFAULT_CHARSET = Charset.defaultCharset().name();

  private ByteArrayOutputStream err;

  @Override
  public void afterTestExecution(final ExtensionContext context) throws Exception {
    System.err.flush();
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, DEFAULT_CHARSET));

    err.close();
    final String output = err.toString();

    err = null;

    assertThat("Expected no System.err output", output, is(emptyString()));
  }

  @Override
  public void beforeTestExecution(final ExtensionContext context) throws Exception {
    if (err != null) {
      fail("STDERR CORRUPTION");
    }

    err = new ByteArrayOutputStream();

    System.err.flush();
    System.setErr(new PrintStream(err, true, DEFAULT_CHARSET));
  }
}
