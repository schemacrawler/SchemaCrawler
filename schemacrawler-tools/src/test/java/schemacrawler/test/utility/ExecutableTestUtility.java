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

package schemacrawler.test.utility;


import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAndTypeAs;

import java.nio.file.Path;
import java.sql.Connection;

import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptionsBuilder;

public final class ExecutableTestUtility
{

  public static SchemaCrawlerExecutable createExecutable(final String command)
    throws SchemaCrawlerException
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder()
      .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final SchemaCrawlerExecutable scriptExecutable = new SchemaCrawlerExecutable(command);
    scriptExecutable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    return scriptExecutable;
  }

  public static Path executeExecutable(final Connection connection,
                                       final SchemaCrawlerExecutable executable,
                                       final OutputFormat outputFormat,
                                       final String referenceFileName)
    throws Exception
  {
    return executeExecutable(connection,
                             executable,
                             outputFormat.getFormat(),
                             referenceFileName);
  }

  public static Path executeExecutable(final Connection connection,
                                       final SchemaCrawlerExecutable executable,
                                       final String outputFormatValue,
                                       final String referenceFileName)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final OutputOptionsBuilder outputOptionsBuilder = OutputOptionsBuilder
        .builder().withOutputFormatValue(outputFormatValue)
        .withOutputWriter(out);

      executable.setOutputOptions(outputOptionsBuilder.toOptions());
      executable.setConnection(connection);
      executable.execute();
    }
    assertThat(fileResource(testout),
               hasSameContentAndTypeAs(classpathResource(referenceFileName),
                                       outputFormatValue));
    return testout.getFilePath();
  }

}
