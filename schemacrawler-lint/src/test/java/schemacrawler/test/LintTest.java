/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
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
                 7,
                 catalog.getTables(schema).size());

    final LinterConfigs linterConfigs = new LinterConfigs();
    final LinterConfig linterConfig = new LinterConfig("schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns");
    linterConfig.put("bad-column-names", ".*\\.COUNTRY");
    linterConfigs.add(linterConfig);

    final LintedCatalog lintedDatabase = new LintedCatalog(catalog,
                                                           getConnection(),
                                                           linterConfigs);
    final LintCollector lintCollector = lintedDatabase.getCollector();
    assertEquals(49, lintCollector.size());

    try (final TestWriter out = new TestWriter("text");)
    {
      for (final Lint<?> lint: lintCollector)
      {
        out.println(lint);
      }

      out.assertEquals(LINTS_OUTPUT + "schemacrawler.lints.txt");
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
                 7,
                 catalog.getTables(schema).size());

    final LinterConfigs linterConfigs = new LinterConfigs();

    final LintedCatalog lintedDatabase = new LintedCatalog(catalog,
                                                           getConnection(),
                                                           linterConfigs);
    final LintCollector lintCollector = lintedDatabase.getCollector();
    assertEquals(38, lintCollector.size());

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
