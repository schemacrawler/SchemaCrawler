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
package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.Executable;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.operation.OperationOptions;
import schemacrawler.tools.schematext.SchemaTextOptions;
import schemacrawler.utility.TestDatabase;

public class SpringIntegrationTest
{

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
  {
    TestDatabase.disableApplicationLogging();
    testUtility.createMemoryDatabase();
  }

  private final ApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml");

  @SuppressWarnings("unchecked")
  @Test
  public void testExecutableForCount()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<OperationOptions> executable = (Executable<OperationOptions>) appContext
      .getBean("executableForCount");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExecutableForFreeMarker()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForFreeMarker");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExecutableForJavaScript()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForJavaScript");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExecutableForQuery()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<DataTextFormatOptions> executable = (Executable<DataTextFormatOptions>) appContext
      .getBean("executableForQuery");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExecutableForSchema()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForSchema");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExecutableForVelocity()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForVelocity");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @Test
  public void testSchema()
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = (SchemaCrawlerOptions) appContext
      .getBean("schemaCrawlerOptions");

    final Catalog catalog = testUtility.getCatalog(schemaCrawlerOptions);
    assertNotNull("Could not obtain catalog", catalog);
    assertTrue("Could not find any schemas", catalog.getSchemas().length > 0);

    final Schema schema = catalog.getSchema("PUBLIC");
    assertNotNull("Could not obtain schema", schema);

    assertEquals(6, schema.getTables().length);
  }

  private void executeAndCheckForOutputFile(final Executable<?> executable,
                                            final String outputFilename)
    throws Exception
  {
    executable.execute(testUtility.getDataSource());

    final File outputFile = new File(outputFilename);
    assertTrue(outputFile.exists());
    assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

}
