package schemacrawler.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.GrepOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;

class TableGrepFilterTest {

  @Test
  void testTableGrepFilter() {
    final GrepOptions grepOptions = GrepOptions.builder().build();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    final Table table = new Table() {
      @Override
      public String getFullName() {
        return "test_table";
      }

      @Override
      public String getRemarks() {
        return "test_remarks";
      }

      @Override
      public String getDefinition() {
        return "test_definition";
      }

      @Override
      public Iterable<Column> getColumns() {
        return Collections.emptyList();
      }

      @Override
      public Iterable<Trigger> getTriggers() {
        return Collections.emptyList();
      }
    };

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithInclusionRule() {
    final InclusionRule grepTableInclusionRule = new RegularExpressionInclusionRule("test_table");
    final GrepOptions grepOptions = GrepOptions.builder().withGrepTableInclusionRule(grepTableInclusionRule).build();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    final Table table = new Table() {
      @Override
      public String getFullName() {
        return "test_table";
      }

      @Override
      public String getRemarks() {
        return "test_remarks";
      }

      @Override
      public String getDefinition() {
        return "test_definition";
      }

      @Override
      public Iterable<Column> getColumns() {
        return Collections.emptyList();
      }

      @Override
      public Iterable<Trigger> getTriggers() {
        return Collections.emptyList();
      }
    };

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithNonMatchingInclusionRule() {
    final InclusionRule grepTableInclusionRule = new RegularExpressionInclusionRule("non_matching_table");
    final GrepOptions grepOptions = GrepOptions.builder().withGrepTableInclusionRule(grepTableInclusionRule).build();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    final Table table = new Table() {
      @Override
      public String getFullName() {
        return "test_table";
      }

      @Override
      public String getRemarks() {
        return "test_remarks";
      }

      @Override
      public String getDefinition() {
        return "test_definition";
      }

      @Override
      public Iterable<Column> getColumns() {
        return Collections.emptyList();
      }

      @Override
      public Iterable<Trigger> getTriggers() {
        return Collections.emptyList();
      }
    };

    assertThat(tableGrepFilter.test(table), is(false));
  }

  @Test
  void testTableGrepFilterWithInvertMatch() {
    final GrepOptions grepOptions = GrepOptions.builder().withGrepInvertMatch(true).build();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    final Table table = new Table() {
      @Override
      public String getFullName() {
        return "test_table";
      }

      @Override
      public String getRemarks() {
        return "test_remarks";
      }

      @Override
      public String getDefinition() {
        return "test_definition";
      }

      @Override
      public Iterable<Column> getColumns() {
        return Collections.emptyList();
      }

      @Override
      public Iterable<Trigger> getTriggers() {
        return Collections.emptyList();
      }
    };

    assertThat(tableGrepFilter.test(table), is(false));
  }

  @Test
  void testTableGrepFilterWithColumnInclusionRule() {
    final InclusionRule grepColumnInclusionRule = new RegularExpressionInclusionRule("test_column");
    final GrepOptions grepOptions = GrepOptions.builder().withGrepColumnInclusionRule(grepColumnInclusionRule).build();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    final Table table = new Table() {
      @Override
      public String getFullName() {
        return "test_table";
      }

      @Override
      public String getRemarks() {
        return "test_remarks";
      }

      @Override
      public String getDefinition() {
        return "test_definition";
      }

      @Override
      public Iterable<Column> getColumns() {
        return Collections.singletonList(new Column() {
          @Override
          public String getFullName() {
            return "test_column";
          }
        });
      }

      @Override
      public Iterable<Trigger> getTriggers() {
        return Collections.emptyList();
      }
    };

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithDefinitionInclusionRule() {
    final InclusionRule grepDefinitionInclusionRule = new RegularExpressionInclusionRule("test_definition");
    final GrepOptions grepOptions = GrepOptions.builder().withGrepDefinitionInclusionRule(grepDefinitionInclusionRule).build();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    final Table table = new Table() {
      @Override
      public String getFullName() {
        return "test_table";
      }

      @Override
      public String getRemarks() {
        return "test_remarks";
      }

      @Override
      public String getDefinition() {
        return "test_definition";
      }

      @Override
      public Iterable<Column> getColumns() {
        return Collections.emptyList();
      }

      @Override
      public Iterable<Trigger> getTriggers() {
        return Collections.emptyList();
      }
    };

    assertThat(tableGrepFilter.test(table), is(true));
  }

  @Test
  void testTableGrepFilterWithNonMatchingDefinitionInclusionRule() {
    final InclusionRule grepDefinitionInclusionRule = new RegularExpressionInclusionRule("non_matching_definition");
    final GrepOptions grepOptions = GrepOptions.builder().withGrepDefinitionInclusionRule(grepDefinitionInclusionRule).build();
    final TableGrepFilter tableGrepFilter = new TableGrepFilter(grepOptions);

    final Table table = new Table() {
      @Override
      public String getFullName() {
        return "test_table";
      }

      @Override
      public String getRemarks() {
        return "test_remarks";
      }

      @Override
      public String getDefinition() {
        return "test_definition";
      }

      @Override
      public Iterable<Column> getColumns() {
        return Collections.emptyList();
      }

      @Override
      public Iterable<Trigger> getTriggers() {
        return Collections.emptyList();
      }
    };

    assertThat(tableGrepFilter.test(table), is(false));
  }
}
