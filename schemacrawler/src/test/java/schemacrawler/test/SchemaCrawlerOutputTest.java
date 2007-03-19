/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.custommonkey.xmlunit.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutor;
import schemacrawler.main.Config;
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
import dbconnector.datasource.PropertiesDataSourceException;
import dbconnector.test.TestUtility;

public class SchemaCrawlerOutputTest
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerOutputTest.class.getName());

  private final TestUtility testUtility = new TestUtility();

  @Before
  public void before()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.setUp();
  }

  @After
  public void after()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.tearDown();
  }

  @Test
  public void countOperatorOutput()
  {
    try
    {
      final String outputFilename = File
        .createTempFile("schemacrawler", "test").getAbsolutePath();

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      final OutputOptions outputOptions = new OutputOptions("text",
                                                            outputFilename);
      final DataTextFormatOptions textFormatOptions = new DataTextFormatOptions(new Config(),
                                                                                outputOptions);
      final OperatorOptions operatorOptions = new OperatorOptions(outputOptions,
                                                                  Operation.COUNT,
                                                                  null);

      final DataHandler dataHandler = DataTextFormatterLoader
        .load(textFormatOptions);
      final CrawlHandler formatter = OperatorLoader.load(operatorOptions,
                                                         testUtility
                                                           .getDataSource()
                                                           .getConnection(),
                                                         dataHandler);
      final SchemaCrawler crawler = new SchemaCrawler(testUtility
        .getDataSource(), null, formatter);
      crawler.crawl(schemaCrawlerOptions);

      final File outputFile = new File(outputFilename);
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

  @Test
  public void countOperatorValidXMLOutput()
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
    final OperatorOptions operatorOptions = new OperatorOptions(outputOptions,
                                                                Operation.COUNT,
                                                                null);

    try
    {
      final CrawlHandler formatter = OperatorLoader.load(operatorOptions,
                                                         testUtility
                                                           .getDataSource()
                                                           .getConnection(),
                                                         null);
      final SchemaCrawler crawler = new SchemaCrawler(testUtility
        .getDataSource(), null, formatter);
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

  @Test
  public void dataOutput()
  {
    try
    {
      final String outputFilename = File
        .createTempFile("schemacrawler", "test").getAbsolutePath();

      final DataTextFormatOptions textFormatOptions = new DataTextFormatOptions(new Config(),
                                                                                new OutputOptions("text",
                                                                                                  outputFilename));

      final DataHandler dataHandler = DataTextFormatterLoader
        .load(textFormatOptions);
      final QueryExecutor executor = new QueryExecutor(testUtility
        .getDataSource(), dataHandler);
      executor.executeSQL("SELECT COUNT(*) FROM CUSTOMER");

      final File outputFile = new File(outputFilename);
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

  @Test
  public void dumpOperatorValidXMLOutput()
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
    final DataTextFormatOptions textFormatOptions = new DataTextFormatOptions(new Config(),
                                                                              outputOptions);
    final OperatorOptions operatorOptions = new OperatorOptions(outputOptions,
                                                                Operation.DUMP,
                                                                null);

    try
    {
      final DataHandler dataHandler = DataTextFormatterLoader
        .load(textFormatOptions);
      final CrawlHandler formatter = OperatorLoader.load(operatorOptions,
                                                         testUtility
                                                           .getDataSource()
                                                           .getConnection(),
                                                         dataHandler);
      final SchemaCrawler crawler = new SchemaCrawler(testUtility
        .getDataSource(), null, formatter);
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

  @Test
  public void schemaOutput()
  {
    try
    {
      final String outputFilename = File
        .createTempFile("schemacrawler", "test").getAbsolutePath();

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      final SchemaTextOptions textFormatOptions = new SchemaTextOptions(new Config(),
                                                                        new OutputOptions("text",
                                                                                          outputFilename),
                                                                        SchemaTextDetailType.BRIEF);

      final SchemaTextFormatter formatter = (SchemaTextFormatter) SchemaTextFormatterLoader
        .load(textFormatOptions);
      final SchemaCrawler crawler = new SchemaCrawler(testUtility
        .getDataSource(), null, formatter);
      crawler.crawl(schemaCrawlerOptions);

      final File outputFile = new File(outputFilename);
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

  @Test
  public void schemaValidXMLOutput()
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
    final SchemaTextOptions textFormatOptions = new SchemaTextOptions(new Config(),
                                                                      outputOptions,
                                                                      SchemaTextDetailType.MAXIMUM);
    try
    {
      final CrawlHandler formatter = SchemaTextFormatterLoader
        .load(textFormatOptions);
      final SchemaCrawler crawler = new SchemaCrawler(testUtility
        .getDataSource(), null, formatter);
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

  @Test
  public void tableCountFromPlainTextFormatter()
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
    final SchemaTextOptions textFormatOptions = new SchemaTextOptions(new Config(),
                                                                      new OutputOptions("text",
                                                                                        outputFilename),
                                                                      SchemaTextDetailType.VERBOSE);

    try
    {
      final SchemaTextFormatter formatter = (SchemaTextFormatter) SchemaTextFormatterLoader
        .load(textFormatOptions);
      final SchemaCrawler crawler = new SchemaCrawler(testUtility
        .getDataSource(), null, formatter);
      crawler.crawl(schemaCrawlerOptions);
      assertEquals("Table count does not match", 6, formatter.getTableCount());
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      fail("SchemaCrawlerException getting table count: " + e.getMessage());
    }

    final File outputFile = new File(outputFilename);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }

  }

}
