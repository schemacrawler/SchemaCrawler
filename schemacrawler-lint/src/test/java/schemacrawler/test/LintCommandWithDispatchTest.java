/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.LintTestUtility.executeLintCommandLine;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;

@WithTestDatabase
@CaptureSystemStreams
@ResolveTestContext
public class LintCommandWithDispatchTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void commandlineLintReportWithDispatch(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--schemas", ".*FOR_LINT");
    argsMap.put("--lint-dispatch", "write_err");

    executeLintCommandLine(
        connectionInfo,
        TextOutputFormat.text,
        "/schemacrawler-linter-configs-force-dispatch.yaml",
        argsMap,
        testContext.testMethodFullName() + ".txt");

    assertThat(outputOf(streams.out()), hasNoContent());
    assertThat(
        outputOf(streams.err()),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".stderr.txt")));
  }
}
