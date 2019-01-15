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
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.outputFileOf;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;
import static sf.util.Utility.isBlank;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.lint.executable.LintOptionsBuilder;
import schemacrawler.tools.options.OutputFormat;

public abstract class BaseLintExecutableTest
{

  protected void executeLintCommandLine(final DatabaseConnectionInfo connectionInfo,
                                        final OutputFormat outputFormat,
                                        final String linterConfigsResource,
                                        final Config additionalConfig,
                                        final String referenceFileName)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("url", connectionInfo.getConnectionUrl());
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
    }
    assertThat(fileResource(testout),
               hasSameContentAndTypeAs(classpathResource(referenceFileName
                                                         + ".txt"),
                                       outputFormat.getFormat()));
  }

  protected void executableLint(final Connection connection,
                                final String linterConfigsResource,
                                final Config additionalConfig,
                                final String referenceFileName)
    throws Exception
  {
    final SchemaCrawlerExecutable lintExecutable = new SchemaCrawlerExecutable("lint");
    if (!isBlank(linterConfigsResource))
    {
      final Path linterConfigsFile = copyResourceToTempFile(linterConfigsResource);
      final LintOptionsBuilder optionsBuilder = LintOptionsBuilder.builder();
      optionsBuilder.withLinterConfigs(linterConfigsFile.toString());

      final Config config = optionsBuilder.toConfig();
      if (additionalConfig != null)
      {
        config.putAll(additionalConfig);
      }
      lintExecutable.setAdditionalConfiguration(config);
    }

    assertThat(outputFileOf(executableExecution(connection, lintExecutable)),
               hasSameContentAs(classpathResource(referenceFileName + ".txt")));
  }

}
