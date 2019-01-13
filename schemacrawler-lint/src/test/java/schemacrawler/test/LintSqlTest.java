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


import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.nio.file.Path;
import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.lint.executable.LintOptionsBuilder;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class LintSqlTest
  extends BaseExecutableTest
{

  @Test
  public void executableLintSQLReport(final Connection connection)
    throws Exception
  {
    executeLintExecutable(connection,
                          TextOutputFormat.text,
                          "executableLintSQLReport");
  }

  private void executeLintExecutable(final Connection connection,
                                     final OutputFormat outputFormat,
                                     final String referenceFileName)
    throws Exception
  {
    final SchemaCrawlerExecutable lintExecutable = new SchemaCrawlerExecutable("lint");

    final Path linterConfigsFile = copyResourceToTempFile("/schemacrawler-linter-configs-sql.xml");
    final LintOptionsBuilder optionsBuilder = LintOptionsBuilder.builder();
    optionsBuilder.withLinterConfigs(linterConfigsFile.toString());

    lintExecutable.setAdditionalConfiguration(optionsBuilder.toConfig());

    executeExecutable(connection,
                      lintExecutable,
                      outputFormat,
                      referenceFileName + ".txt");
  }

}
