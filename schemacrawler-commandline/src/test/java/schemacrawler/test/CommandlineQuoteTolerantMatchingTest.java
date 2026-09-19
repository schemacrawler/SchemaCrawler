/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;

/**
 * Verifies that an unquoted, CLI-supplied regular expression can match a table whose full name is
 * quoted for display (because it contains a space), without also becoming case-insensitive.
 */
@WithTestDatabase
public class CommandlineQuoteTolerantMatchingTest {

  @Test
  public void quotedRegexMatchesQuotedFullName(final DatabaseConnectionInfo connectionInfo)
      throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "minimum");
    argsMap.put("--no-info", Boolean.TRUE.toString());
    argsMap.put("--grep-tables", ".*\\\"Celebrity Updates\\\"$");

    final Path outputFile =
        commandlineExecution(
            connectionInfo, SchemaTextDetailType.list.name(), argsMap, TextOutputFormat.text);

    final String output = Files.readString(outputFile, StandardCharsets.UTF_8);
    assertThat(output, containsString("Celebrity Updates"));
  }

  @Test
  public void unquotedRegexDoesNotMatchWithDifferingCase(
      final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "minimum");
    argsMap.put("--no-info", Boolean.TRUE.toString());
    argsMap.put("--grep-tables", ".*celebrity updates$");

    final Path outputFile =
        commandlineExecution(
            connectionInfo, SchemaTextDetailType.list.name(), argsMap, TextOutputFormat.text);

    final String output = Files.readString(outputFile, StandardCharsets.UTF_8);
    // CLI regex matching stays case-sensitive - quote-tolerance was added without introducing
    // case-insensitivity.
    assertThat(output, not(containsString("Celebrity Updates")));
  }

  @Test
  public void unquotedRegexMatchesQuotedFullName(final DatabaseConnectionInfo connectionInfo)
      throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "minimum");
    argsMap.put("--no-info", Boolean.TRUE.toString());
    argsMap.put("--grep-tables", ".*Celebrity Updates$");

    final Path outputFile =
        commandlineExecution(
            connectionInfo, SchemaTextDetailType.list.name(), argsMap, TextOutputFormat.text);

    final String output = Files.readString(outputFile, StandardCharsets.UTF_8);
    assertThat(output, containsString("Celebrity Updates"));
  }
}
