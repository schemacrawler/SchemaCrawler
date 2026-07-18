/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.renderer.JsonUtility;
import schemacrawler.scribe.renderer.ScribeMessages;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OkfConceptWriterTableTest {

  private static Table withNullSchemaName(final Table delegate) {
    final Schema nullNameSchema = new SchemaReference(delegate.getSchema().getCatalogName(), null);
    return (Table)
        Proxy.newProxyInstance(
            Table.class.getClassLoader(),
            new Class<?>[] {Table.class},
            (proxy, method, args) -> {
              if ("getSchema".equals(method.getName())) {
                return nullNameSchema;
              }
              try {
                return method.invoke(delegate, args);
              } catch (final InvocationTargetException e) {
                throw e.getCause();
              }
            });
  }

  @Test
  public void tableConceptWithForeignKeysAndDefinition(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));
    final ScribeMessages msg = support.messages();

    Table target = null;
    for (final Table table : support.allTables()) {
      if (!support.childForeignKeys(table).isEmpty()) {
        target = table;
        break;
      }
    }
    assertThat(target, is(notNullValue()));

    new OkfConceptPageWriter(support, new BundleDirectoryOutput(tempDir, true))
        .writeTableConcept(target);

    final String resourcePath = "tables/" + target.key().slug() + ".md";
    assertThat(Files.exists(tempDir.resolve(resourcePath)), is(true));
    final String content = Files.readString(tempDir.resolve(resourcePath));

    final int frontMatterStart = content.indexOf("---");
    final int frontMatterEnd = content.indexOf("---", frontMatterStart + 3);
    assertThat(frontMatterStart, is(not(-1)));
    assertThat(frontMatterEnd, is(not(-1)));
    final String frontMatter = content.substring(frontMatterStart + 3, frontMatterEnd);
    final JsonNode parsed = JsonUtility.mapper.readTree(frontMatter);
    final JsonNode typeNode = parsed.get("type");
    assertThat(typeNode, is(notNullValue()));
    final String tableType = JsonUtility.mapper.treeToValue(typeNode, String.class);
    assertThat(tableType, anyOf(is("table"), is("view")));

    assertThat(content, containsString("## " + msg.sectionColumns()));
    assertThat(content, containsString("## " + msg.sectionForeignKeys()));
    assertThat(content, containsString("## " + msg.sectionCrossReferences()));
    assertThat(content, containsString("## " + msg.sectionDiagram()));
    assertThat(content, containsString("```mermaid"));

    if (target.hasTriggers()) {
      assertThat(content, containsString("## " + msg.sectionTriggers()));
    } else {
      assertThat(content, not(containsString("## " + msg.sectionTriggers())));
    }

    final Table finalTarget = target;
    if (!support.tableDefinition(finalTarget).isBlank()) {
      assertThat(content, containsString("## " + msg.sectionDefinition()));
    } else {
      assertThat(content, not(containsString("## " + msg.sectionDefinition())));
    }
    assertThat(content, not(containsString("## " + msg.sectionLintIssues())));
  }

  @Test
  public void tableConceptWithNullSchemaName(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    final Table table = support.allTables().get(0);
    final Table tableWithNullSchemaName = withNullSchemaName(table);

    new OkfConceptPageWriter(support, new BundleDirectoryOutput(tempDir, true))
        .writeTableConcept(tableWithNullSchemaName);

    final String resourcePath = "tables/" + tableWithNullSchemaName.key().slug() + ".md";
    final String content = Files.readString(tempDir.resolve(resourcePath));
    assertThat(content, containsString("schema:"));
    assertThat(content, containsString("complete_type:"));
  }
}
