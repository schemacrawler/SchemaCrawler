/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.TestUtility.readerForResource;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.BaseLintExecutableTest;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.options.TextOutputFormat;

public class LinterConfigsDispatchTest
  extends BaseLintExecutableTest
{

  private TestOutputStream out;
  private TestOutputStream err;

  @AfterEach
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @BeforeEach
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

    assertThat(linterConfigs.size(), is(1));
    boolean asserted = false;
    for (final LinterConfig linterConfig: linterConfigs)
    {
      if (linterConfig.getLinterId()
        .equals("schemacrawler.tools.linter.LinterTableWithNoIndexes"))
      {
        assertThat(linterConfig.getSeverity(), is(LintSeverity.critical));
        assertThat(linterConfig.getThreshold(), is(1));
        assertThat(linterConfig.isRunLinter(), is(true));
        asserted = true;
      }
    }
    if (!asserted)
    {
      fail();
    }
  }

  @Test
  @ExpectSystemExitWithStatus(1)
  public void testSystemExitLinterConfigCommandLine(final TestInfo testInfo)
    throws Exception
  {

    final Config additionalConfig = new Config();
    additionalConfig.put("lintdispatch", "terminate_system");

    executeLintCommandLine(TextOutputFormat.text,
                           "/schemacrawler-linter-configs-with-dispatch.xml",
                           additionalConfig,
                           "schemacrawler-linter-configs-with-dispatch");

    checkSystemErrLog(testInfo);
  }

  @Test
  @ExpectSystemExitWithStatus(1)
  public void testSystemExitLinterConfigExecutable(final TestInfo testInfo)
    throws Exception
  {

    final Config additionalConfig = new Config();
    additionalConfig.put("lintdispatch", "terminate_system");

    executeLintExecutable(TextOutputFormat.text,
                          "/schemacrawler-linter-configs-with-dispatch.xml",
                          additionalConfig,
                          "schemacrawler-linter-configs-with-dispatch");

    checkSystemErrLog(testInfo);
  }

  private void checkSystemErrLog(final TestInfo testInfo)
    throws Exception
  {
    assertThat(fileResource(out), hasNoContent());
    assertThat(fileResource(err),
               hasSameContentAs(classpathResource(currentMethodName(testInfo)
                                                  + ".log")));
  }

}
