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

package schemacrawler.integration.test;


import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.integration.test.util.TestBase;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.SchemaCrawlerExecutor;
import schemacrawler.tools.integration.freemarker.FreeMarkerExecutor;
import schemacrawler.tools.integration.jung.JungExecutor;
import schemacrawler.tools.integration.velocity.VelocityExecutor;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;

public class ExecutorIntegrationTest
  extends TestBase
{

  public static Test suite()
  {
    return new TestSuite(ExecutorIntegrationTest.class);
  }

  public ExecutorIntegrationTest(String name)
  {
    super(name);
  }

  private void executorIntegrationTest(SchemaCrawlerExecutor executor,
                                       OutputOptions outputOptions)
  {
    try
    {
      SchemaTextOptions schemaTextOptions = new SchemaTextOptions(null,
          outputOptions, SchemaTextDetailType.BASIC);

      executor.execute(new SchemaCrawlerOptions(),
                       schemaTextOptions,
                       dataSource);

      // Check post-conditions
      File outputFile = outputOptions.getOutputFile();
      if (!outputFile.exists())
      {
        fail("Output file '" + outputFile.getName() + "' was not created");
      }

    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }

  }

  public void testSchemaGraphingWithJung()
  {
    try
    {
      String outputFilename = File.createTempFile("schemacrawler", ".jpg")
        .getAbsolutePath();
      OutputOptions outputOptions = new OutputOptions("800x600", outputFilename);
      executorIntegrationTest(new JungExecutor(), outputOptions);
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }

  public void testTemplatingWithVelocity()
  {
    try
    {
      String outputFilename = File.createTempFile("schemacrawler", ".txt")
        .getAbsolutePath();
      OutputOptions outputOptions = new OutputOptions("plaintextschema.vm",
          outputFilename);
      executorIntegrationTest(new VelocityExecutor(), outputOptions);
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }

  public void testTemplatingWithFreeMarker()
  {
    try
    {
      String outputFilename = File.createTempFile("schemacrawler", ".txt")
        .getAbsolutePath();
      OutputOptions outputOptions = new OutputOptions("plaintextschema.ftl",
          outputFilename);
      executorIntegrationTest(new FreeMarkerExecutor(), outputOptions);
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }
  
}
