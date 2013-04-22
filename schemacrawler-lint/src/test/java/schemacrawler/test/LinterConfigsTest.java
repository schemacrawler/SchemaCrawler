/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static schemacrawler.test.utility.TestUtility.readerForResource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

import org.junit.Test;

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
    final Reader reader = readerForResource("bad-schemacrawler-linter-configs-a.xml", Charset.forName("UTF-8"));
    final LinterConfigs linterConfigs = new LinterConfigs();
    linterConfigs.parse(reader);
    assertEquals(3, linterConfigs.size());

    LinterConfig linterConfig;

    linterConfig = linterConfigs.get("linter.Linter1");
    assertEquals(null, linterConfig.getConfig().getStringValue("exclude", null));

    linterConfig = linterConfigs.get("linter.Linter2");
    assertEquals(".*", linterConfig.getConfig().getStringValue("exclude", null));

    linterConfig = linterConfigs.get("linter.Linter3");
    assertNull(linterConfig.getSeverity());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBadXml0()
    throws SchemaCrawlerException
  {
    final LinterConfigs linterConfigs = new LinterConfigs();
    linterConfigs.parse(null);
  }

  @Test(expected = SchemaCrawlerException.class)
  public void testParseBadXml1()
    throws SchemaCrawlerException, IOException
  {
    final Reader reader = new StringReader("some random string that is not XML");
    final LinterConfigs linterConfigs = new LinterConfigs();
    linterConfigs.parse(reader);
    assertEquals(0, linterConfigs.size());
  }

  @Test
  public void testParseBadXml2()
    throws SchemaCrawlerException, IOException
  {
    final Reader reader = readerForResource("bad-schemacrawler-linter-configs-2.xml", Charset.forName("UTF-8"));
    final LinterConfigs linterConfigs = new LinterConfigs();
    linterConfigs.parse(reader);
  }

  @Test
  public void testParseGoodLinterConfigs()
    throws SchemaCrawlerException, IOException
  {
    final Reader reader = readerForResource("schemacrawler-linter-configs-1.xml", Charset.forName("UTF-8"));
    final LinterConfigs linterConfigs = new LinterConfigs();
    linterConfigs.parse(reader);

    assertEquals(3, linterConfigs.size());
    LinterConfig linterConfig;

    linterConfig = linterConfigs.get("linter.Linter1");
    assertEquals(LintSeverity.medium, linterConfig.getSeverity());

    linterConfig = linterConfigs.get("linter.Linter2");
    assertTrue(linterConfig.getSeverity() == null);
    assertEquals(".*", linterConfig.getConfig().getStringValue("exclude", null));

    linterConfig = linterConfigs.get("linter.Linter3");
    assertEquals(LintSeverity.high, linterConfig.getSeverity());
  }

}
