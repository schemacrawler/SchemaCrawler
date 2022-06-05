/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.test.utility.LintTestUtility.executableLint;
import static schemacrawler.test.utility.LintTestUtility.executeLintCommandLine;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.test.utility.AssertNoSystemErrOutputExtension;
import schemacrawler.test.utility.AssertNoSystemOutOutputExtension;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;

@WithTestDatabase
@ExtendWith(AssertNoSystemErrOutputExtension.class)
@ExtendWith(AssertNoSystemOutOutputExtension.class)
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
  public void executableLintReport(final Connection connection) throws Exception {
    executableLint(connection, null, null, "executableForLint");
  }

  @Test
  public void executableLintReportWithConfig(final Connection connection) throws Exception {
    executableLint(
        connection, "/schemacrawler-linter-configs-test.yaml", null, "executableForLintWithConfig");
  }
}
