/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

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
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.TestUtility;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;

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
    return EnumSet.complementOf(EnumSet.of(OperationType.tablesample)).stream()
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
        "%d%d.%s_%s.%s"
            .formatted(
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
