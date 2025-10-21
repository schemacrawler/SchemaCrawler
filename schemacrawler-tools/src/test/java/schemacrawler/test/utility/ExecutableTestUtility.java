/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import java.nio.file.Path;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.FileHasContent;
import us.fatehi.test.utility.extensions.ResultsResource;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public final class ExecutableTestUtility {

  public static Path executableExecution(
      final DatabaseConnectionSource dataSource, final SchemaCrawlerExecutable executable)
      throws Exception {
    return executableExecution(dataSource, executable, "text");
  }

  public static Path executableExecution(
      final DatabaseConnectionSource dataSource,
      final SchemaCrawlerExecutable executable,
      final OutputFormat outputFormat)
      throws Exception {
    return executableExecution(dataSource, executable, outputFormat.getFormat());
  }

  public static Path executableExecution(
      final DatabaseConnectionSource dataSource,
      final SchemaCrawlerExecutable executable,
      final String outputFormatValue)
      throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final OutputOptionsBuilder outputOptionsBuilder =
          OutputOptionsBuilder.builder(executable.getOutputOptions())
              .withOutputFormatValue(outputFormatValue)
              .withOutputWriter(out);

      executable.setOutputOptions(outputOptionsBuilder.toOptions());
      executable.setDataSource(dataSource);
      executable.execute();
    }
    return testout.getFilePath();
  }

  public static SchemaCrawlerExecutable executableOf(final String command) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    return executable;
  }

  public static FileHasContent hasSameContentAndTypeAs(
      final ResultsResource classpathTestResource, final OutputFormat outputFormat) {
    return FileHasContent.hasSameContentAndTypeAs(classpathTestResource, outputFormat.getFormat());
  }

  public static OutputOptions newOutputOptions(
      final String outputFormatValue, final Path outputFile) {
    return OutputOptionsBuilder.builder()
        .withOutputFormatValue(outputFormatValue)
        .withOutputFile(outputFile)
        .toOptions();
  }

  private ExecutableTestUtility() {
    // Prevent instantiation
  }
}
