/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;
import static sf.util.Utility.isBlank;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.lint.executable.LintOptionsBuilder;
import schemacrawler.tools.options.OutputFormat;

public abstract class BaseLintExecutableTest
  extends BaseExecutableTest
{

  protected void executeLintCommandLine(final OutputFormat outputFormat,
                                        final String linterConfigsResource,
                                        final Config additionalConfig,
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

      if (additionalConfig != null)
      {
        argsMap.putAll(additionalConfig);
      }

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals(referenceFileName + ".txt");
    }
  }

  protected void executeLintExecutable(final OutputFormat outputFormat,
                                       final String linterConfigsResource,
                                       final Config additionalConfig,
                                       final String referenceFileName)
    throws Exception
  {
    final SchemaCrawlerExecutable lintExecutable = new SchemaCrawlerExecutable("lint");
    if (!isBlank(linterConfigsResource))
    {
      final Path linterConfigsFile = copyResourceToTempFile(linterConfigsResource);
      final LintOptionsBuilder optionsBuilder = new LintOptionsBuilder();
      optionsBuilder.withLinterConfigs(linterConfigsFile.toString());

      final Config config = optionsBuilder.toConfig();
      if (additionalConfig != null)
      {
        config.putAll(additionalConfig);
      }
      lintExecutable.setAdditionalConfiguration(config);
    }

    executeExecutable(lintExecutable,
                      outputFormat.getFormat(),
                      referenceFileName + ".txt");
  }

}
