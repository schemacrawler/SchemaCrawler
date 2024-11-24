/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

  protected static Stream<Arguments> spinThroughArguments() {
    return EnumSet.allOf(OperationType.class).stream()
        .flatMap(
            operation ->
                EnumSet.complementOf(EnumSet.of(InfoLevel.unknown)).stream()
                    .flatMap(
                        infoLevel ->
                            EnumSet.of(TextOutputFormat.text, TextOutputFormat.html).stream()
                                .map(
                                    outputFormat ->
                                        Arguments.of(operation, infoLevel, outputFormat))));
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

  @DisplayName("Spin through operations for output")
  @ParameterizedTest()
  @MethodSource("spinThroughArguments")
  public void spinThroughMain(
      final OperationType operation,
      final InfoLevel infoLevel,
      final TextOutputFormat outputFormat,
      final DatabaseConnectionInfo connectionInfo)
      throws Exception {

    // Special case where no output is generated
    if (infoLevel == InfoLevel.minimum && operation == OperationType.dump) {
      return;
    }

    final String command = operation.name();

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--sequences", ".*");
    argsMap.put("--synonyms", ".*");
    argsMap.put("--routines", ".*");
    argsMap.put("--no-info", Boolean.FALSE.toString());
    argsMap.put("--info-level", infoLevel.name());

    final String referenceFile =
        SPIN_THROUGH_OPERATIONS_OUTPUT + referenceFile(operation, infoLevel, outputFormat);
    assertThat(
        outputOf(commandlineExecution(connectionInfo, command, argsMap, true, outputFormat)),
        hasSameContentAndTypeAs(classpathResource(referenceFile), outputFormat));
  }
}
