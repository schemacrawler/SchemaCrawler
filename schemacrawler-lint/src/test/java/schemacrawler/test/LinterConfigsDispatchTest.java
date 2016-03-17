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
import static org.junit.Assert.assertTrue;
import static schemacrawler.test.utility.TestUtility.readerForResource;

import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.options.TextOutputFormat;

public class LinterConfigsDispatchTest
  extends BaseLintExecutableTest
{

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  @Test
  public void testSystemExitLinterConfig()
    throws Exception
  {
    final Config additionalConfig = new Config();
    additionalConfig.put("lintdispatch", "terminate_system");

    exit.expectSystemExitWithStatus(1);

    executeLintExecutable(TextOutputFormat.text,
                          "/schemacrawler-linter-configs-with-dispatch.xml",
                          additionalConfig,
                          "schemacrawler-linter-configs-with-dispatch");

  }

  @Test
  public void testSystemExitLinterConfiguration()
    throws Exception
  {
    final Reader reader = readerForResource("schemacrawler-linter-configs-with-dispatch.xml",
                                            StandardCharsets.UTF_8);
    final LinterConfigs linterConfigs = new LinterConfigs();
    linterConfigs.parse(reader);

    assertEquals(1, linterConfigs.size());
    for (final LinterConfig linterConfig: linterConfigs)
    {
      if (linterConfig.getLinterId()
        .equals("schemacrawler.tools.linter.LinterTableWithNoIndexes"))
      {
        assertEquals(LintSeverity.critical, linterConfig.getSeverity());
        assertEquals(1, linterConfig.getThreshold());
        assertTrue(linterConfig.isRunLinter());
      }
    }
  }

}
