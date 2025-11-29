/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.LinterHelp;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
public class LinterHelpTest {

  @Test
  public void markdown(final TestContext testContext) throws IOException {

    final String[] strings = new LinterHelp(true).get();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(String.join("\n", strings));
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void text(final TestContext testContext) throws IOException {

    final String[] strings = new LinterHelp().get();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(String.join("\n", strings));
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
