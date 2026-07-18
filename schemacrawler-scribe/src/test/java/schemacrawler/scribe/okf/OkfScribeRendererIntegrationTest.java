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
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.output.ScribeOutputContext;
import schemacrawler.scribe.output.ScribeOutputContextFactory;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.scribe.renderer.ScribeSupport.EntityModelType;
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

    final Path zipFile = tempDir.resolve("okf.zip");
    try (ScribeOutputContext output = ScribeOutputContextFactory.create(zipFile, false)) {
      new OkfScribeRenderer().render(support, options, output);
    }

    final Set<String> entries = ZipTestUtility.entryNames(zipFile);
    assertThat(entries.contains("index.md"), is(true));
    assertThat(entries.contains("tables/index.md"), is(true));
    assertThat(entries.contains("routines/index.md"), is(true));
    assertThat(entries.contains("reports/index.md"), is(true));
    assertThat(entries.contains("reports/cross-references.md"), is(true));
    assertThat(entries.contains("reports/schema.md"), is(true));
    assertThat(entries.contains("log.md"), is(false));
    assertThat(entries.contains("reports/lint.md"), is(true));

    for (final Table table : support.allTables()) {
      assertThat(entries.contains("tables/" + table.key().slug() + ".md"), is(true));
    }
    assertThat(
        entries.stream().filter(e -> e.startsWith("routines/")).count(),
        is((long) support.allRoutines().size() + 1));

    final String rootIndex = ZipTestUtility.readEntry(zipFile, "index.md");
    assertThat(rootIndex, containsString(support.messages().labelDatabaseProduct() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelDatabaseVersion() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelTables() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelViews() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelRoutines() + ":"));
    assertThat(rootIndex, containsString(support.messages().labelForeignKeyCount() + ":"));
    assertThat(rootIndex, containsString("(tables/index.md)"));
    assertThat(rootIndex, containsString("(routines/index.md)"));
    assertThat(rootIndex, containsString("(reports/schema.md)"));
    assertThat(rootIndex, containsString("(reports/lint.md)"));
    assertThat(rootIndex, containsString("(reports/cross-references.md)"));

    final String reportsIndex = ZipTestUtility.readEntry(zipFile, "reports/index.md");
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

    final String tablesIndex = ZipTestUtility.readEntry(zipFile, "tables/index.md");
    if (!support.allTables().isEmpty()) {
      assertThat(
          tablesIndex,
          containsString("## " + support.allTables().get(0).getSchema().getFullName()));
    }

    final String routinesIndex = ZipTestUtility.readEntry(zipFile, "routines/index.md");
    if (!support.allRoutines().isEmpty()) {
      assertThat(
          routinesIndex,
          containsString("## " + support.allRoutines().get(0).getSchema().getFullName()));
    }

    boolean sawEmbeddedDiagram = false;
    boolean sawLocalizedEntityModelType = false;
    for (final String entry : entries) {
      if (entry.startsWith("tables/")) {
        final String tableContent = ZipTestUtility.readEntry(zipFile, entry);
        if (tableContent.contains("```mermaid")) {
          sawEmbeddedDiagram = true;
        }
        if (tableContent.contains("- " + support.messages().labelEntityModelType() + ": ")) {
          sawLocalizedEntityModelType = true;
        }
      }
    }
    assertThat(sawEmbeddedDiagram, is(true));

    for (final Table bridgeTable : support.bridgeTables()) {
      final String content =
          ZipTestUtility.readEntry(zipFile, "tables/" + bridgeTable.key().slug() + ".md");
      assertThat(content, containsString("bridge_table"));
    }

    for (final Table table : support.allTables()) {
      final String content =
          ZipTestUtility.readEntry(zipFile, "tables/" + table.key().slug() + ".md");
      if (support.entityModelType(table) == EntityModelType.unknown) {
        assertThat(
            content, not(containsString("- " + support.messages().labelEntityModelType() + ": ")));
      }
    }
    assertThat(sawLocalizedEntityModelType, is(true));
  }
}
