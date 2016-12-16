/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

import org.junit.Test;

import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.lint.executable.LintOptionsBuilder;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

public class LintSqlTest
  extends BaseExecutableTest
{

  @Test
  public void executableLintSQLReport()
    throws Exception
  {
    executeLintExecutable(TextOutputFormat.text, "executableLintSQLReport");
  }

  private void executeLintExecutable(final OutputFormat outputFormat,
                                     final String referenceFileName)
    throws Exception
  {
    final SchemaCrawlerExecutable lintExecutable = new SchemaCrawlerExecutable("lint");

    final Path linterConfigsFile = copyResourceToTempFile("/schemacrawler-linter-configs-sql.xml");
    final LintOptionsBuilder optionsBuilder = new LintOptionsBuilder();
    optionsBuilder.withLinterConfigs(linterConfigsFile.toString());

    lintExecutable.setAdditionalConfiguration(optionsBuilder.toConfig());

    executeExecutable(lintExecutable,
                      outputFormat.getFormat(),
                      referenceFileName + ".txt");
  }

}
