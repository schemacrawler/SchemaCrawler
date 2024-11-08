/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.nio.file.Path;

import org.hamcrest.Matcher;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
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

  public static Matcher<TestResource> hasSameContentAndTypeAs(
      final TestResource classpathTestResource, final OutputFormat outputFormat) {
    return hasSameContentAndTypeAs(classpathTestResource, outputFormat.getFormat());
  }

  public static Matcher<TestResource> hasSameContentAndTypeAs(
      final TestResource classpathTestResource, final String outputFormat) {
    return FileHasContent.hasSameContentAndTypeAs(classpathTestResource, outputFormat);
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
