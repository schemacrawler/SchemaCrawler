package schemacrawler.tools.lint;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.tools.lint.LintUtility.LINTER_COMPARATOR;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import java.sql.Connection;
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
public class LinterTest {

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
      linter.lint(catalog, connection);

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
}
