/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static us.fatehi.utility.Utility.isBlank;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public final class LintTestUtility {

  public static void executableLint(
      final DatabaseConnectionSource dataSource,
      final String linterConfigsResource,
      final Config additionalConfig,
      final String referenceFileName)
      throws Exception {

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("lint");
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);
    if (!isBlank(linterConfigsResource)) {
      final Path linterConfigsFile = copyResourceToTempFile(linterConfigsResource);
      final LintOptionsBuilder optionsBuilder = LintOptionsBuilder.builder();
      optionsBuilder.withLinterConfigs(linterConfigsFile.toString());

      final Config config = optionsBuilder.toConfig();
      config.merge(additionalConfig);
      executable.setAdditionalConfiguration(config);
    }

    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(referenceFileName + ".txt")));
  }

  public static void executeLintCommandLine(
      final DatabaseConnectionInfo connectionInfo,
      final OutputFormat outputFormat,
      final String linterConfigsResource,
      final Map<String, String> additionalArgs,
      final String referenceFileName)
      throws Exception {
    final Map<String, String> argsMap = new HashMap<>();

    argsMap.put("--info-level", "standard");
    argsMap.put("--sort-columns", "true");

    if (!isBlank(linterConfigsResource)) {
      final Path linterConfigsFile = copyResourceToTempFile(linterConfigsResource);
      argsMap.put("--linter-configs", linterConfigsFile.toString());
    }

    if (additionalArgs != null) {
      argsMap.putAll(additionalArgs);
    }

    assertThat(
        outputOf(commandlineExecution(connectionInfo, "lint", argsMap, null, outputFormat)),
        hasSameContentAndTypeAs(classpathResource(referenceFileName), outputFormat));
  }
}
