/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility.extensions;

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

final class AssertNoSystemOutOutputExtension
    implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final String DEFAULT_CHARSET = Charset.defaultCharset().name();

  private ByteArrayOutputStream out;

  @Override
  public void afterTestExecution(final ExtensionContext context) throws Exception {
    System.out.flush();
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, DEFAULT_CHARSET));

    out.close();
    final String output = out.toString();

    out = null;

    assertThat("Expected no System.out output", output, is(emptyString()));
  }

  @Override
  public void beforeTestExecution(final ExtensionContext context) throws Exception {
    if (out != null) {
      fail("STDOUT CORRUPTION");
    }

    out = new ByteArrayOutputStream();

    System.out.flush();
    System.setOut(new PrintStream(out, true, DEFAULT_CHARSET));
  }
}
