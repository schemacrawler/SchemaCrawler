/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.io.InputStream;

import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.SchemaCrawlerTextExecutable;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.utility.TestDatabase;

public class SchemaCrawlerXmlOutputTest
{

  private static class LocalEntityResolver
    implements EntityResolver
  {

    public InputSource resolveEntity(final String publicId,
                                     final String systemId)
      throws SAXException, IOException
    {
      final String localResource = "/xhtml1"
                                   + systemId.substring(systemId
                                     .lastIndexOf('/'));
      final InputStream entityStream = LocalEntityResolver.class
        .getResourceAsStream(localResource);
      if (entityStream == null)
      {
        throw new IOException("Could not load " + localResource);
      }
      return new InputSource(entityStream);
    }

  }

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.createMemoryDatabase();
    XMLUnit.setControlEntityResolver(new LocalEntityResolver());
  }

  @Test
  public void brief_schemaValidXMLOutput()
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

    final SchemaCrawlerTextExecutable executable = new SchemaCrawlerTextExecutable(SchemaTextDetailType.list_objects
      .name());
    executable.getSchemaCrawlerOptions().setSchemaInfoLevel(SchemaInfoLevel
      .minimum());
    executable.setOutputOptions(outputOptions);
    executable.execute(testUtility.getConnection());

    validateXml(outputFilename);
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

    final SchemaCrawlerTextExecutable executable = new SchemaCrawlerTextExecutable(Operation.count
      .name());
    executable.setOutputOptions(outputOptions);
    executable.execute(testUtility.getConnection());

    validateXml(outputFilename);
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

    final SchemaCrawlerTextExecutable executable = new SchemaCrawlerTextExecutable(Operation.dump
      .name());
    executable.setOutputOptions(outputOptions);
    executable.execute(testUtility.getConnection());

    validateXml(outputFilename);
  }

  @Test
  public void verbose_schemaValidXMLOutput()
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

    final SchemaCrawlerTextExecutable executable = new SchemaCrawlerTextExecutable(SchemaTextDetailType.verbose_schema
      .name());
    executable.getSchemaCrawlerOptions().setSchemaInfoLevel(SchemaInfoLevel
      .maximum());
    executable.setOutputOptions(outputOptions);
    executable.execute(testUtility.getConnection());

    validateXml(outputFilename);
  }

  @Test
  public void standard_schemaValidXMLOutput()
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

    final SchemaCrawlerTextExecutable executable = new SchemaCrawlerTextExecutable(SchemaTextDetailType.standard_schema
      .name());
    executable.getSchemaCrawlerOptions().setSchemaInfoLevel(SchemaInfoLevel
      .detailed());
    executable.setOutputOptions(outputOptions);
    executable.execute(testUtility.getConnection());

    validateXml(outputFilename);
  }

  private void validateXml(final String outputFilename)
    throws Exception
  {
    final File outputFile = new File(outputFilename);
    try
    {
      final Validator validator = new Validator(new FileReader(outputFile));
      validator.assertIsValid();
    }
    finally
    {
      if (!outputFile.delete())
      {
        fail("Could not delete output file, " + outputFile.getAbsolutePath());
      }
    }
  }

}
