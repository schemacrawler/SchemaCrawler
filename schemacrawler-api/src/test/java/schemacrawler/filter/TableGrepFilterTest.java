/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.GrepOptions;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.test.utility.crawl.LightTrigger;

class TableGrepFilterTest {

  private Table table;

  @BeforeEach
  public void setUp() {
    final LightTable table = new LightTable("test_table");
    table.addColumn("test_column");
    table.setDefinition("test_definition");
    table.setRemarks("test_remarks");

    final LightTrigger trigger = new LightTrigger(table, "test_trigger");
    trigger.setActionStatement("test_action_statement");
    table.addTrigger(trigger);

    this.table = table;
  }

  @Test
  void testTableGrepFilter() {
    final GrepOptions grepOptions = GrepOptionsBuilder.builder().toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithColumnInclusionRule() {
    final InclusionRule grepColumnInclusionRule =
        new RegularExpressionInclusionRule("test_table\\.test_column");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedColumns(grepColumnInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithColumnInclusionRuleWithNoColumns() {
    table = new LightTable("test_table");

    final InclusionRule grepColumnInclusionRule =
        new RegularExpressionInclusionRule("test_table\\.test_column");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedColumns(grepColumnInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithDefinitionInclusionRule() {
    final InclusionRule grepDefinitionInclusionRule =
        new RegularExpressionInclusionRule("test_definition");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder()
            .includeGreppedDefinitions(grepDefinitionInclusionRule)
            .toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithInclusionRule() {
    final InclusionRule grepTableInclusionRule = new RegularExpressionInclusionRule("test_table");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTableInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithInvertMatch() {
    final InclusionRule grepTableInclusionRule = new RegularExpressionInclusionRule("test_table");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder()
            .includeGreppedTables(grepTableInclusionRule)
            .invertGrepMatch(true)
            .toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(false));
  }

  @Test
  void testTableGrepFilterWithInvertMatchForNoMatch() {
    final InclusionRule grepTableInclusionRule = new RegularExpressionInclusionRule("test_table_1");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder()
            .includeGreppedTables(grepTableInclusionRule)
            .invertGrepMatch(true)
            .toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithJustInvertMatch() {
    final GrepOptions grepOptions = GrepOptionsBuilder.builder().invertGrepMatch(true).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithNonMatchingColumnInclusionRule() {
    final InclusionRule grepColumnInclusionRule =
        new RegularExpressionInclusionRule("test_table\\.test_column_1");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedColumns(grepColumnInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(false));
  }

  @Test
  void testTableGrepFilterWithNonMatchingDefinitionInclusionRule() {
    final InclusionRule grepDefinitionInclusionRule =
        new RegularExpressionInclusionRule("non_matching_definition");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder()
            .includeGreppedDefinitions(grepDefinitionInclusionRule)
            .toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(false));
  }

  @Test
  void testTableGrepFilterWithNonMatchingInclusionRule() {
    final InclusionRule grepTableInclusionRule =
        new RegularExpressionInclusionRule("non_matching_table");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTableInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(false));
  }

  @Test
  void testTableGrepFilterWithoutColumnInclusionRule() {
    final InclusionRule grepColumnInclusionRule =
        new RegularExpressionInclusionRule("test_table\\.test_column_1");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedColumns(grepColumnInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(false));
  }

  @Test
  void testTableGrepFilterWithRemarksInclusionRule() {
    final InclusionRule grepDefinitionInclusionRule =
        new RegularExpressionInclusionRule("test_remarks");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder()
            .includeGreppedDefinitions(grepDefinitionInclusionRule)
            .toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithTriggerActionItemInclusionRule() {
    final InclusionRule grepDefinitionInclusionRule =
        new RegularExpressionInclusionRule("test_action_statement");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder()
            .includeGreppedDefinitions(grepDefinitionInclusionRule)
            .toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }
}
