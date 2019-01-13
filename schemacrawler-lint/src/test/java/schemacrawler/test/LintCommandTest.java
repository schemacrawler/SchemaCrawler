/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.test.utility.BaseLintExecutableTest;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.options.TextOutputFormat;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class LintCommandTest
  extends BaseLintExecutableTest
{

  @Test
  public void commandlineLintReport(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeLintCommandLine(connectionInfo,
                           TextOutputFormat.text,
                           null,
                           null,
                           "executableForLint");
  }

  @Test
  public void commandlineLintReportWithConfig(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    executeLintCommandLine(connectionInfo,
                           TextOutputFormat.text,
                           "/schemacrawler-linter-configs-test.xml",
                           null,
                           "executableForLintWithConfig");
  }

  @Test
  public void executableLintReport(final Connection connection)
    throws Exception
  {
    executeLintExecutable(connection,
                          TextOutputFormat.text,
                          null,
                          null,
                          "executableForLint");
  }

  @Test
  public void executableLintReportWithConfig(final Connection connection)
    throws Exception
  {
    executeLintExecutable(connection,
                          TextOutputFormat.text,
                          "/schemacrawler-linter-configs-test.xml",
                          null,
                          "executableForLintWithConfig");
  }

}
