/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.analysis.lint.Lint;
import schemacrawler.tools.analysis.lint.LintCollector;
import schemacrawler.tools.analysis.lint.LintedDatabase;
import schemacrawler.utility.TestDatabase;
import sf.util.ObjectToString;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class LintTest
{

  private static TestDatabase testUtility = new TestDatabase("publisher sales",
                                                             "for_lint");

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.startMemoryDatabase();
  }

  @Test
  public void lints()
    throws Exception
  {
    final Database database = testUtility
      .getDatabase(new SchemaCrawlerOptions());
    assertNotNull(database);

    final LintedDatabase lintedDatabase = new LintedDatabase(database);
    final LintCollector lintCollector = lintedDatabase.getCollector();
    assertEquals(10, lintCollector.size());

    final Multimap<String, Lint<?>> lintMap = ArrayListMultimap.create();
    for (final Lint<?> lint: lintCollector)
    {
      System.out.println(ObjectToString.toString(lint));
      lintMap.put(lint.getId(), lint);
    }
  }

}
