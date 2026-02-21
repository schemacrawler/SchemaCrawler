/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.tools.lint.LintUtility.LINTER_COMPARATOR;
import static schemacrawler.tools.lint.LintUtility.LINT_COMPARATOR;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.LinterProviderForTest;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.linter.LinterProviderCatalogSql;
import schemacrawler.tools.linter.LinterProviderTableEmpty;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.property.ProductVersion;

@TestInstance(Lifecycle.PER_CLASS)
@WithTestDatabase
public class LinterTest {

  private Catalog catalog;

  @BeforeAll
  public void createCatalog(final DatabaseConnectionSource dataSource) {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    catalog =
        getCatalog(
            dataSource,
            schemaRetrievalOptionsDefault,
            schemaCrawlerOptions,
            ConfigUtility.newConfig());
    assertThat(catalog, notNullValue());
    assertThat(catalog.getSchemas().size(), is(6));
  }

  @Test
  public void linterComparator(final DatabaseConnectionSource dataSource) {

    final LintCollector collector = new LintCollector();
    final Linter linter1 = new LinterProviderTableEmpty().newLinter(collector);
    final Linter linter2 = new LinterProviderCatalogSql().newLinter(collector);

    assertThat(LINTER_COMPARATOR.compare(null, null), is(0));
    assertThat(LINTER_COMPARATOR.compare(linter1, null), is(1));
    assertThat(LINTER_COMPARATOR.compare(null, linter1), is(-1));
    assertThat(LINTER_COMPARATOR.compare(linter1, linter1), is(0));
    assertThat(LINTER_COMPARATOR.compare(linter1, linter2), is(lessThan(0)));
  }

  @Test
  public void linterCoverage(final DatabaseConnectionSource dataSource) throws Exception {

    try (final Connection connection = dataSource.get(); ) {
      final LintCollector collector = new LintCollector();
      final Linter linter = new LinterProviderTableEmpty().newLinter(collector);
      linter.initialize();
      linter.setCatalog(catalog);
      if (linter.usesConnection()) {
        linter.setConnection(connection);
      }
      linter.execute();

      assertThat(linter.getDescription(), is("Checks for empty tables with no data."));
      assertThat(linter.getLintCount(), is(10));
      assertThat(linter.getLinterId(), is(linter.getClass().getCanonicalName()));
      assertThat(linter.getLinterInstanceId(), is(not(emptyString())));
      assertThat(linter.getSeverity(), is(LintSeverity.low));
      assertThat(linter.getSummary(), is("empty table"));

      assertThat(linter.exceedsThreshold(), is(false));
      assertThat(linter.toString(), containsString("empty table"));

      assertThat(collector.getLints().size(), is(10));

      // Get columns
      assertThat(((BaseLinter) linter).getColumns(null), is(empty()));
      // Add catalog lint, but it will throw since the catalog is cleared after linting
      assertThrows(
          NullPointerException.class,
          () -> ((BaseLinter) linter).addCatalogLint("Test catalog lint"));
    }
  }

  @Test
  public void linterForCrawlInfo(final DatabaseConnectionSource dataSource) throws Exception {

    try (final Connection connection = dataSource.get(); ) {
      final LintCollector collector = new LintCollector();
      final Linter linter = new LinterProviderForTest().newLinter(collector);
      linter.initialize();
      linter.setCatalog(catalog);
      if (linter.usesConnection()) {
        linter.setConnection(connection);
      }
      linter.execute();

      final List<Lint<? extends Serializable>> lints = new ArrayList<>(collector.getLints());
      lints.sort(LINT_COMPARATOR);

      assertThat(lints.size(), is(2));

      final Serializable value = lints.get(0).getValue();

      assertThat(value, is(instanceOf(ProductVersion.class)));
    }
  }

  @Test
  public void noOpLinter(final DatabaseConnectionSource dataSource) throws Exception {

    try (final Connection connection = dataSource.get(); ) {
      final LintCollector collector = new LintCollector();
      final Linter linter =
          LinterRegistry.getLinterRegistry().newLinter("bad-linter-id", collector);

      assertThat(linter.getSummary(), is("No-op linter"));

      linter.initialize();
      linter.setCatalog(catalog);
      if (linter.usesConnection()) {
        linter.setConnection(connection);
      }
      linter.execute();

      final List<Lint<? extends Serializable>> lints = new ArrayList<>(collector.getLints());
      assertThat(lints.size(), is(0));
    }
  }
}
