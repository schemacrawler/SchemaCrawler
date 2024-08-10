package schemacrawler.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.GrepOptions;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.test.utility.crawl.LightTable;

class TableGrepFilterTest {

  @Test
  void testTableGrepFilter() {
    final Table table = new LightTable("test_table");

    final GrepOptions grepOptions = GrepOptionsBuilder.builder().toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithInclusionRule() {
    final Table table = new LightTable("test_table");

    final InclusionRule grepTableInclusionRule = new RegularExpressionInclusionRule("test_table");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTableInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithNonMatchingInclusionRule() {
    final Table table = new LightTable("test_table");

    final InclusionRule grepTableInclusionRule =
        new RegularExpressionInclusionRule("non_matching_table");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTableInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(false));
  }

  @Test
  void testTableGrepFilterWithInvertMatch() {
    final Table table = new LightTable("test_table");

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
  void testTableGrepFilterWithColumnInclusionRule() {
    final LightTable table = new LightTable("test_table");
    table.addColumn("test_column");

    final InclusionRule grepColumnInclusionRule =
        new RegularExpressionInclusionRule("test_table\\.test_column");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder().includeGreppedColumns(grepColumnInclusionRule).toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithDefinitionInclusionRule() {
    final LightTable table = new LightTable("test_table");
    table.setDefinition("test_definition");

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
  void testTableGrepFilterWithNonMatchingDefinitionInclusionRule() {
    final LightTable table = new LightTable("test_table");
    table.setDefinition("test_definition");

    final InclusionRule grepDefinitionInclusionRule =
        new RegularExpressionInclusionRule("non_matching_definition");
    final GrepOptions grepOptions =
        GrepOptionsBuilder.builder()
            .includeGreppedDefinitions(grepDefinitionInclusionRule)
            .toOptions();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    assertThat(tableGrepFilter.test(table), is(false));
  }
}
