/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.createTempFile;
import static sf.util.Utility.UTF8;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintedCatalog;
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
      .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*FOR_LINT"));

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull(catalog);
    assertEquals(1, catalog.getSchemas().size());
    final Schema schema = catalog.getSchema("PUBLIC.FOR_LINT");
    assertNotNull("FOR_LINT schema not found", schema);
    assertEquals("FOR_LINT tables not found", 4, catalog.getTables(schema)
      .size());

    final LintedCatalog lintedDatabase = new LintedCatalog(catalog,
                                                           new LinterConfigs());
    final LintCollector lintCollector = lintedDatabase.getCollector();
    assertEquals(23, lintCollector.size());

    final Path testOutputFile = createTempFile("lints.",
                                                    "data");

    try (final PrintWriter writer = new PrintWriter(testOutputFile.toFile(), UTF8.name());)
    {
      for (final Lint<?> lint: lintCollector)
      {
        writer.println(lint);
      }
    }

    final List<String> failures = compareOutput(LINTS_OUTPUT + "schemacrawler.lints.txt", testOutputFile);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
