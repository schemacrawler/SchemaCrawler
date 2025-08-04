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
import schemacrawler.test.utility.TestCatalogLoader;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.utility.SystemExitException;

@WithTestDatabase
@ResolveTestContext
@CaptureSystemStreams
public class CommandLineNegativeTest {

  private static final String COMMAND_LINE_NEGATIVE_OUTPUT = "command_line_negative_output/";

  @Test
  public void mainBadCommand(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {
    final Map<String, String> argsMapOverride = new HashMap<>();
    argsMapOverride.put("--command", "badcommand");

    restoreSystemProperties(
        () -> {
          System.setProperty("SC_EXIT_WITH_EXCEPTION", "true");
          assertThrows(
              SystemExitException.class,
              () -> run(testContext, argsMapOverride, connectionInfo, streams));
        });
  }

  @Test
  public void mainForceCatalogError(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {

    final Map<String, String> argsMapOverride = new HashMap<>();

    restoreSystemProperties(
        () -> {
          System.setProperty(TestCatalogLoader.class.getName() + ".force-load-failure", "throw");
          System.setProperty("SC_EXIT_WITH_EXCEPTION", "true");
          assertThrows(
              SystemExitException.class,
              () -> run(testContext, argsMapOverride, connectionInfo, streams));
        });
  }

  @Test
  public void mainNoArgs() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty("SC_EXIT_WITH_EXCEPTION", "true");
          assertThrows(SystemExitException.class, () -> Main.main());
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
      argsMap.put("--no-info", Boolean.TRUE.toString());
      argsMap.put("--schemas", ".*\\.(?!FOR_LINT).*");
      argsMap.put("--info-level", "standard");
      argsMap.put("--command", "brief");
      argsMap.put("--tables", "");
      argsMap.put("--routines", "");
      argsMap.put("--output-format", TextOutputFormat.text.getFormat());
      argsMap.put("--output-file", outFile.toString());

      argsMap.putAll(argsMapOverride);

      Main.main(flattenCommandlineArgs(argsMap));
    }

    assertThat(outputOf(outputFile), hasNoContent());
    assertThat(outputOf(streams.out()), hasNoContent());
    assertThat(
        outputOf(streams.err()),
        hasSameContentAs(
            classpathResource(
                COMMAND_LINE_NEGATIVE_OUTPUT + testContext.testMethodName() + ".stderr.txt")));
  }
}
