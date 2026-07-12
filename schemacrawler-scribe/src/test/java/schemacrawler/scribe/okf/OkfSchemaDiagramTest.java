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

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;
import schemacrawler.scribe.command.options.SchemaScribeOptions;
import schemacrawler.scribe.command.options.SchemaScribeOptionsBuilder;
import schemacrawler.scribe.output.ScribeOutputContext;
import schemacrawler.scribe.output.ZipScribeOutputContext;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OkfSchemaDiagramTest {

  @Test
  public void schemaWideDiagram(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final SchemaScribeOptions options = SchemaScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    final Path zipFile = tempDir.resolve("okf.zip");
    final String relativePath = "reports/schema.md";
    try (ScribeOutputContext output = new ZipScribeOutputContext(zipFile)) {
      final Map<String, Object> model = new HashMap<>();
      model.put("support", support);
      model.put("msg", support.messages());
      model.put("catalog", executionState.getCatalog());
      model.put("er_model", executionState.getERModel());
      model.put("title", support.databaseTitle());
      new OkfTemplateRenderer(output).writeTemplate("schema-diagram.ftl", model, relativePath);
    }

    final String content = ZipTestUtility.readEntry(zipFile, relativePath);
    assertThat(content.strip(), containsString("erDiagram"));
    assertThat(content, containsString("title: " + support.databaseTitle()));

    final Table knownTable = support.allTablesAndViews().get(0);
    assertThat(content, containsString(knownTable.getName()));

    final ForeignKey knownFk = support.allForeignKeys().stream().findFirst().orElseThrow();
    assertThat(content, containsString(support.mermaidCardinality(knownFk)));
  }
}
