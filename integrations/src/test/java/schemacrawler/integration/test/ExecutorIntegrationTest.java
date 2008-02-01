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

package schemacrawler.integration.test;


import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.integration.freemarker.FreeMarkerRenderer;
import schemacrawler.tools.integration.jung.JungExecutable;
import schemacrawler.tools.integration.velocity.VelocityRenderer;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;
import schemacrawler.utility.datasource.PropertiesDataSourceException;
import schemacrawler.utility.test.TestUtility;

public class ExecutorIntegrationTest
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
    testUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void schemaGraphingWithJung()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", ".jpg")
      .getAbsolutePath();
    final OutputOptions outputOptions = new OutputOptions("800x600",
                                                          outputFilename);
    executeAndCheckForOutputFile(new JungExecutable(), outputOptions);
  }

  @Test
  public void templatingWithFreeMarker()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", ".txt")
      .getAbsolutePath();
    final OutputOptions outputOptions = new OutputOptions("plaintextschema.ftl",
                                                          outputFilename);
    executeAndCheckForOutputFile(new FreeMarkerRenderer(), outputOptions);
  }

  @Test
  public void templatingWithVelocity()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", ".txt")
      .getAbsolutePath();
    final OutputOptions outputOptions = new OutputOptions("plaintextschema.vm",
                                                          outputFilename);
    executeAndCheckForOutputFile(new VelocityRenderer(), outputOptions);
  }

  private void executeAndCheckForOutputFile(final Executable<SchemaTextOptions> executable,
                                            final OutputOptions outputOptions)
    throws Exception
  {
    final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(null,
                                                                      outputOptions,
                                                                      SchemaTextDetailType.basic_schema);
    executable.setToolOptions(schemaTextOptions);

    executable.execute(testUtility.getDataSource());

    // Check post-conditions
    final File outputFile = outputOptions.getOutputFile();
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

}
