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

import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
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

    final List<String> failures = new ArrayList<>();

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

      failures.addAll(
          compareOutput(
              GREP_OUTPUT + referenceFile, testOutputFile, TextOutputFormat.text.getFormat()));
    }

    if (failures.size() > 0) {
      fail(failures.toString());
    }
  }
}
