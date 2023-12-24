/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.AssertNoSystemErrOutput;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputFormat;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public class SpinThroughOperationsCommandLineTest {

  private static final String SPIN_THROUGH_OPERATIONS_OUTPUT = "spin_through_operations_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(SPIN_THROUGH_OPERATIONS_OUTPUT);
  }

  private static Stream<InfoLevel> infoLevels() {
    return Arrays.stream(InfoLevel.values()).filter(infoLevel -> infoLevel != InfoLevel.unknown);
  }

  private static Stream<OperationType> operations() {
    return Arrays.stream(OperationType.values());
  }

  private static Stream<TextOutputFormat> outputFormats() {
    return Arrays.stream(new TextOutputFormat[] {TextOutputFormat.text, TextOutputFormat.html});
  }

  private static String referenceFile(
      final OperationType operation, final InfoLevel infoLevel, final OutputFormat outputFormat) {
    final String referenceFile =
        String.format(
            "%d%d.%s_%s.%s",
            operation.ordinal(),
            infoLevel.ordinal(),
            operation,
            infoLevel,
            outputFormat.getFormat());
    return referenceFile;
  }

  @Test
  public void spinThroughMain(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertAll(
        infoLevels()
            .flatMap(
                infoLevel ->
                    outputFormats()
                        .flatMap(
                            outputFormat ->
                                operations()
                                    .map(
                                        operation ->
                                            () -> {

                                              // Special case where no output is generated
                                              if (infoLevel == InfoLevel.minimum
                                                  && operation == OperationType.dump) {
                                                return;
                                              }

                                              final String referenceFile =
                                                  referenceFile(operation, infoLevel, outputFormat);

                                              final String command = operation.name();

                                              final Map<String, String> argsMap = new HashMap<>();
                                              argsMap.put("--sequences", ".*");
                                              argsMap.put("--synonyms", ".*");
                                              argsMap.put("--routines", ".*");
                                              argsMap.put("--no-info", Boolean.FALSE.toString());
                                              argsMap.put("--info-level", infoLevel.name());

                                              assertThat(
                                                  outputOf(
                                                      commandlineExecution(
                                                          connectionInfo,
                                                          command,
                                                          argsMap,
                                                          true,
                                                          outputFormat)),
                                                  hasSameContentAndTypeAs(
                                                      classpathResource(
                                                          SPIN_THROUGH_OPERATIONS_OUTPUT
                                                              + referenceFile),
                                                      outputFormat));
                                            }))));
  }
}
