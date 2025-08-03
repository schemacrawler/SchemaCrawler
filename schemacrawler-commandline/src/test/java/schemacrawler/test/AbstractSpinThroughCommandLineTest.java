/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.javaVersion;
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
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.options.OutputFormat;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public abstract class AbstractSpinThroughCommandLineTest {

  private static final String SPIN_THROUGH_OUTPUT = "spin_through_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(SPIN_THROUGH_OUTPUT);
  }

  private static String referenceFile(
      final SchemaTextDetailType schemaTextDetailType,
      final InfoLevel infoLevel,
      final OutputFormat outputFormat,
      final String javaVersion) {
    final String referenceFile =
        String.format(
            "%d%d.%s_%s%s.%s",
            schemaTextDetailType.ordinal(),
            infoLevel.ordinal(),
            schemaTextDetailType,
            infoLevel,
            javaVersion,
            outputFormat.getFormat());
    return referenceFile;
  }

  protected static Stream<Arguments> spinThroughArguments() {
    return EnumSet.complementOf(EnumSet.of(InfoLevel.unknown)).stream()
        .flatMap(
            infoLevel ->
                EnumSet.allOf(SchemaTextDetailType.class).stream()
                    .map(schemaTextDetailType -> Arguments.of(infoLevel, schemaTextDetailType)));
  }

  @DisplayName("Spin through command-line for output")
  @ParameterizedTest()
  @MethodSource("spinThroughArguments")
  public void spinThroughMain(
      final InfoLevel infoLevel,
      final SchemaTextDetailType schemaTextDetailType,
      final DatabaseConnectionInfo connectionInfo)
      throws Exception {
    assertAll(
        outputFormats()
            .map(
                outputFormat ->
                    () -> {
                      final String javaVersion;
                      if (schemaTextDetailType == SchemaTextDetailType.details
                          && infoLevel == InfoLevel.maximum) {
                        javaVersion = "." + javaVersion();
                      } else {
                        javaVersion = "";
                      }
                      final String referenceFile =
                          referenceFile(schemaTextDetailType, infoLevel, outputFormat, javaVersion);

                      final String command = schemaTextDetailType.name();

                      final Map<String, String> argsMap = new HashMap<>();
                      argsMap.put("--sequences", ".*");
                      argsMap.put("--synonyms", ".*");
                      argsMap.put("--routines", ".*");
                      argsMap.put("--no-info", Boolean.FALSE.toString());
                      argsMap.put("--info-level", infoLevel.name());

                      assertThat(
                          outputOf(
                              commandlineExecution(
                                  connectionInfo, command, argsMap, true, outputFormat)),
                          hasSameContentAndTypeAs(
                              classpathResource(SPIN_THROUGH_OUTPUT + referenceFile),
                              outputFormat));
                    }));
  }

  protected abstract Stream<? extends OutputFormat> outputFormats();
}
