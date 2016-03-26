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
  public void testSystemExitLinterConfigCommandLine()
    throws Exception
  {
    final Config additionalConfig = new Config();
    additionalConfig.put("lintdispatch", "terminate_system");

    exit.expectSystemExitWithStatus(1);

    executeLintCommandLine(TextOutputFormat.text,
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
