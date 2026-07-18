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
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
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
public class OkfLocalizationTest {

  @Test
  public void frenchLocale(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    assertLocalizedHeading(connectionSource, tempDir, Locale.FRENCH);
  }

  @Test
  public void germanLocale(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    assertLocalizedHeading(connectionSource, tempDir, Locale.GERMAN);
  }

  @Test
  public void spanishLocale(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    assertLocalizedHeading(connectionSource, tempDir, Locale.forLanguageTag("es"));
  }

  @Test
  public void portugueseLocale(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    assertLocalizedHeading(connectionSource, tempDir, Locale.forLanguageTag("pt"));
  }

  private void assertLocalizedHeading(
      final DatabaseConnectionSource connectionSource, final Path tempDir, final Locale locale)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().withLocale(locale).toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));
    final ScribeMessages msg = support.messages();

    Table table = support.allTables().get(0);
    for (final Table candidate : support.allTables()) {
      if (candidate.hasTriggers()) {
        table = candidate;
        break;
      }
    }
    new OkfConceptPageWriter(support, new BundleDirectoryOutput(tempDir, false))
        .writeTableConcept(table);

    final String content =
        Files.readString(tempDir.resolve("tables/" + table.key().slug() + ".md"));

    assertThat(content, containsString("## " + msg.sectionColumns()));
    if (table.hasTriggers()) {
      assertThat(
          content, containsString("| " + msg.headerAttribute() + " | " + msg.headerValue() + " |"));
      assertThat(content, containsString("| " + msg.triggerAttributeTiming() + " |"));
    }
    assertThat(content, containsString("type: \"table\""));
  }
}
