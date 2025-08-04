/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.Main;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.SystemExitException;

@WithTestDatabase
@ResolveTestContext
@CaptureSystemStreams
public class CommandLineSpecialCasesTest {

  private static final String COMMAND_LINE_SPECIAL_CASES_OUTPUT =
      "command_line_special_cases_output/";

  @Test
  public void htmlxWithoutOutputFilename(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--command", "schema");
    argsMap.put("--output-format", "htmlx");
    argsMap.put("--info-level", "standard");

    restoreSystemProperties(
        () -> {
          System.setProperty("SC_EXIT_WITH_EXCEPTION", "true");
          assertThrows(
              SystemExitException.class, () -> run(testContext, argsMap, connectionInfo, streams));
        });
  }

  private void run(
      final TestContext testContext,
      final Map<String, String> argsMapOverride,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {
    final TestWriter outputFile = new TestWriter();
    try (final TestWriter outFile = outputFile) {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--url", connectionInfo.getConnectionUrl());
      argsMap.put("--user", "sa");
      argsMap.put("--password", "");

      argsMap.putAll(argsMapOverride);

      Main.main(flattenCommandlineArgs(argsMap));
    }

    assertThat(outputOf(outputFile), hasNoContent());
    assertThat(outputOf(streams.out()), hasNoContent());
    assertThat(
        outputOf(streams.err()),
        hasSameContentAs(
            classpathResource(
                COMMAND_LINE_SPECIAL_CASES_OUTPUT + testContext.testMethodName() + ".stderr.txt")));
  }
}
