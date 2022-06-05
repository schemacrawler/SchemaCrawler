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

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

import schemacrawler.Main;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestCatalogLoader;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ResolveTestContext
public class CommandLineNegativeTest {

  private static final String COMMAND_LINE_NEGATIVE_OUTPUT = "command_line_negative_output/";

  private TestOutputStream err;
  private TestOutputStream out;

  @AfterEach
  public void cleanUpStreams() {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  @ExpectSystemExitWithStatus(1)
  public void commandLine_BadCommand(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMapOverride = new HashMap<>();
    argsMapOverride.put("--command", "badcommand");

    restoreSystemProperties(
        () -> {
          System.setProperty(TestCatalogLoader.class.getName() + ".force-load-failure", "throw");
          run(testContext, argsMapOverride, connectionInfo);
        });
  }

  @Test
  @ExpectSystemExitWithStatus(1)
  public void mainNoArgs() throws Exception {

    Main.main();
  }

  @BeforeEach
  public void setUpStreams() throws Exception {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

  private void run(
      final TestContext testContext,
      final Map<String, String> argsMapOverride,
      final DatabaseConnectionInfo connectionInfo)
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
    assertThat(outputOf(out), hasNoContent());
    assertThat(
        outputOf(err),
        hasSameContentAs(
            classpathResource(
                COMMAND_LINE_NEGATIVE_OUTPUT + testContext.testMethodName() + ".stderr.txt")));
  }
}
