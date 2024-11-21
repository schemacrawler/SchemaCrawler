/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.lint.config.LinterConfigUtility.readLinterConfigs;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintReport;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class LintTest {

  private static final String LINTS_OUTPUT = "lints_output/";

  private static final Config config = new Config();

  @Test
  public void lints(final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .tableTypes("TABLE", "VIEW", "GLOBAL TEMPORARY")
            .includeSchemas(new RegularExpressionInclusionRule(".*FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, config);
    assertThat(catalog, notNullValue());
    assertThat(catalog.getSchemas().size(), is(1));
    final Schema schema = catalog.lookupSchema("PUBLIC.FOR_LINT").orElse(null);
    assertThat("FOR_LINT schema not found", schema, notNullValue());
    assertThat("FOR_LINT tables not found", catalog.getTables(schema), hasSize(7));

    final LinterConfigs linterConfigs = new LinterConfigs(new Config());

    final Map<String, Object> config = new HashMap<>();
    config.put("bad-column-names", ".*\\.COUNTRY");
    final LinterConfig linterConfig =
        new LinterConfig(
            "schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns",
            true,
            LintSeverity.medium,
            0,
            null,
            null,
            null,
            null,
            config);

    linterConfigs.add(linterConfig);

    try (final Connection connection = dataSource.get(); ) {
      final Linters linters = new Linters(linterConfigs, true);
      linters.lint(catalog, connection);
      final LintReport lintReport = linters.getLintReport();
      assertThat(lintReport.size(), is(51));

      final TestWriter testout1 = new TestWriter();
      try (final TestWriter out = testout1) {
        for (final Lint<?> lint : lintReport) {
          out.println(lint);
        }
      }
      assertThat(
          outputOf(testout1),
          hasSameContentAs(classpathResource(LINTS_OUTPUT + "schemacrawler.lints.txt")));

      final TestWriter testout2 = new TestWriter();
      try (final TestWriter out = testout2) {
        out.println(linters.getLintSummary());
      }
      assertThat(
          outputOf(testout2),
          hasSameContentAs(classpathResource(LINTS_OUTPUT + "schemacrawler.lints.summary.txt")));
    }
  }

  @Test
  public void lintsWithExcludedColumns(final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .tableTypes("TABLE", "VIEW", "GLOBAL TEMPORARY")
            .includeSchemas(new RegularExpressionInclusionRule(".*FOR_LINT"))
            .includeColumns(new RegularExpressionExclusionRule(".*\\..*\\..*[123]"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, config);
    assertThat(catalog, notNullValue());
    assertThat(catalog.getSchemas().size(), is(1));
    final Schema schema = catalog.lookupSchema("PUBLIC.FOR_LINT").orElse(null);
    assertThat("FOR_LINT schema not found", schema, notNullValue());
    assertThat("FOR_LINT tables not found", catalog.getTables(schema), hasSize(7));

    try (final Connection connection = dataSource.get(); ) {
      final LinterConfigs linterConfigs = new LinterConfigs(new Config());
      final Linters linters = new Linters(linterConfigs, true);
      linters.lint(catalog, connection);
      final LintReport lintReport = linters.getLintReport();
      assertThat(lintReport.size(), is(40));

      final TestWriter testout = new TestWriter();
      try (final TestWriter out = testout) {
        for (final Lint<?> lint : lintReport) {
          out.println(lint);
        }
      }
      assertThat(
          outputOf(testout),
          hasSameContentAs(
              classpathResource(LINTS_OUTPUT + "schemacrawler.lints.excluded_columns.txt")));
    }
  }

  @Test
  public void runLintersWithConfig(final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(".*FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, config);
    assertThat(catalog, notNullValue());
    assertThat(catalog.getSchemas().size(), is(1));
    final Schema schema = catalog.lookupSchema("PUBLIC.FOR_LINT").orElse(null);
    assertThat("FOR_LINT schema not found", schema, notNullValue());
    assertThat("FOR_LINT tables not found", catalog.getTables(schema), hasSize(6));

    final Config additionalConfig = new Config();
    final String message = UUID.randomUUID().toString();
    additionalConfig.put("message", message);
    additionalConfig.put("sql", "SELECT TOP 1 1 FROM INFORMATION_SCHEMA.TABLES");

    final LintOptions lintOptions =
        LintOptionsBuilder.builder()
            .fromConfig(additionalConfig)
            .withLinterConfigs("/schemacrawler-linter-configs-sql-from-config.yaml")
            .toOptions();

    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);

    final Linters linters = new Linters(linterConfigs, false);

    try (final Connection connection = dataSource.get(); ) {
      linters.lint(catalog, connection);
      final LintReport lintReport = linters.getLintReport();

      assertThat(
          lintReport.stream().findFirst().map(Lint::getMessage).orElse("No value found"),
          startsWith(message));
    }
  }

  @Test
  public void runNoLinters(final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(".*FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, config);
    assertThat(catalog, notNullValue());
    assertThat(catalog.getSchemas().size(), is(1));
    final Schema schema = catalog.lookupSchema("PUBLIC.FOR_LINT").orElse(null);
    assertThat("FOR_LINT schema not found", schema, notNullValue());
    assertThat("FOR_LINT tables not found", catalog.getTables(schema), hasSize(6));

    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    final Linters linters = new Linters(linterConfigs, false);
    assertThat("All linters should be turned off", linters.size(), is(0));

    try (final Connection connection = dataSource.get(); ) {
      linters.lint(catalog, connection);
      final LintReport lintReport = linters.getLintReport();
      assertThat(
          "All linters should be turned off, so there should be no lints",
          lintReport.size(),
          is(0));
    }
  }
}
