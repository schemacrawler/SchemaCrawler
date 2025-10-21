/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.LintTestUtility.executeLintCommandLine;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.TestUtility;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
public class LintOutputTest {

  private static final String TEXT_OUTPUT = "lint_text_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(TEXT_OUTPUT);
  }

  @ParameterizedTest
  @EnumSource(LintReportOutputFormat.class)
  public void commandlineLintReportOutput(
      final OutputFormat outputFormat, final DatabaseConnectionInfo connectionInfo)
      throws Exception {

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--schemas", ".*FOR_LINT");

    final String referenceFile = TEXT_OUTPUT + "lint." + outputFormat.getFormat();
    executeLintCommandLine(connectionInfo, outputFormat, null, argsMap, referenceFile);
  }

  @ParameterizedTest
  @EnumSource(LintReportOutputFormat.class)
  public void executableLintReportOutput(
      final OutputFormat outputFormat, final DatabaseConnectionSource dataSource) throws Exception {

    final InfoLevel infoLevel = InfoLevel.standard;

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(".*FOR_LINT"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("lint");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    final String referenceFile = TEXT_OUTPUT + "lint." + outputFormat.getFormat();
    assertThat(
        outputOf(executableExecution(dataSource, executable, outputFormat)),
        hasSameContentAndTypeAs(classpathResource(referenceFile), outputFormat));
  }

  @Test
  public void lintReportOutputFormat() {
    assertThat(LintReportOutputFormat.isSupportedFormat(null), is(false));
    assertThat(LintReportOutputFormat.fromFormat(null), is(LintReportOutputFormat.text));
    assertThat(LintReportOutputFormat.fromFormat("badformat"), is(LintReportOutputFormat.text));

    assertThat(LintReportOutputFormat.text.getDescription(), is("Plain text format"));
    assertThat(LintReportOutputFormat.text.toString(), is("[txt, text] Plain text format"));
    assertThat(LintReportOutputFormat.text.getFormats(), contains("txt", "text"));
  }
}
