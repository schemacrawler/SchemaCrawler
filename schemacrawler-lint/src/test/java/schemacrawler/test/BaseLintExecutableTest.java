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
import static sf.util.Utility.isBlank;
import static us.fatehi.commandlineparser.CommandLineUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.Main;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.lint.executable.LintOptionsBuilder;
import schemacrawler.tools.options.OutputFormat;

public abstract class BaseLintExecutableTest
  extends BaseExecutableTest
{

  protected void executeCommandlineAndCheckForOutputFile(final OutputFormat outputFormat,
                                                         final String linterConfigsResource,
                                                         final String referenceFileName)
    throws Exception
  {
    try (final TestWriter out = new TestWriter(outputFormat.getFormat());)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
      argsMap.put("user", "sa");
      argsMap.put("password", "");

      argsMap.put("infolevel", "standard");
      argsMap.put("command", "lint");
      argsMap.put("sortcolumns", "true");
      argsMap.put("outputformat", outputFormat.getFormat());
      argsMap.put("outputfile", out.toString());

      if (!isBlank(linterConfigsResource))
      {
        final Path linterConfigsFile = copyResourceToTempFile(linterConfigsResource);
        argsMap.put("linterconfigs", linterConfigsFile.toString());
      }

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals(referenceFileName + ".txt");
    }
  }

  protected void executeLintExecutable(final OutputFormat outputFormat,
                                       final String linterConfigsResource,
                                       final String referenceFileName)
    throws Exception
  {
    final SchemaCrawlerExecutable lintExecutable = new SchemaCrawlerExecutable("lint");
    if (!isBlank(linterConfigsResource))
    {
      final Path linterConfigsFile = copyResourceToTempFile(linterConfigsResource);
      final LintOptionsBuilder optionsBuilder = new LintOptionsBuilder();
      optionsBuilder.withLinterConfigs(linterConfigsFile.toString());

      lintExecutable.setAdditionalConfiguration(optionsBuilder.toConfig());
    }

    executeExecutable(lintExecutable,
                      outputFormat.getFormat(),
                      referenceFileName + ".txt");
  }

}
