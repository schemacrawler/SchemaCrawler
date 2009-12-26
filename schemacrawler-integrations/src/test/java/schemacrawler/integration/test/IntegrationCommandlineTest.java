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


import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.utility.TestDatabase;

public class IntegrationCommandlineTest
{

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
  }

  @Test
  public void templatingWithFreeMarker()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", ".txt")
      .getAbsolutePath();
    final OutputOptions outputOptions = new OutputOptions("plaintextschema.ftl",
                                                          outputFilename);

    final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(testUtility
                                                                                .getDatabaseConnectionOptions(),
                                                                              SchemaInfoLevel
                                                                                .standard(),
                                                                              "freemarker",
                                                                              new Config(),
                                                                              outputOptions);

    executeAndCheckForOutputFile(commandLine, outputOptions);
  }

  @Test
  public void templatingWithVelocity()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", ".txt")
      .getAbsolutePath();
    final OutputOptions outputOptions = new OutputOptions("plaintextschema.vm",
                                                          outputFilename);
    final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(testUtility
                                                                                .getDatabaseConnectionOptions(),
                                                                              SchemaInfoLevel
                                                                                .standard(),
                                                                              "velocity",
                                                                              new Config(),
                                                                              outputOptions);

    executeAndCheckForOutputFile(commandLine, outputOptions);
  }

  private void executeAndCheckForOutputFile(final SchemaCrawlerCommandLine commandLine,
                                            final OutputOptions outputOptions)
    throws Exception
  {
    commandLine.execute();
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
