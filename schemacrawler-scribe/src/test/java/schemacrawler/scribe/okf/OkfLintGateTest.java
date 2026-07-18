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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
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
    render(connectionSource, tempDir, false);

    assertThat(Files.exists(tempDir.resolve("reports/index.md")), is(true));
    assertThat(Files.exists(tempDir.resolve("reports/lint.md")), is(false));
    assertThat(
        Files.readString(tempDir.resolve("index.md")), not(containsString("(reports/lint.md)")));
    final String reportsIndex = Files.readString(tempDir.resolve("reports/index.md"));
    assertThat(reportsIndex, containsString("(schema.md)"));
    assertThat(reportsIndex, containsString("(cross-references.md)"));
    assertThat(reportsIndex, not(containsString("(lint.md)")));
    walkTableFiles(
        tempDir,
        file -> assertThat(Files.readString(file), not(containsString(LINT_ISSUES_HEADING))));
  }

  @Test
  public void lintEnabled(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    render(connectionSource, tempDir, true);

    assertThat(Files.exists(tempDir.resolve("reports/index.md")), is(true));
    assertThat(Files.exists(tempDir.resolve("reports/lint.md")), is(true));
    assertThat(Files.readString(tempDir.resolve("index.md")), containsString("(reports/lint.md)"));
    final String reportsIndex = Files.readString(tempDir.resolve("reports/index.md"));
    assertThat(reportsIndex, containsString("(schema.md)"));
    assertThat(reportsIndex, containsString("(cross-references.md)"));
    assertThat(reportsIndex, containsString("(lint.md)"));
    final String content = Files.readString(tempDir.resolve("reports/lint.md"));
    assertThat(content, containsString("# "));
    walkTableFiles(
        tempDir,
        file -> assertThat(Files.readString(file), not(containsString(LINT_ISSUES_HEADING))));
  }

  private void render(
      final DatabaseConnectionSource connectionSource,
      final Path outputDirectory,
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

    new OkfScribeRenderer().render(support, new BundleDirectoryOutput(outputDirectory, true));
  }

  @FunctionalInterface
  private interface IOConsumer<T> {
    void accept(T t) throws IOException;
  }

  private static void walkTableFiles(final Path root, final IOConsumer<Path> consumer) {
    final Path tablesDir = root.resolve("tables");
    if (!Files.isDirectory(tablesDir)) {
      return;
    }
    try (final var paths = Files.walk(tablesDir)) {
      paths
          .filter(Files::isRegularFile)
          .forEach(
              file -> {
                try {
                  consumer.accept(file);
                } catch (final IOException e) {
                  throw new UncheckedIOException(e);
                }
              });
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
