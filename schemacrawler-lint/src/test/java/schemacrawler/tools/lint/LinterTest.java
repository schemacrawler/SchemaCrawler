package schemacrawler.tools.lint;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import java.sql.Connection;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.linter.LinterProviderTableEmpty;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class LinterTest {

  @Test
  public void linterCoverage(final DatabaseConnectionSource dataSource) throws Exception {

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());
    assertThat(catalog, notNullValue());
    assertThat(catalog.getSchemas().size(), is(6));

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
    }
  }
}
