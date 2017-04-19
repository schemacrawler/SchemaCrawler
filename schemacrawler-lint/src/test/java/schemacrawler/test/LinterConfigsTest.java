/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.TestUtility.readerForResource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;

public class LinterConfigsTest
{

  @Test
  public void testParseBadLinterConfigs1()
    throws SchemaCrawlerException, IOException
  {
    final Reader reader = readerForResource("bad-schemacrawler-linter-configs-a.xml",
                                            UTF_8);
    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    linterConfigs.parse(reader);
    assertEquals(3, linterConfigs.size());

    for (final LinterConfig linterConfig: linterConfigs)
    {
      if (linterConfig.getLinterId().equals("linter.Linter1"))
      {
        assertEquals(null,
                     linterConfig.getConfig().getStringValue("exclude", null));
      }

      if (linterConfig.getLinterId().equals("linter.Linter2"))
      {
        assertEquals(".*",
                     linterConfig.getConfig().getStringValue("exclude", null));
      }

      if (linterConfig.getLinterId().equals("linter.Linter3"))
      {
        assertEquals(LintSeverity.medium, linterConfig.getSeverity());
      }
    }
  }

  @Test(expected = NullPointerException.class)
  public void testParseBadXml0()
    throws SchemaCrawlerException
  {
    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    linterConfigs.parse(null);
  }

  @Test(expected = SchemaCrawlerException.class)
  public void testParseBadXml1()
    throws SchemaCrawlerException, IOException
  {
    final Reader reader = new StringReader("some random string that is not XML");
    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    linterConfigs.parse(reader);
    assertEquals(0, linterConfigs.size());
  }

  @Test
  public void testParseBadXml2()
    throws SchemaCrawlerException, IOException
  {
    final Reader reader = readerForResource("bad-schemacrawler-linter-configs-2.xml",
                                            UTF_8);
    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    linterConfigs.parse(reader);
  }

  @Test
  public void testParseGoodLinterConfigs()
    throws SchemaCrawlerException, IOException
  {
    final Reader reader = readerForResource("schemacrawler-linter-configs-1.xml",
                                            UTF_8);
    final LinterConfigs linterConfigs = new LinterConfigs(new Config());
    linterConfigs.parse(reader);

    assertEquals(3, linterConfigs.size());
    for (final LinterConfig linterConfig: linterConfigs)
    {
      if (linterConfig.getLinterId().equals("linter.Linter1"))
      {
        assertEquals(LintSeverity.medium, linterConfig.getSeverity());
        assertTrue(linterConfig.isRunLinter());
      }

      if (linterConfig.getLinterId().equals("linter.Linter2"))
      {
        assertEquals(null, linterConfig.getSeverity());
        assertTrue(!linterConfig.isRunLinter());
        assertEquals(".*",
                     linterConfig.getConfig().getStringValue("exclude", null));
      }

      if (linterConfig.getLinterId().equals("linter.Linter3"))
      {
        assertEquals(LintSeverity.high, linterConfig.getSeverity());
        assertTrue(linterConfig.isRunLinter());
      }
    }
  }

}
