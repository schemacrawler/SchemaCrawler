/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.lint.LintDispatch;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@DisableLogging
public class LintDispatchTest {

  @Test
  @AssertNoSystemErrOutput
  @AssertNoSystemOutOutput
  public void lintDispatchNone() {
    LintDispatch.none.dispatch();
  }

  @Test
  @AssertNoSystemErrOutput
  @AssertNoSystemOutOutput
  public void lintDispatchThrow() {
    assertThrows(ExecutionRuntimeException.class, () -> LintDispatch.throw_exception.dispatch());
  }

  @Test
  @CaptureSystemStreams
  public void lintDispatchWriteErr(
      final TestContext testContext, final CapturedSystemStreams streams) {
    LintDispatch.write_err.dispatch();

    assertThat(
        outputOf(streams.err()),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".stderr.txt")));
    assertThat(outputOf(streams.out()), hasNoContent());
  }
}
