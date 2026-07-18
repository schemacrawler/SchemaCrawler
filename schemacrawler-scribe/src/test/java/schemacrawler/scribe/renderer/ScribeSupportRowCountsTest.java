/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;

import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.state.ExecutionState;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class ScribeSupportRowCountsTest {

  @Test
  public void rowCountsAreUnavailableByDefault(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    for (final var table : support.allTables()) {
      assertThat(support.rowCount(table), is(-1L));
    }
  }

  @Test
  public void rowCountsAreLoadedWhenEnabled(final DatabaseConnectionSource connectionSource) {
    final Config config = ConfigUtility.newConfig();
    config.put("load-row-counts", true);
    final Catalog catalog =
        SchemaCrawlerUtility.getCatalog(
            connectionSource,
            schemaRetrievalOptionsDefault,
            schemaCrawlerOptionsWithMaximumSchemaInfoLevel,
            config);
    final ScribeOptions options = ScribeOptionsBuilder.builder().toOptions();
    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    boolean foundAvailableRowCount = false;
    for (final var table : support.allTables()) {
      final long rowCount = support.rowCount(table);
      if (rowCount >= 0) {
        assertThat(rowCount, is(greaterThanOrEqualTo(0L)));
        foundAvailableRowCount = true;
      }
    }
    assertThat(foundAvailableRowCount, is(true));
  }
}
