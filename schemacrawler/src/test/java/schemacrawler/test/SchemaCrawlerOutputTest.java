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
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutor;
import schemacrawler.test.util.TestBase;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.datatext.DataTextFormatterLoader;
import schemacrawler.tools.operation.Operation;
import schemacrawler.tools.operation.OperatorLoader;
import schemacrawler.tools.operation.OperatorOptions;
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

  public SchemaCrawlerOutputTest(final String name)
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
    catch (final IOException e)
    {
      fail(e.getMessage());
    }

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final SchemaTextOptions textFormatOptions = new SchemaTextOptions(new Properties(),
                                                                      new OutputOptions("text",
                                                                                        outputFilename),
                                                                      SchemaTextDetailType.VERBOSE);

    try
    {
      final SchemaTextFormatter formatter = (SchemaTextFormatter) SchemaTextFormatterLoader
        .load(textFormatOptions);
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      null,
                                                      formatter);
      crawler.crawl(schemaCrawlerOptions);
      assertEquals("Table count does not match", 6, formatter.getTableCount());
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      fail("SchemaCrawlerException getting table count: " + e.getMessage());
    }

    File outputFile = new File(outputFilename);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }

  }

  public void testSchemaOutput()
  {
    try
    {
      String outputFilename = File.createTempFile("schemacrawler", "test")
        .getAbsolutePath();

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      final SchemaTextOptions textFormatOptions = new SchemaTextOptions(new Properties(),
                                                                        new OutputOptions("text",
                                                                                          outputFilename),
                                                                        SchemaTextDetailType.BRIEF);

      final SchemaTextFormatter formatter = (SchemaTextFormatter) SchemaTextFormatterLoader
        .load(textFormatOptions);
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      null,
                                                      formatter);
      crawler.crawl(schemaCrawlerOptions);

      File outputFile = new File(outputFilename);
      if (!outputFile.delete())
      {
        fail("Cannot delete output file");
      }
    }
    catch (final Exception e)
    {
      fail(e.getMessage());
    }

  }

  public void testDataOutput()
  {
    try
    {
      String outputFilename = File.createTempFile("schemacrawler", "test")
        .getAbsolutePath();

      final DataTextFormatOptions textFormatOptions = new DataTextFormatOptions(new Properties(),
                                                                                new OutputOptions("text",
                                                                                                  outputFilename));

      final DataHandler dataHandler = (DataHandler) DataTextFormatterLoader
        .load(textFormatOptions);
      final QueryExecutor executor = new QueryExecutor(dataSource, dataHandler);
      executor.executeSQL("SELECT COUNT(*) FROM CUSTOMER");

      File outputFile = new File(outputFilename);
      if (!outputFile.delete())
      {
        fail("Cannot delete output file");
      }
    }
    catch (final Exception e)
    {
      fail(e.getMessage());
    }

  }

  public void testCountOperatorOutput()
  {
    try
    {
      String outputFilename = File.createTempFile("schemacrawler", "test")
        .getAbsolutePath();

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      final OutputOptions outputOptions = new OutputOptions("text",
                                                            outputFilename);
      final DataTextFormatOptions textFormatOptions = new DataTextFormatOptions(new Properties(),
                                                                                outputOptions);
      final OperatorOptions operatorOptions = new OperatorOptions(new Properties(),
                                                                  outputOptions,
                                                                  Operation.COUNT,
                                                                  null);

      final DataHandler dataHandler = (DataHandler) DataTextFormatterLoader
        .load(textFormatOptions);
      final CrawlHandler formatter = OperatorLoader.load(operatorOptions,
                                                         dataSource
                                                           .getConnection(),
                                                         dataHandler);
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      null,
                                                      formatter);
      crawler.crawl(schemaCrawlerOptions);

      File outputFile = new File(outputFilename);
      if (!outputFile.delete())
      {
        fail("Cannot delete output file");
      }
    }
    catch (final Exception e)
    {
      fail(e.getMessage());
    }

  }

  public void testSchemaValidXMLOutput()
  {
    String outputFilename = "";
    try
    {
      outputFilename = File.createTempFile("schemacrawler", ".test.html")
        .getAbsolutePath();
    }
    catch (final IOException e)
    {
      fail(e.getMessage());
    }

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final OutputOptions outputOptions = new OutputOptions("html",
                                                          outputFilename);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);
    outputOptions.setNoInfo(false);
    final SchemaTextOptions textFormatOptions = new SchemaTextOptions(new Properties(),
                                                                      outputOptions,
                                                                      SchemaTextDetailType.MAXIMUM);
    try
    {
      final CrawlHandler formatter = SchemaTextFormatterLoader
        .load(textFormatOptions);
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      null,
                                                      formatter);
      crawler.crawl(schemaCrawlerOptions);
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      fail("SchemaCrawlerException getting table count: " + e.getMessage());
    }

    try
    {
      final Validator validator = new Validator(new FileReader(outputFilename));
      validator.assertIsValid();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      e.printStackTrace();
      fail("" + e.getMessage());
    }

  }

  public void testCountOperatorValidXMLOutput()
  {
    String outputFilename = "";
    try
    {
      outputFilename = File.createTempFile("schemacrawler", ".test.html")
        .getAbsolutePath();
    }
    catch (final IOException e)
    {
      fail(e.getMessage());
    }

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final OutputOptions outputOptions = new OutputOptions("html",
                                                          outputFilename);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);
    outputOptions.setNoInfo(false);
    final OperatorOptions operatorOptions = new OperatorOptions(new Properties(),
                                                                outputOptions,
                                                                Operation.COUNT,
                                                                null);

    try
    {
      final CrawlHandler formatter = OperatorLoader.load(operatorOptions,
                                                         dataSource
                                                           .getConnection(),
                                                         null);
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      null,
                                                      formatter);
      crawler.crawl(schemaCrawlerOptions);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      fail("SchemaCrawlerException running operation: " + e.getMessage());
    }

    try
    {
      System.out.println(outputFilename);
      final Validator validator = new Validator(new FileReader(outputFilename));
      validator.assertIsValid();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      e.printStackTrace();
      fail("" + e.getMessage());
    }

  }

  public void testDumpOperatorValidXMLOutput()
  {
    String outputFilename = "";
    try
    {
      outputFilename = File.createTempFile("schemacrawler", ".test.html")
        .getAbsolutePath();
    }
    catch (final IOException e)
    {
      fail(e.getMessage());
    }

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final OutputOptions outputOptions = new OutputOptions("html",
                                                          outputFilename);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);
    outputOptions.setNoInfo(false);
    final DataTextFormatOptions textFormatOptions = new DataTextFormatOptions(new Properties(),
                                                                              outputOptions);
    final OperatorOptions operatorOptions = new OperatorOptions(new Properties(),
                                                                outputOptions,
                                                                Operation.DUMP,
                                                                null);

    try
    {
      final DataHandler dataHandler = (DataHandler) DataTextFormatterLoader
        .load(textFormatOptions);
      final CrawlHandler formatter = OperatorLoader.load(operatorOptions,
                                                         dataSource
                                                           .getConnection(),
                                                         dataHandler);
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      null,
                                                      formatter);
      crawler.crawl(schemaCrawlerOptions);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      fail("SchemaCrawlerException running operation: " + e.getMessage());
    }

    try
    {
      final Validator validator = new Validator(new FileReader(outputFilename));
      validator.assertIsValid();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      e.printStackTrace();
      fail("" + e.getMessage());
    }

  }

}
