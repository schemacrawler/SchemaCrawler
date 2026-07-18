/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.output.ScribeOutputContext;
import schemacrawler.scribe.output.ScribeOutputContextFactory;
import schemacrawler.scribe.renderer.ScribeMessages;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OkfLintGateTest {

  private static final String LINT_ISSUES_HEADING =
      "## " + new ScribeMessages(Locale.ENGLISH).sectionLintIssues();

  @Test
  public void lintDisabledByDefault(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Path zipFile = tempDir.resolve("okf.zip");
    render(connectionSource, zipFile, false);

    assertThat(ZipTestUtility.hasEntry(zipFile, "reports/index.md"), is(true));
    assertThat(ZipTestUtility.hasEntry(zipFile, "reports/lint.md"), is(false));
    assertThat(
        ZipTestUtility.readEntry(zipFile, "index.md"), not(containsString("(reports/lint.md)")));
    final String reportsIndex = ZipTestUtility.readEntry(zipFile, "reports/index.md");
    assertThat(reportsIndex, containsString("(schema.md)"));
    assertThat(reportsIndex, containsString("(cross-references.md)"));
    assertThat(reportsIndex, not(containsString("(lint.md)")));
    for (final String entry : ZipTestUtility.entryNames(zipFile)) {
      if (entry.startsWith("tables/")) {
        assertThat(
            ZipTestUtility.readEntry(zipFile, entry), not(containsString(LINT_ISSUES_HEADING)));
      }
    }
  }

  @Test
  public void lintEnabled(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Path zipFile = tempDir.resolve("okf.zip");
    render(connectionSource, zipFile, true);

    assertThat(ZipTestUtility.hasEntry(zipFile, "reports/index.md"), is(true));
    assertThat(ZipTestUtility.hasEntry(zipFile, "reports/lint.md"), is(true));
    assertThat(ZipTestUtility.readEntry(zipFile, "index.md"), containsString("(reports/lint.md)"));
    final String reportsIndex = ZipTestUtility.readEntry(zipFile, "reports/index.md");
    assertThat(reportsIndex, containsString("(schema.md)"));
    assertThat(reportsIndex, containsString("(cross-references.md)"));
    assertThat(reportsIndex, containsString("(lint.md)"));
    final String content = ZipTestUtility.readEntry(zipFile, "reports/lint.md");
    assertThat(content, containsString("# "));
    for (final String entry : ZipTestUtility.entryNames(zipFile)) {
      if (entry.startsWith("tables/")) {
        assertThat(
            ZipTestUtility.readEntry(zipFile, entry), not(containsString(LINT_ISSUES_HEADING)));
      }
    }
  }

  private void render(
      final DatabaseConnectionSource connectionSource,
      final Path zipFile,
      final boolean includeLint)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options =
        ScribeOptionsBuilder.builder()
            .withIncludeLint(includeLint)
            .withLocale(Locale.ENGLISH)
            .toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    try (ScribeOutputContext output = ScribeOutputContextFactory.create(zipFile, false)) {
      new OkfScribeRenderer().render(support, options, output);
    }
  }
}
