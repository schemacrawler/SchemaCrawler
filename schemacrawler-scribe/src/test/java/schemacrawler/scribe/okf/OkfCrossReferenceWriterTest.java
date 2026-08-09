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
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
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
public class OkfCrossReferenceWriterTest {

  @Test
  public void crossReferenceIndex(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    new OpenKnowledgeFormatRenderer().render(support, new BundleDirectoryOutput(tempDir, true));

    final String resourcePath = "reports/cross-references.md";
    assertThat(Files.exists(tempDir.resolve(resourcePath)), is(true));
    final String content = Files.readString(tempDir.resolve(resourcePath));

    assertThat(content, containsString("# " + support.messages().sectionCrossReferences()));
    assertThat(content, containsString("| " + support.messages().headerName()));
    assertThat(content, containsString(support.messages().headerType()));

    final Table sourceTable =
        support.allTables().stream()
            .filter(table -> !table.getUsedByObjects().isEmpty())
            .findFirst()
            .orElseThrow();
    assertThat(content, containsString("../tables/" + sourceTable.key().slug() + ".md"));

    final DatabaseObject usingObject =
        sourceTable.getUsedByObjects().stream().findFirst().orElseThrow();
    if (usingObject instanceof Table) {
      assertThat(content, containsString("../tables/" + usingObject.key().slug() + ".md"));
    } else if (usingObject instanceof Routine) {
      assertThat(content, containsString("../routines/" + usingObject.key().slug() + ".md"));
    } else {
      assertThat(content, containsString(support.cleanFullName(usingObject)));
    }
  }
}
