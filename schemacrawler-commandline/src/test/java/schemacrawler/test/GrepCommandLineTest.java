/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.archunit.thirdparty.com.google.common.collect.ImmutableMap;

import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.utility.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class GrepCommandLineTest {

  private static final String GREP_OUTPUT = "grep_output/";

  @Test
  public void grep(final DatabaseConnectionInfo connectionInfo) throws Exception {
    clean(GREP_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final List<Map<String, String>> grepArgs =
        Arrays.asList(
            ImmutableMap.of("-grep-columns", ".*\\.STREET|.*\\.PRICE"),
            ImmutableMap.of("-grep-columns", ".*\\..*NAME"),
            ImmutableMap.of("-grep-def", ".*book authors.*"),
            ImmutableMap.of("-tables", "", "-routines", ".*", "-grep-parameters", ".*\\.B_COUNT"),
            ImmutableMap.of("-tables", "", "-routines", ".*", "-grep-parameters", ".*\\.B_OFFSET"),
            ImmutableMap.of(
                "-grep-columns", ".*\\.STREET|.*\\.PRICE", "-grep-def", ".*book authors.*"));
    for (int i = 0; i < grepArgs.size(); i++) {

      final String referenceFile = String.format("grep%02d.txt", i + 1);
      final Path testOutputFile = IOUtility.createTempFilePath(referenceFile, "data");

      final Map<String, String> args = new HashMap<>(grepArgs.get(i));
      args.put("-info-level", InfoLevel.detailed.name());
      args.put("-no-info", Boolean.TRUE.toString());

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
