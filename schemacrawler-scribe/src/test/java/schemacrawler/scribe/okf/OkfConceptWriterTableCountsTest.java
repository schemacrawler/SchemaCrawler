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
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.loader.utility.TableRowCountsUtility;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.scribe.renderer.ScribeSupport;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.state.ExecutionState;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OkfConceptWriterTableCountsTest {

  @Test
  public void rowCountInFrontMatter(
      final DatabaseConnectionSource connectionSource, @TempDir final Path tempDir)
      throws Exception {
    final Config config = ConfigUtility.newConfig();
    config.put("load-row-counts", true);
    final Catalog catalog =
        SchemaCrawlerUtility.getCatalog(
            connectionSource,
            schemaRetrievalOptionsDefault,
            schemaCrawlerOptionsWithMaximumSchemaInfoLevel,
            config);

    final ScribeOptions options =
        ScribeOptionsBuilder.builder().withLocale(Locale.FRENCH).toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    Table target = null;
    for (final Table table : support.allTables()) {
      if (TableRowCountsUtility.hasRowCount(table)) {
        target = table;
        break;
      }
    }
    assertThat(target, is(notNullValue()));

    new OkfConceptPageWriter(support, new BundleDirectoryOutput(tempDir)).writeTableConcept(target);

    final String content =
        Files.readString(tempDir.resolve("tables/" + target.key().slug() + ".md"));
    final long rowCount = support.rowCount(target);
    assertThat(content, containsString("row_count: " + rowCount));
  }
}
