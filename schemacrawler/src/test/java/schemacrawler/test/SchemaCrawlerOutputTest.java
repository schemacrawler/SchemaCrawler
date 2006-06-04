/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.Validator;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.test.util.TestBase;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextFormatter;
import schemacrawler.tools.schematext.SchemaTextFormatterLoader;
import schemacrawler.tools.schematext.SchemaTextOptions;

public class SchemaCrawlerOutputTest
  extends TestBase
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerOutputTest.class.getName());

  public static Test suite()
  {
    return new TestSuite(SchemaCrawlerOutputTest.class);
  }

  public SchemaCrawlerOutputTest(String name)
  {
    super(name);
  }

  public void testTableCountFromPlainTextFormatter()
  {
    String outputFilename = "";
    try
    {
      outputFilename = File.createTempFile("schemacrawler", "test")
        .getAbsolutePath();
    }
    catch (IOException e)
    {
      fail(e.getMessage());
    }

    SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    SchemaTextOptions textFormatOptions = new SchemaTextOptions(
        new Properties(), new OutputOptions("text", outputFilename),
        SchemaTextDetailType.VERBOSE);

    try
    {
      final SchemaTextFormatter formatter = (SchemaTextFormatter) SchemaTextFormatterLoader
        .load(textFormatOptions);
      final SchemaCrawler crawler = new SchemaCrawler(dataSource, formatter);
      crawler.crawl(schemaCrawlerOptions);
      assertEquals("Table count does not match", 5, formatter.getTableCount());
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      fail("SchemaCrawlerException getting table count: " + e.getMessage());
    }

  }

  public void testValidXMLOutput()
  {
    String outputFilename = "";
    try
    {
      outputFilename = File.createTempFile("schemacrawler", ".test.html")
        .getAbsolutePath();
    }
    catch (IOException e)
    {
      fail(e.getMessage());
    }

    SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final OutputOptions outputOptions = new OutputOptions("html",
        outputFilename);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);
    outputOptions.setNoInfo(false);
    SchemaTextOptions textFormatOptions = new SchemaTextOptions(
        new Properties(), outputOptions, SchemaTextDetailType.MAXIMUM);
    try
    {
      final CrawlHandler formatter = SchemaTextFormatterLoader
        .load(textFormatOptions);
      final SchemaCrawler crawler = new SchemaCrawler(dataSource, formatter);
      crawler.crawl(schemaCrawlerOptions);
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      fail("SchemaCrawlerException getting table count: " + e.getMessage());
    }

    try
    {
      Validator validator = new Validator(new FileReader(outputFilename));
      validator.assertIsValid();
    }
    catch (Exception e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      e.printStackTrace();
      fail("" + e.getMessage());
    }

  }

}
