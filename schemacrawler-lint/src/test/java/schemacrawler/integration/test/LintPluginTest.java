/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.LinterRegistry;

public class LintPluginTest {

  @Test
  public void testLintPlugin() throws Exception {
    final LinterRegistry registry = LinterRegistry.getLinterRegistry();
    for (final String linter :
        new String[] {
          "schemacrawler.tools.linter.LinterColumnTypes",
          "schemacrawler.tools.linter.LinterForeignKeyMismatch",
          "schemacrawler.tools.linter.LinterForeignKeyWithNoIndexes",
          "schemacrawler.tools.linter.LinterNullColumnsInIndex",
          "schemacrawler.tools.linter.LinterNullIntendedColumns",
          "schemacrawler.tools.linter.LinterRedundantIndexes",
          "schemacrawler.tools.linter.LinterTableCycles",
          "schemacrawler.tools.linter.LinterTableWithIncrementingColumns",
          "schemacrawler.tools.linter.LinterTableWithNoIndexes",
          "schemacrawler.tools.linter.LinterTableWithQuotedNames",
          "schemacrawler.tools.linter.LinterTableWithSingleColumn",
          "schemacrawler.tools.linter.LinterTooManyLobs",
        }) {
      assertThat(registry.hasLinter(linter), is(true));
    }
  }
}
