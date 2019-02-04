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
package schemacrawler.test.sitegen;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.test.utility.*;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.move;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;

@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestAssertNoSystemOutOutput.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class SiteLintReportVariationsTest
{

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
      throws IOException, URISyntaxException
  {
    if (directory != null)
    {
      return;
    }

    directory = testContext.resolveTargetFromRootPath("lint-report-examples");
  }

  @Test
  public void lint_reports(final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    for (final OutputFormat outputFormat : new OutputFormat[] {
        TextOutputFormat.html, TextOutputFormat.json, TextOutputFormat.text, })
    {
      final String extension;
      if (outputFormat == TextOutputFormat.text)
      {
        extension = "txt";
      }
      else
      {
        extension = outputFormat.getFormat();
      }
      final Map<String, String> args = new HashMap<>();
      args.put("infolevel", "maximum");

      run(connectionInfo,
          args,
          outputFormat,
          directory.resolve("lint_report." + extension));
    }
  }

  private void run(final DatabaseConnectionInfo connectionInfo,
                   final Map<String, String> argsMap,
                   final OutputFormat outputFormat,
                   final Path outputFile)
      throws Exception
  {
    deleteIfExists(outputFile);

    argsMap.put("title", "Lint Report of Example Database");

    final Path lintReportFile = commandlineExecution(connectionInfo,
                                                     "lint",
                                                     argsMap,
                                                     outputFormat);
    move(lintReportFile, outputFile);
  }

}
