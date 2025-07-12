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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.AssertNoSystemErrOutput;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.lint.LintDispatch;

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
