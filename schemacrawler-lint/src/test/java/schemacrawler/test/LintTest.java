/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.lint.Linters;

public class LintTest
  extends BaseDatabaseTest
{

  private static final String LINTS_OUTPUT = "lints_output/";

  @Test
  public void lints()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setTableTypes(Arrays.asList("TABLE", "VIEW", "GLOBAL TEMPORARY"));
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*FOR_LINT"));

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull(catalog);
    assertEquals(1, catalog.getSchemas().size());
    final Schema schema = catalog.lookupSchema("PUBLIC.FOR_LINT").orElse(null);
    assertNotNull("FOR_LINT schema not found", schema);
    assertEquals("FOR_LINT tables not found",
                 8,
                 catalog.getTables(schema).size());

    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    final LinterConfig linterConfig = new LinterConfig("schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns");
    linterConfig.setThreshold(0);
    linterConfig.put("bad-column-names", ".*\\.COUNTRY");
    linterConfigs.add(linterConfig);

    final Linters linters = new Linters(linterConfigs);

    final LintedCatalog lintedDatabase = new LintedCatalog(catalog,
                                                           getConnection(),
                                                           linters);
    final LintCollector lintCollector = lintedDatabase.getCollector();
    assertEquals(56, lintCollector.size());

    try (final TestWriter out = new TestWriter("text");)
    {
      for (final Lint<?> lint: lintCollector)
      {
        out.println(lint);
      }

      out.assertEquals(LINTS_OUTPUT + "schemacrawler.lints.txt");
    }

    try (final TestWriter out = new TestWriter("text");)
    {
      out.println(linters.getLintSummary());
      out.assertEquals(LINTS_OUTPUT + "schemacrawler.lints.summary.txt");
    }
  }

  @Test
  public void lintsWithExcludedColumns()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setTableTypes(Arrays.asList("TABLE", "VIEW", "GLOBAL TEMPORARY"));
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*FOR_LINT"));
    schemaCrawlerOptions
      .setColumnInclusionRule(new RegularExpressionExclusionRule(".*\\..*\\..*[123]"));

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull(catalog);
    assertEquals(1, catalog.getSchemas().size());
    final Schema schema = catalog.lookupSchema("PUBLIC.FOR_LINT").orElse(null);
    assertNotNull("FOR_LINT schema not found", schema);
    assertEquals("FOR_LINT tables not found",
                 8,
                 catalog.getTables(schema).size());

    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    final Linters linters = new Linters(linterConfigs);

    final LintedCatalog lintedDatabase = new LintedCatalog(catalog,
                                                           getConnection(),
                                                           linters);
    final LintCollector lintCollector = lintedDatabase.getCollector();
    assertEquals(45, lintCollector.size());

    try (final TestWriter out = new TestWriter("text");)
    {
      for (final Lint<?> lint: lintCollector)
      {
        out.println(lint);
      }

      out.assertEquals(LINTS_OUTPUT
                       + "schemacrawler.lints.excluded_columns.txt");
    }
  }

}
