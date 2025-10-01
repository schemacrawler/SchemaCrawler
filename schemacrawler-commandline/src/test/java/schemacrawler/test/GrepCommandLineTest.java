/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.utility.IOUtility;

@WithTestDatabase
public class GrepCommandLineTest {

  private static final String GREP_OUTPUT = "grep_output/";

  public static Stream<Arguments> _grepTestArguments() {
    final List<List<Map.Entry<String, String>>> grepArgs =
        Arrays.asList(
            Arrays.asList(
                new AbstractMap.SimpleEntry<>("--grep-columns", ".*\\.STREET|.*\\.PRICE")),
            Arrays.asList(new AbstractMap.SimpleEntry<>("--grep-columns", ".*\\..*NAME")),
            Arrays.asList(new AbstractMap.SimpleEntry<>("--grep-def", ".*book authors.*")),
            Arrays.asList(
                new AbstractMap.SimpleEntry<>("--tables", ""),
                new AbstractMap.SimpleEntry<>("--routines", ".*"),
                new AbstractMap.SimpleEntry<>("--grep-parameters", ".*\\.B_COUNT")),
            Arrays.asList(
                new AbstractMap.SimpleEntry<>("--tables", ""),
                new AbstractMap.SimpleEntry<>("--routines", ".*"),
                new AbstractMap.SimpleEntry<>("--grep-parameters", ".*\\.B_OFFSET")),
            Arrays.asList(
                new AbstractMap.SimpleEntry<>("--grep-columns", ".*\\.STREET|.*\\.PRICE"),
                new AbstractMap.SimpleEntry<>("--grep-def", ".*book authors.*")),
            Arrays.asList(new AbstractMap.SimpleEntry<>("--grep-tables", ".*\\.BOOKS")));

    return IntStream.range(0, grepArgs.size()).mapToObj(i -> Arguments.of(i, grepArgs.get(i)));
  }

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(GREP_OUTPUT);
  }

  @ParameterizedTest
  @MethodSource("_grepTestArguments")
  public void grep(
      final int testCaseCounter,
      final List<Map.Entry<String, String>> grepArguments,
      final DatabaseConnectionInfo connectionInfo)
      throws Exception {

    final String referenceFile = String.format("grep%02d.txt", testCaseCounter + 1);
    final Path testOutputFile = IOUtility.createTempFilePath(referenceFile, "data");

    final Map<String, String> args =
        grepArguments.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    args.put("--info-level", InfoLevel.detailed.name());
    args.put("--no-info", Boolean.TRUE.toString());

    commandlineExecution(
        connectionInfo,
        SchemaTextDetailType.details.name(),
        args,
        DatabaseTestUtility.tempHsqldbConfig(),
        TextOutputFormat.text.getFormat(),
        testOutputFile);

    final String expectedResource = GREP_OUTPUT + referenceFile;
    assertThat(outputOf(testOutputFile), hasSameContentAs(classpathResource(expectedResource)));
  }
}
