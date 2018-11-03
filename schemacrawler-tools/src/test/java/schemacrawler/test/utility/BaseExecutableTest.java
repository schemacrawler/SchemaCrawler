/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.utility;


import static org.junit.Assert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAndTypeAs;

import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;

public abstract class BaseExecutableTest
  extends BaseDatabaseTest
{

  protected void executeExecutable(final SchemaCrawlerExecutable executable,
                                   final OutputFormat outputFormat,
                                   final String referenceFileName)
    throws Exception
  {
    executeExecutable(executable, outputFormat.getFormat(), referenceFileName);
  }

  protected void executeExecutable(final SchemaCrawlerExecutable executable,
                                   final String referenceFileName)
    throws Exception
  {
    executeExecutable(executable, TextOutputFormat.text, referenceFileName);
  }

  protected void executeExecutable(final SchemaCrawlerExecutable executable,
                                   final String outputFormatValue,
                                   final String referenceFileName)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final OutputOptions outputOptions = OutputOptionsBuilder
        .newOutputOptions(outputFormatValue, out);

      executable.setOutputOptions(outputOptions);
      executable.setConnection(getConnection());
      executable.execute();
    }
    assertThat(fileResource(testout),
               hasSameContentAndTypeAs(classpathResource(referenceFileName),
                                       outputFormatValue));
  }

  protected void executeExecutable(final String command,
                                   final String outputFormatValue,
                                   final String referenceFileName)
    throws Exception
  {
    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executeExecutable(executable, outputFormatValue, referenceFileName);
  }

}
