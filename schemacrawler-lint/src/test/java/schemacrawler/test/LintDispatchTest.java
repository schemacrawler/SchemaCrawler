/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

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
  @ExpectSystemExitWithStatus(1)
  public void lintDispatchTerminateSystem() {
    LintDispatch.terminate_system.dispatch();
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
