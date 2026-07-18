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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OkfScribeRendererIntegrationTest {

  @Test
  public void fullRender(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().withIncludeLint(true).toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    new OkfScribeRenderer().render(support, new BundleDirectoryOutput(tempDir, false));

    assertThat(Files.exists(tempDir.resolve("index.md")), is(true));
    assertThat(Files.exists(tempDir.resolve("tables/index.md")), is(true));
    assertThat(Files.exists(tempDir.resolve("routines/index.md")), is(true));
    assertThat(Files.exists(tempDir.resolve("reports/index.md")), is(true));
    assertThat(Files.exists(tempDir.resolve("reports/cross-references.md")), is(true));
    assertThat(Files.exists(tempDir.resolve("reports/schema.md")), is(true));
    assertThat(Files.exists(tempDir.resolve("log.md")), is(false));
    assertThat(Files.exists(tempDir.resolve("reports/lint.md")), is(true));

    for (final Table table : support.allTables()) {
      assertThat(Files.exists(tempDir.resolve("tables/" + table.key().slug() + ".md")), is(true));
    }
    final long routineFileCount;
    try (final var paths = Files.list(tempDir.resolve("routines"))) {
      routineFileCount = paths.filter(Files::isRegularFile).count();
    }
    assertThat(routineFileCount, is((long) support.allRoutines().size() + 1));

    final String rootIndex = Files.readString(tempDir.resolve("index.md"));
    assertThat(rootIndex, containsString(support.messages().labelDatabaseProduct() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelDatabaseVersion() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelTables() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelViews() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelRoutines() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelForeignKeyCount() + ":"));
    if (support.erModelStats() != null) {
      assertThat(rootIndex, containsString("## " + support.messages().sectionErModel()));
      assertThat(rootIndex, containsString(support.messages().labelEntityCount() + ":"));
      assertThat(rootIndex, containsString(support.messages().labelStrongEntityCount() + ":"));
      assertThat(rootIndex, containsString(support.messages().labelWeakEntityCount() + ":"));
      assertThat(rootIndex, containsString(support.messages().labelSubtypeEntityCount() + ":"));
      assertThat(rootIndex, containsString(support.messages().labelNonEntityCount() + ":"));
      assertThat(rootIndex, containsString(support.messages().labelUnknownEntityCount() + ":"));
      assertThat(rootIndex, containsString(support.messages().labelRelationshipCount() + ":"));
      assertThat(
          rootIndex, containsString(support.messages().labelOneToOneRelationshipCount() + ":"));
      assertThat(
          rootIndex, containsString(support.messages().labelOneToManyRelationshipCount() + ":"));
      assertThat(
          rootIndex, containsString(support.messages().labelZeroToOneRelationshipCount() + ":"));
      assertThat(
          rootIndex, containsString(support.messages().labelZeroToManyRelationshipCount() + ":"));
      assertThat(
          rootIndex, containsString(support.messages().labelManyToManyRelationshipCount() + ":"));
      assertThat(
          rootIndex, containsString(support.messages().labelUnknownRelationshipCount() + ":"));
      assertThat(
          rootIndex, containsString(support.messages().labelImplicitRelationshipCount() + ":"));
      assertThat(rootIndex, containsString(support.messages().labelUnmodeledTableCount() + ":"));
    }
    assertThat(rootIndex, containsString("(tables/index.md)"));
    assertThat(rootIndex, containsString("(routines/index.md)"));
    assertThat(rootIndex, containsString("(reports/schema.md)"));
    assertThat(rootIndex, containsString("(reports/lint.md)"));
    assertThat(rootIndex, containsString("(reports/cross-references.md)"));

    final String reportsIndex = Files.readString(tempDir.resolve("reports/index.md"));
    assertThat(reportsIndex, containsString("(schema.md)"));
    assertThat(reportsIndex, containsString("(cross-references.md)"));
    assertThat(reportsIndex, containsString("(lint.md)"));

    if (catalog.getCrawlInfo().getDatabaseVersion() != null) {
      assertThat(
          rootIndex, containsString(catalog.getCrawlInfo().getDatabaseVersion().getProductName()));
      assertThat(
          rootIndex,
          containsString(catalog.getCrawlInfo().getDatabaseVersion().getProductVersion()));
    }

    final String tablesIndex = Files.readString(tempDir.resolve("tables/index.md"));
    if (!support.allTables().isEmpty()) {
      assertThat(
          tablesIndex,
          containsString("## " + support.allTables().get(0).getSchema().getFullName()));
    }

    final String routinesIndex = Files.readString(tempDir.resolve("routines/index.md"));
    if (!support.allRoutines().isEmpty()) {
      assertThat(
          routinesIndex,
          containsString("## " + support.allRoutines().get(0).getSchema().getFullName()));
    }

    boolean sawEmbeddedDiagram = false;
    boolean sawLocalizedEntityModelType = false;
    try (final var paths = Files.walk(tempDir.resolve("tables"))) {
      for (final Path tableFile : (Iterable<Path>) paths.filter(Files::isRegularFile)::iterator) {
        final String tableContent = Files.readString(tableFile);
        if (tableContent.contains("```mermaid")) {
          sawEmbeddedDiagram = true;
        }
        if (tableContent.contains("- " + support.messages().labelEntityModelType() + ": ")) {
          sawLocalizedEntityModelType = true;
        }
      }
    }
    assertThat(sawEmbeddedDiagram, is(true));

    for (final Table table : support.allTables()) {
      final String content =
          Files.readString(tempDir.resolve("tables/" + table.key().slug() + ".md"));
      if (support.localizedEntityModelType(table).isBlank()) {
        assertThat(
            content, not(containsString("- " + support.messages().labelEntityModelType() + ": ")));
      }
    }
    assertThat(sawLocalizedEntityModelType, is(true));
  }
}
