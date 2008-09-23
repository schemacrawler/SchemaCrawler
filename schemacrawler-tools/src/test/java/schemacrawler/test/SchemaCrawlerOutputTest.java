/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.custommonkey.xmlunit.Validator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutor;
import schemacrawler.execute.QueryExecutorException;
import schemacrawler.main.SchemaCrawlerCommandLine;
import schemacrawler.main.SchemaCrawlerMain;
import schemacrawler.main.dbconnector.TestUtilityDatabaseConnector;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.Command;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.datatext.DataToolsExecutable;
import schemacrawler.tools.operation.Operation;
import schemacrawler.tools.operation.OperationExecutable;
import schemacrawler.tools.operation.OperationOptions;
import schemacrawler.tools.schematext.SchemaCrawlerExecutable;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;
import schemacrawler.utility.datasource.PropertiesDataSourceException;
import schemacrawler.utility.test.TestUtility;

public class SchemaCrawlerOutputTest
{

  private static TestUtility testUtility = new TestUtility();

  @AfterClass
  public static void afterAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    TestUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void compareCompositeOutput()
    throws Exception
  {
    final OutputFormat outputFormat = OutputFormat.text;

    final String outputFilename = File
      .createTempFile("schemacrawler.compareCompositeOutput.", "test")
      .getAbsolutePath();
    final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                          outputFilename);

    Command[] commands = new Command[] {
        new Command("maximum_schema", false),
        new Command("count", true),
        new Command("dump", true)
    };
    SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(commands,
                                                                        new Config(),
                                                                        new TestUtilityDatabaseConnector(testUtility),
                                                                        outputOptions);

    SchemaCrawlerMain.schemacrawler(commandLine, "");

    final File outputFile = new File(outputFilename);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void countOperatorOutput()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.text,
                                                          outputFilename);
    final OperationOptions operatorOptions = new OperationOptions(new Config(),
                                                                  outputOptions,
                                                                  Operation.count);

    final OperationExecutable executable = new OperationExecutable();
    executable.setToolOptions(operatorOptions);
    executable.execute(testUtility.getDataSource());

    final File outputFile = new File(outputFilename);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void countOperatorValidXMLOutput()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler",
                                                      ".test.html")
      .getAbsolutePath();

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.html,
                                                          outputFilename);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);
    outputOptions.setNoInfo(false);
    final OperationOptions operatorOptions = new OperationOptions(new Config(),
                                                                  outputOptions,
                                                                  Operation.count);

    final OperationExecutable executable = new OperationExecutable();
    executable.setToolOptions(operatorOptions);
    executable.execute(testUtility.getDataSource());

    final Validator validator = new Validator(new FileReader(outputFilename));
    validator.assertIsValid();
  }

  @Test
  public void dataOutput()
    throws IOException, SchemaCrawlerException, QueryExecutorException
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final DataTextFormatOptions textFormatOptions = new DataTextFormatOptions(new Config(),
                                                                              new OutputOptions(OutputFormat.text,
                                                                                                outputFilename),
                                                                              null);

    final DataHandler dataHandler = DataToolsExecutable
      .createDataHandler(textFormatOptions);
    final QueryExecutor executor = new QueryExecutor(testUtility
      .getDataSource(), dataHandler);
    executor.executeSQL("SELECT COUNT(*) FROM CUSTOMER");

    final File outputFile = new File(outputFilename);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void dumpOperatorValidXMLOutput()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler",
                                                      ".test.html")
      .getAbsolutePath();

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.html,
                                                          outputFilename);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);
    outputOptions.setNoInfo(false);
    final OperationOptions operatorOptions = new OperationOptions(new Config(),
                                                                  outputOptions,
                                                                  Operation.dump);

    final OperationExecutable executable = new OperationExecutable();
    executable.setToolOptions(operatorOptions);
    executable.execute(testUtility.getDataSource());

    final Validator validator = new Validator(new FileReader(outputFilename));
    validator.assertIsValid();
  }

  @Test
  public void schemaOutput()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final SchemaTextOptions textFormatOptions = new SchemaTextOptions(new Config(),
                                                                      new OutputOptions(OutputFormat.text,
                                                                                        outputFilename),
                                                                      SchemaTextDetailType.brief_schema);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable();
    executable.setToolOptions(textFormatOptions);
    executable.execute(testUtility.getDataSource());

    final File outputFile = new File(outputFilename);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void schemaValidXMLOutput()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler",
                                                      ".test.html")
      .getAbsolutePath();

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.html,
                                                          outputFilename);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);
    outputOptions.setNoInfo(false);
    final SchemaTextOptions textFormatOptions = new SchemaTextOptions(new Config(),
                                                                      outputOptions,
                                                                      SchemaTextDetailType.maximum_schema);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable();
    executable.setToolOptions(textFormatOptions);
    executable.execute(testUtility.getDataSource());

    final Validator validator = new Validator(new FileReader(outputFilename));
    validator.assertIsValid();
  }
}
