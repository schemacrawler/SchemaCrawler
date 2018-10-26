/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.readerForResource;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.internal.CheckExitCalled;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.BaseLintExecutableTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.options.TextOutputFormat;

public class LinterConfigsDispatchTest
  extends BaseLintExecutableTest
{

  @Rule
  public final TestName testName = new TestName();
  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  private TestOutputStream out;
  private TestOutputStream err;

  @After
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Before
  public void setUpStreams()
    throws Exception
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

  @Test
  public void testLinterConfigs()
  {

    LinterConfigs linterConfigs = null;
    try
    {
      final Reader reader = readerForResource("schemacrawler-linter-configs-with-dispatch.xml",
                                              UTF_8);
      linterConfigs = new LinterConfigs(new Config());
      linterConfigs.parse(reader);
    }
    catch (IOException | SchemaCrawlerException e)
    {
      fail(e.getMessage());
    }

    assertEquals(1, linterConfigs.size());
    boolean asserted = false;
    for (final LinterConfig linterConfig: linterConfigs)
    {
      if (linterConfig.getLinterId()
        .equals("schemacrawler.tools.linter.LinterTableWithNoIndexes"))
      {
        assertEquals(LintSeverity.critical, linterConfig.getSeverity());
        assertEquals(1, linterConfig.getThreshold());
        assertTrue(linterConfig.isRunLinter());
        asserted = true;
      }
    }
    if (!asserted)
    {
      fail();
    }
  }

  @Test
  public void testSystemExitLinterConfigCommandLine()
    throws Exception
  {

    final Config additionalConfig = new Config();
    additionalConfig.put("lintdispatch", "terminate_system");

    exit.expectSystemExitWithStatus(1);

    try
    {
      executeLintCommandLine(TextOutputFormat.text,
                             "/schemacrawler-linter-configs-with-dispatch.xml",
                             additionalConfig,
                             "schemacrawler-linter-configs-with-dispatch");
    }
    catch (final CheckExitCalled e)
    {
      // Expected exception
      assertEquals(1, e.getStatus().intValue());
    }
    catch (final Exception e)
    {
      fail(e.getMessage());
    }

    checkSystemErrLog();
  }

  @Test
  public void testSystemExitLinterConfigExecutable()
    throws Exception
  {

    final Config additionalConfig = new Config();
    additionalConfig.put("lintdispatch", "terminate_system");

    exit.expectSystemExitWithStatus(1);

    try
    {
      executeLintExecutable(TextOutputFormat.text,
                            "/schemacrawler-linter-configs-with-dispatch.xml",
                            additionalConfig,
                            "schemacrawler-linter-configs-with-dispatch");
    }
    catch (final CheckExitCalled e)
    {
      // Expected exception
      assertEquals(1, e.getStatus().intValue());
    }
    catch (final Exception e)
    {
      fail(e.getMessage());
    }

    checkSystemErrLog();
  }

  private void checkSystemErrLog()
    throws Exception
  {
    out.assertEmpty();
    err.assertEquals(testName.currentMethodName() + ".log");
  }

}
