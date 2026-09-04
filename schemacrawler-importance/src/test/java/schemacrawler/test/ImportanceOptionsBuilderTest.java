/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.options.ImportanceOptions;
import schemacrawler.importance.options.ImportanceOptionsBuilder;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;

class ImportanceOptionsBuilderTest {

  @Test
  void defaultsToIncludingAllTables() {
    final ImportanceOptions options = ImportanceOptionsBuilder.builder().toOptions();

    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.BOOK"), is(true));
    assertThat(options.getMaxTables(), is(5));
  }

  @Test
  void handlesMaxTablesConfigurationAndOptions() {
    final Config config = ConfigUtility.newConfig();
    config.put("max-tables", 10);

    final ImportanceOptions optionsFromConfig =
        ImportanceOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(optionsFromConfig.getMaxTables(), is(10));

    final ImportanceOptions optionsFromBuilder =
        ImportanceOptionsBuilder.builder().withMaxTables(0).toOptions();
    assertThat(optionsFromBuilder.getMaxTables(), is(0));

    final ImportanceOptions copied =
        ImportanceOptionsBuilder.builder().fromOptions(optionsFromBuilder).toOptions();
    assertThat(copied.getMaxTables(), is(0));
  }

  @Test
  void acceptsNullConfigurationAndOptions() {
    final ImportanceOptionsBuilder builder = ImportanceOptionsBuilder.builder();

    assertThat(builder.fromConfig(null), is(builder));
    assertThat(builder.fromOptions(null), is(builder));
  }

  @Test
  void readsBareCommandLineTableFilter() {
    final Config config = ConfigUtility.newConfig();
    config.put("table-filter", ".*\\.BOOK$");

    final ImportanceOptions options =
        ImportanceOptionsBuilder.builder().fromConfig(config).toOptions();

    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.BOOK"), is(true));
    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.AUTHOR"), is(false));
  }

  @Test
  void readsPrefixedTableFilter() {
    final Config config = ConfigUtility.newConfig();
    config.put("schemacrawler.importance.table-filter", ".*\\.AUTHOR$");

    final ImportanceOptions options =
        ImportanceOptionsBuilder.builder().fromConfig(config).toOptions();

    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.AUTHOR"), is(true));
    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.BOOK"), is(false));
  }

  @Test
  void prefersBareCommandLineTableFilter() {
    final Config config = ConfigUtility.newConfig();
    config.put("schemacrawler.importance.table-filter", ".*\\.AUTHOR$");
    config.put("table-filter", ".*\\.BOOK$");

    final ImportanceOptions options =
        ImportanceOptionsBuilder.builder().fromConfig(config).toOptions();

    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.BOOK"), is(true));
    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.AUTHOR"), is(false));
  }

  @Test
  void retainsTheExistingRuleForAnEmptyFilter() {
    final Config config = ConfigUtility.newConfig();
    config.put("table-filter", " ");

    final ImportanceOptions options =
        ImportanceOptionsBuilder.builder()
            .withTableNamePattern(".*\\.BOOK$")
            .fromConfig(config)
            .toOptions();

    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.BOOK"), is(true));
    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.AUTHOR"), is(false));
  }

  @Test
  void copiesOptions() {
    final ImportanceOptions original =
        ImportanceOptionsBuilder.builder().withTableNamePattern(".*\\.BOOK$").toOptions();

    final ImportanceOptions copy =
        ImportanceOptionsBuilder.builder().fromOptions(original).toOptions();

    assertThat(copy.getTableInclusionRule(), is(original.getTableInclusionRule()));
  }

  @Test
  void usesTheSuppliedInclusionRule() {
    final InclusionRule rule = tableName -> tableName.startsWith("PUBLIC.AUTHOR");

    final ImportanceOptions options =
        ImportanceOptionsBuilder.builder().withTableInclusionRule(rule).toOptions();

    assertThat(options.getTableInclusionRule(), is(rule));
  }

  @Test
  void replacesANullInclusionRuleWithIncludeAll() {
    final ImportanceOptions options =
        ImportanceOptionsBuilder.builder().withTableInclusionRule(null).toOptions();

    assertThat(options.getTableInclusionRule().test("PUBLIC.BOOKS.BOOK"), is(true));
  }

  @Test
  void createsABuilder() {
    assertThat(ImportanceOptionsBuilder.builder(), is(notNullValue()));
  }
}
