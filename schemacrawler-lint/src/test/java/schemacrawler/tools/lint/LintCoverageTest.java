/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.tools.lint.LintUtility.LINT_COMPARATOR;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import java.io.Serializable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.linter.LinterProviderCatalogSql;
import schemacrawler.tools.linter.LinterProviderTableEmpty;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@TestInstance(Lifecycle.PER_CLASS)
@WithTestDatabase
public class LintCoverageTest {

  private Catalog catalog;

  @BeforeAll
  public void createCatalog(final DatabaseConnectionSource dataSource) {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());
    assertThat(catalog, notNullValue());
    assertThat(catalog.getSchemas().size(), is(6));
  }

  @Test
  public void lintComparator() {

    final LintCollector collector = new LintCollector();
    final Linter linter1 = new LinterProviderTableEmpty().newLinter(collector);
    new LinterProviderCatalogSql().newLinter(collector);

    final Lint<? extends Serializable> lint1 =
        new Lint<Serializable>(
            linter1.getLinterId(),
            linter1.getLinterInstanceId(),
            LintObjectType.table,
            catalog,
            LintSeverity.high,
            linter1.getSummary(),
            catalog);
    final Lint<? extends Serializable> lint2 =
        new Lint<Serializable>(
            linter1.getLinterId(),
            linter1.getLinterInstanceId(),
            LintObjectType.table,
            catalog,
            LintSeverity.medium,
            linter1.getSummary(),
            catalog);

    assertThat(LINT_COMPARATOR.compare(null, null), is(0));
    assertThat(LINT_COMPARATOR.compare(lint1, null), is(1));
    assertThat(LINT_COMPARATOR.compare(null, lint1), is(-1));
    assertThat(LINT_COMPARATOR.compare(lint1, lint1), is(0));
    assertThat(LINT_COMPARATOR.compare(lint1, lint2), is(lessThan(0)));
  }

  @Test
  public void lintConstructorNullSeverity() {

    final LintCollector collector = new LintCollector();
    final Linter linter1 = new LinterProviderTableEmpty().newLinter(collector);
    new LinterProviderCatalogSql().newLinter(collector);

    final Lint<? extends Serializable> lint1 =
        new Lint<Serializable>(
            linter1.getLinterId(),
            linter1.getLinterInstanceId(),
            LintObjectType.table,
            catalog,
            null,
            linter1.getSummary(),
            catalog);

    assertThat(lint1.getSeverity(), is(LintSeverity.critical));
  }

  @Test
  public void lintConstructorNullMessage() {

    final LintCollector collector = new LintCollector();
    final Linter linter1 = new LinterProviderTableEmpty().newLinter(collector);
    new LinterProviderCatalogSql().newLinter(collector);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Lint<Serializable>(
                linter1.getLinterId(),
                linter1.getLinterInstanceId(),
                LintObjectType.table,
                catalog,
                LintSeverity.high,
                null,
                catalog));
  }
}
