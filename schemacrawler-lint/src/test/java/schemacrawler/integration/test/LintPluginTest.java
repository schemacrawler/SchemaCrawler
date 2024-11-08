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
