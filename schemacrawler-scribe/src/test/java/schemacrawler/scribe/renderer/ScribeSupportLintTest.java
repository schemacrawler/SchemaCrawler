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
import java.util.Locale;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.command.options.ScribeOptionsBuilder;
import schemacrawler.test.utility.StubExecutionState;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.state.ExecutionState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class ScribeSupportLintTest {

  @Test
  public void lintDisabled(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().withIncludeLint(false).toOptions();

    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    assertThat(support.isLintEnabled(), is(false));
    assertThat(support.lints().isEmpty(), is(true));
  }

  @Test
  public void lintEnabled(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    final ScribeOptions options = ScribeOptionsBuilder.builder().withIncludeLint(true).toOptions();

    final ExecutionState executionState = new StubExecutionState(catalog);
    final ScribeSupport support = new ScribeSupport(executionState, options, new Lints(List.of()));

    assertThat(support.isLintEnabled(), is(true));
    assertThat(support.lints().size(), is(greaterThanOrEqualTo(0)));

    final Table knownTable = support.allTables().get(0);
    assertThat(support.lintIssues(knownTable), is(not(nullValue())));
  }

  @Test
  public void severityMessagesAreLocalized(final DatabaseConnectionSource connectionSource) {
    final Catalog catalog =
        getCatalog(connectionSource.get(), schemaCrawlerOptionsWithMaximumSchemaInfoLevel);

    final ScribeOptions englishOptions =
        ScribeOptionsBuilder.builder().withLocale(Locale.ENGLISH).toOptions();
    final ScribeOptions frenchOptions =
        ScribeOptionsBuilder.builder().withLocale(Locale.FRENCH).toOptions();

    final ExecutionState englishExecutionState = new StubExecutionState(catalog);
    final ExecutionState frenchExecutionState = new StubExecutionState(catalog);
    final ScribeSupport englishSupport =
        new ScribeSupport(englishExecutionState, englishOptions, new Lints(List.of()));
    final ScribeSupport frenchSupport =
        new ScribeSupport(frenchExecutionState, frenchOptions, new Lints(List.of()));

    assertThat(englishSupport.severityMessage(LintSeverity.medium), is("Severity medium"));
    assertThat(frenchSupport.severityMessage(LintSeverity.medium), is("Sévérité moyenne"));
  }
}
