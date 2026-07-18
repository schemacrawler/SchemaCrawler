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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.model.CrossReferenceEntry;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.test.utility.crawl.LightRoutine;
import schemacrawler.test.utility.crawl.LightRoutineParameter;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.test.utility.crawl.LightTrigger;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

public class OkfTemplateCompilationTest {

  private static Map<String, Object> baseModel(
      final ScribeSupport support, final String resourcePath) {
    final Map<String, Object> model = new HashMap<>();
    model.put("support", support);
    model.put("msg", support.messages());
    model.put("resourcePath", resourcePath);
    model.put("timestamp", "2026-01-01T00:00:00Z");
    return model;
  }

  private static ScribeSupport newScribeSupport(final Catalog catalog) {
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    return new ScribeSupport(executionState, options, new Lints(List.of()));
  }

  @Test
  public void compilesAndRendersCrossReferencesTemplate(@TempDir final Path tempDir)
      throws Exception {
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final LightTable table = new LightTable(schema, "books");
    final Catalog catalog = LightCatalogUtility.lightCatalog(table);
    final ScribeSupport support = newScribeSupport(catalog);

    final Routine routine = new LightRoutine(schema, "find_book");
    final CrossReferenceEntry crossReferenceEntry =
        new CrossReferenceEntry(
            table, SimpleDatabaseObjectType.table, routine, SimpleDatabaseObjectType.procedure);

    final String resourcePath = "reports/cross-references.md";
    final Map<String, Object> model = baseModel(support, resourcePath);
    model.put("crossReferenceEntries", List.of(crossReferenceEntry));

    new OkfTemplateRenderer(new BundleDirectoryOutput(tempDir, false))
        .writeTemplate("cross-references.ftl", model, resourcePath);

    final String content = Files.readString(tempDir.resolve(resourcePath));
    assertThat(content, containsString("# " + support.messages().sectionCrossReferences()));
    assertThat(content, containsString("table"));
    assertThat(content, containsString("procedure"));
    assertThat(content, containsString(support.messages().labelReferencedBy()));
    assertThat(content, containsString("(../tables/books"));
    assertThat(content, containsString("(../routines/find_book"));
    assertThat(Files.exists(tempDir.resolve(resourcePath)), is(true));
  }

  @Test
  public void compilesAndRendersLintTemplate(@TempDir final Path tempDir) throws Exception {
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final LightTable table = new LightTable(schema, "books");
    final Catalog catalog = LightCatalogUtility.lightCatalog(table);
    final ScribeSupport support = newScribeSupport(catalog);

    final String resourcePath = "reports/lint.md";
    final Map<String, Object> model = baseModel(support, resourcePath);
    model.put("lintsBySeverity", Map.of(LintSeverity.medium, List.of()));

    new OkfTemplateRenderer(new BundleDirectoryOutput(tempDir, false))
        .writeTemplate("lint.ftl", model, resourcePath);

    final String content = Files.readString(tempDir.resolve(resourcePath));
    assertThat(content, containsString("# " + support.messages().sectionLintIssues()));
    assertThat(content, containsString("## " + support.severityMessage(LintSeverity.medium)));
    assertThat(Files.exists(tempDir.resolve(resourcePath)), is(true));
  }

  @Test
  public void compilesAndRendersReportsIndexTemplate(@TempDir final Path tempDir) throws Exception {
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final LightTable table = new LightTable(schema, "books");
    final Catalog catalog = LightCatalogUtility.lightCatalog(table);
    final ScribeSupport support = newScribeSupport(catalog);

    final String resourcePath = "reports/index.md";
    final Map<String, Object> model = baseModel(support, resourcePath);

    new OkfTemplateRenderer(new BundleDirectoryOutput(tempDir, false))
        .writeTemplate("reports-index.ftl", model, resourcePath);

    final String content = Files.readString(tempDir.resolve(resourcePath));
    assertThat(content, containsString("# Reports"));
    assertThat(content, containsString("(schema.md)"));
    assertThat(content, containsString("(cross-references.md)"));
    assertThat(Files.exists(tempDir.resolve(resourcePath)), is(true));
  }

  @Test
  public void compilesAndRendersRoutineConceptTemplate(@TempDir final Path tempDir)
      throws Exception {
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final LightTable table = new LightTable(schema, "books");
    final Catalog catalog = LightCatalogUtility.lightCatalog(table);
    final ScribeSupport support = newScribeSupport(catalog);

    final Routine routine = new LightRoutine(schema, "find_book");
    final LightRoutineParameter parameter = new LightRoutineParameter(routine, "book_id");
    ((LightRoutine) routine).addParameter(parameter);

    final String resourcePath = "routines/find_book.md";
    final Map<String, Object> model = baseModel(support, resourcePath);
    model.put("routine", routine);

    new OkfTemplateRenderer(new BundleDirectoryOutput(tempDir, false))
        .writeTemplate("routine-concept.ftl", model, resourcePath);

    final String content = Files.readString(tempDir.resolve(resourcePath));
    assertThat(content, containsString("type:"));
    assertThat(content, containsString("## Parameters"));
    assertThat(content, containsString("book_id"));
    assertThat(content, containsString("## Metadata"));
    assertThat(Files.exists(tempDir.resolve(resourcePath)), is(true));
  }

  @Test
  public void compilesAndRendersTableConceptTemplate(@TempDir final Path tempDir) throws Exception {
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final LightTable table = new LightTable(schema, "books");
    final Column id = table.addDataColumn("id", "INTEGER");
    table.addDataColumn("title", "VARCHAR");
    table.setPrimaryKey(new schemacrawler.test.utility.crawl.LightPrimaryKey(id));
    final LightTrigger trigger = new LightTrigger(table, "TRG_BOOKS");
    trigger.setActionStatement("BEGIN\nUPDATE books SET title = 'new';\nEND");
    table.addTrigger(trigger);

    final Catalog catalog = LightCatalogUtility.lightCatalog(table);
    final ScribeSupport support = newScribeSupport(catalog);

    final String resourcePath = "tables/books.md";
    final Map<String, Object> model = baseModel(support, resourcePath);
    model.put("table", table);

    new OkfTemplateRenderer(new BundleDirectoryOutput(tempDir, false))
        .writeTemplate("table-concept.ftl", model, resourcePath);

    final String content = Files.readString(tempDir.resolve(resourcePath));
    assertThat(content, containsString("type: \"table\""));
    assertThat(content, containsString("## Columns"));
    assertThat(content, containsString("id"));
    assertThat(content, containsString("title"));
    assertThat(content, containsString("## Diagram"));
    assertThat(content, containsString("```mermaid"));
    assertThat(content, containsString("## Triggers"));
    assertThat(content, containsString("### TRG_BOOKS"));
    assertThat(Files.exists(tempDir.resolve(resourcePath)), is(true));
  }
}
