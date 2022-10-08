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
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.LintTestUtility.executableLint;
import static schemacrawler.test.utility.LintTestUtility.executeLintCommandLine;
import static schemacrawler.tools.lint.config.LinterConfigUtility.readLinterConfigs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

import schemacrawler.test.utility.AssertNoSystemErrOutput;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class LinterConfigsDispatchTest {

  @Test
  @AssertNoSystemErrOutput
  @AssertNoSystemOutOutput
  public void linterConfigs(final TestContext testContext) throws IOException {

    final LintOptions lintOptions =
        LintOptionsBuilder.builder()
            .withLinterConfigs("/schemacrawler-linter-configs-with-dispatch.yaml")
            .toOptions();

    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);

    assertThat(linterConfigs.size(), is(1));
    boolean asserted = false;
    for (final LinterConfig linterConfig : linterConfigs) {
      if (linterConfig == null) {
        fail("Null linter config");
      }
      if (linterConfig
          .getLinterId()
          .equals("schemacrawler.tools.linter.LinterTableWithNoIndexes")) {
        assertThat(linterConfig.getSeverity(), is(LintSeverity.critical));
        assertThat(linterConfig.getThreshold(), is(1));
        assertThat(linterConfig.isRunLinter(), is(true));
        asserted = true;
      }
    }
    if (!asserted) {
      fail();
    }

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(linterConfigs.toString());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  @CaptureSystemStreams
  @ExpectSystemExitWithStatus(1)
  public void testSystemExitLinterConfigCommandLine(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {

    final Map<String, String> additionalArgs = new HashMap<>();
    additionalArgs.put("--lint-dispatch", LintDispatch.terminate_system.name());

    executeLintCommandLine(
        connectionInfo,
        TextOutputFormat.text,
        "/schemacrawler-linter-configs-with-dispatch.yaml",
        additionalArgs,
        "schemacrawler-linter-configs-with-dispatch.txt");

    checkSystemErrLog(testContext, streams);
  }

  @Test
  @CaptureSystemStreams
  @ExpectSystemExitWithStatus(1)
  public void testSystemExitLinterConfigExecutable(
      final TestContext testContext,
      final DatabaseConnectionSource dataSource,
      final CapturedSystemStreams streams)
      throws Exception {

    final Config additionalConfig = new Config();
    additionalConfig.put("lint-dispatch", "terminate_system");

    executableLint(
        dataSource,
        "/schemacrawler-linter-configs-with-dispatch.yaml",
        additionalConfig,
        "schemacrawler-linter-configs-with-dispatch");

    checkSystemErrLog(testContext, streams);
  }

  private void checkSystemErrLog(final TestContext testContext, final CapturedSystemStreams streams)
      throws Exception {
    assertThat(
        outputOf(streams.err()),
        hasSameContentAs(classpathResource(testContext.testMethodName() + ".stderr.txt")));
    assertThat(outputOf(streams.out()), hasNoContent());
  }
}
