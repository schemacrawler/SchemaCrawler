/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.TestUtility.copyResourceToTempFile;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.Utility.isBlank;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
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
