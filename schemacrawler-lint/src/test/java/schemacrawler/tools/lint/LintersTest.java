/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.linter.LinterProviderTableWithBadlyNamedColumns;
import schemacrawler.tools.linter.LinterProviderTableWithNoIndexes;
import schemacrawler.tools.linter.LinterProviderTableWithNoPrimaryKey;
import schemacrawler.tools.linter.LinterProviderTableWithNoRemarks;
import schemacrawler.tools.linter.LinterProviderTableWithSingleColumn;

public class LintersTest {

  @Test
  public void testTableWithBadlyNamedColumns() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithBadlyNamedColumns().newLinter(lintCollector);
    ((BaseLinter) linter)
        .configure(
            new LinterConfig(
                "test-linters",
                true,
                LintSeverity.high,
                0,
                ".*",
                "",
                ".*",
                "",
                Map.of("bad-column-names", ".*BAD.*")));

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("GOOD_COLUMN");
    table.addColumn("BAD_COLUMN");

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("badly named column"));
  }

  @Test
  public void testTableWithNoIndexes() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoIndexes().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("no indexes"));
  }

  @Test
  public void testTableWithNoPrimaryKey() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoPrimaryKey().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");
    table.addColumn("NAME");

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("no primary key"));
  }

  @Test
  public void testTableWithNoRemarks() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithNoRemarks().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");

    ((BaseLinter) linter).lint(table, null);

    // Should have 2 lints: one for table, one for column ID
    assertThat(lintCollector.getLints().size(), is(2));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("should have remarks"));
  }

  @Test
  public void testTableWithSingleColumn() {
    final LintCollector lintCollector = new LintCollector();
    final Linter linter = new LinterProviderTableWithSingleColumn().newLinter(lintCollector);
    ((BaseLinter) linter).configure(createConfig());

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addColumn("ID");

    ((BaseLinter) linter).lint(table, null);

    assertThat(lintCollector.getLints().size(), is(1));
    assertThat(lintCollector.getLints().iterator().next().getMessage(), is("single column"));
  }

  private LinterConfig createConfig() {
    return new LinterConfig(
        "test-linters", true, LintSeverity.high, 0, ".*", "", ".*", "", Map.of());
  }
}
