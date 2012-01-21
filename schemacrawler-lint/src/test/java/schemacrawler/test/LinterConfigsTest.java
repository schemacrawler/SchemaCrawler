package schemacrawler.test;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterConfig;
import schemacrawler.tools.lint.LinterConfigs;

public class LinterConfigsTest
{

  private static Reader readerForResource(final String resource)
    throws IOException
  {
    final InputStream inputStream = LinterConfigsTest.class
      .getResource(resource).openStream();
    Reader reader;
    if (inputStream != null)
    {
      reader = new InputStreamReader(inputStream);
    }
    else
    {
      reader = null;
    }
    return reader;
  }

  @Test
  public void testParseGoodLinterConfigs()
    throws IOException, ParserConfigurationException, SAXException
  {
    final Reader reader = readerForResource("/schemacrawler-linter-configs-1.xml");
    final LinterConfigs linterConfigs = new LinterConfigs();
    linterConfigs.parse(reader);

    assertEquals(3, linterConfigs.size());
    LinterConfig linterConfig;

    linterConfig = linterConfigs.get("linter.Linter1");
    assertEquals(LintSeverity.medium, linterConfig.getSeverity());

    linterConfig = linterConfigs.get("linter.Linter2");
    assertTrue(linterConfig.getSeverity() == null);

    linterConfig = linterConfigs.get("linter.Linter3");
    assertEquals(LintSeverity.high, linterConfig.getSeverity());
  }

}
