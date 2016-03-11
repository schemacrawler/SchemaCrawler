/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
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
