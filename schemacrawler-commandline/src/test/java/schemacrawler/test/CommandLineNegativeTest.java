/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

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

@WithTestDatabase
@ResolveTestContext
@CaptureSystemStreams
public class CommandLineNegativeTest {

  private static final String COMMAND_LINE_NEGATIVE_OUTPUT = "command_line_negative_output/";

  @Test
  @ExpectSystemExitWithStatus(1)
  public void commandLine_BadCommand(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {
    final Map<String, String> argsMapOverride = new HashMap<>();
    argsMapOverride.put("--command", "badcommand");

    restoreSystemProperties(
        () -> {
          System.setProperty(TestCatalogLoader.class.getName() + ".force-load-failure", "throw");
          run(testContext, argsMapOverride, connectionInfo, streams);
        });
  }

  @Test
  @ExpectSystemExitWithStatus(1)
  public void mainNoArgs() throws Exception {

    Main.main();
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
