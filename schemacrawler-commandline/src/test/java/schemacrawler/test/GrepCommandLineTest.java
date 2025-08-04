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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(GREP_OUTPUT);
  }

  @Test
  public void grep(final DatabaseConnectionInfo connectionInfo) throws Exception {

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
    for (int i = 0; i < grepArgs.size(); i++) {

      final String referenceFile = String.format("grep%02d.txt", i + 1);
      final Path testOutputFile = IOUtility.createTempFilePath(referenceFile, "data");

      final Map<String, String> args =
          grepArgs.get(i).stream()
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
}
