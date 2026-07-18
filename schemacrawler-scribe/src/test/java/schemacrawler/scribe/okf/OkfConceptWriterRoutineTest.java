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
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.output.ScribeOutputContext;
import schemacrawler.scribe.output.ZipScribeOutputContext;
import schemacrawler.scribe.renderer.ScribeMessages;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OkfConceptWriterRoutineTest {

  private static Routine withNullSchemaName(final Routine delegate) {
    final Schema nullNameSchema = new SchemaReference(delegate.getSchema().getCatalogName(), null);
    return (Routine)
        Proxy.newProxyInstance(
            Routine.class.getClassLoader(),
            new Class<?>[] {Routine.class},
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
  public void routineConcept(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));
    final ScribeMessages msg = support.messages();

    final Routine routine = support.allRoutines().get(0);

    final Path zipFile = tempDir.resolve("okf.zip");
    try (ScribeOutputContext output = new ZipScribeOutputContext(zipFile)) {
      new OkfConceptPageWriter(support, output).writeRoutineConcept(routine);
    }

    final String resourcePath = "routines/" + routine.key().slug() + ".md";
    assertThat(ZipTestUtility.hasEntry(zipFile, resourcePath), is(true));
    final String content = ZipTestUtility.readEntry(zipFile, resourcePath);

    final int frontMatterStart = content.indexOf("---");
    final int frontMatterEnd = content.indexOf("---", frontMatterStart + 3);
    final String frontMatter = content.substring(frontMatterStart + 3, frontMatterEnd);
    assertThat(
        frontMatter,
        anyOf(containsString("type: \"procedure\""), containsString("type: \"function\"")));

    assertThat(content, containsString("## " + msg.sectionParameters()));

    if (!support.routineDefinition(routine).isBlank()) {
      assertThat(content, containsString("## " + msg.sectionDefinition()));
    } else {
      assertThat(content, not(containsString("## " + msg.sectionDefinition())));
    }
  }

  @Test
  public void routineConceptWithNullSchemaName(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    final Routine routine = support.allRoutines().get(0);
    final Routine routineWithNullSchemaName = withNullSchemaName(routine);

    final Path zipFile = tempDir.resolve("okf-null-schema.zip");
    try (ScribeOutputContext output = new ZipScribeOutputContext(zipFile)) {
      new OkfConceptPageWriter(support, output).writeRoutineConcept(routineWithNullSchemaName);
    }

    final String resourcePath = "routines/" + routineWithNullSchemaName.key().slug() + ".md";
    final String content = ZipTestUtility.readEntry(zipFile, resourcePath);
    assertThat(content, containsString("tags:"));
  }
}
