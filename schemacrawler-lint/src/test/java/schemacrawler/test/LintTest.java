/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.TestDatabase;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintedDatabase;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.lint.executable.LintExecutable;

public class LintTest
{

  private static TestDatabase testDatabase = new TestDatabase();

  private static final String LINTS_OUTPUT = "lints_output/";

  @AfterClass
  public static void afterAllTests()
  {
    testDatabase.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testDatabase.startMemoryDatabase();
  }

  @Test
  public void lints()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(".*FOR_LINT",
                                                InclusionRule.NONE));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    assertNotNull(database);
    assertEquals(1, database.getSchemas().length);
    assertNotNull("FOR_LINT schema not found",
                  database.getSchema("PUBLIC.FOR_LINT"));
    assertEquals("FOR_LINT tables not found",
                 5,
                 database.getSchema("PUBLIC.FOR_LINT").getTables().length);

    final LintedDatabase lintedDatabase = new LintedDatabase(database,
                                                             new LinterConfigs());
    final LintCollector lintCollector = lintedDatabase.getCollector();
    assertEquals(25, lintCollector.size());

    final File testOutputFile = File.createTempFile("schemacrawler.lints.",
                                                    ".test");
    testOutputFile.delete();

    final PrintWriter writer = new PrintWriter(new FileWriter(testOutputFile));
    for (final Lint<?> lint: lintCollector)
    {
      writer.println(lint);
    }
    writer.close();

    final List<String> failures = TestUtility
      .compareOutput(LINTS_OUTPUT + "schemacrawler.lints.txt", testOutputFile);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
