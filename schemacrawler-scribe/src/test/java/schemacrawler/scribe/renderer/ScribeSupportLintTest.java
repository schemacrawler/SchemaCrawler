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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.scribe.command.options.SchemaScribeOptions;
import schemacrawler.scribe.command.options.SchemaScribeOptionsBuilder;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class ScribeSupportLintTest {

  @Test
  public void lintDisabled(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final SchemaScribeOptions options =
        SchemaScribeOptionsBuilder.builder().withIncludeLint(false).toOptions();

    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    assertThat(support.isLintEnabled(), is(false));
    assertThat(support.lints().isEmpty(), is(true));
  }

  @Test
  public void lintEnabled(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final SchemaScribeOptions options =
        SchemaScribeOptionsBuilder.builder().withIncludeLint(true).toOptions();

    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    assertThat(support.isLintEnabled(), is(true));
    assertThat(support.lints().size(), is(greaterThanOrEqualTo(0)));

    final Table knownTable = support.allTablesAndViews().get(0);
    assertThat(support.lintIssues(knownTable), is(not(nullValue())));
  }
}
