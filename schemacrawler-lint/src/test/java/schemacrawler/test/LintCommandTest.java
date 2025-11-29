/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static schemacrawler.test.utility.LintTestUtility.executableLint;
import static schemacrawler.test.utility.LintTestUtility.executeLintCommandLine;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
public class LintCommandTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void commandlineLintReport(final DatabaseConnectionInfo connectionInfo) throws Exception {
    executeLintCommandLine(
        connectionInfo, TextOutputFormat.text, null, null, "executableForLint.txt");
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void commandlineLintReportWithConfig(final DatabaseConnectionInfo connectionInfo)
      throws Exception {
    executeLintCommandLine(
        connectionInfo,
        TextOutputFormat.text,
        "/schemacrawler-linter-configs-test.yaml",
        null,
        "executableForLintWithConfig.txt");
  }

  @Test
  public void executableLintReport(final DatabaseConnectionSource dataSource) throws Exception {
    executableLint(dataSource, null, null, "executableForLint");
  }

  @Test
  public void executableLintReportWithConfig(final DatabaseConnectionSource dataSource)
      throws Exception {
    executableLint(
        dataSource, "/schemacrawler-linter-configs-test.yaml", null, "executableForLintWithConfig");
  }
}
