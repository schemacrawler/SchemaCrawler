/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.graph.GraphExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.utility.TestDatabase;
import sf.util.TestUtility;

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
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.createMemoryDatabase();
  }

  private final ApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml");

  @Test
  public void testExecutables()
    throws Exception
  {
    for (final String beanDefinitionName: appContext.getBeanDefinitionNames())
    {
      final Object bean = appContext.getBean(beanDefinitionName);
      if (bean instanceof Executable)
      {
        final Executable executable = (Executable) bean;
        if (!"graph".equals(executable.getCommand())
            && !(executable instanceof GraphExecutable))
        {
          executeAndCheckForOutputFile(beanDefinitionName, executable);
        }
      }
    }
  }

  @Test
  public void testSchema()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = (SchemaCrawlerOptions) appContext
      .getBean("schemaCrawlerOptions");

    final Schema schema = testUtility.getSchema(schemaCrawlerOptions, "PUBLIC");
    assertNotNull("Could not obtain schema", schema);

    assertEquals(6, schema.getTables().length);
  }

  private void executeAndCheckForOutputFile(final String executableName,
                                            final Executable executable)
    throws Exception
  {
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + executableName + ".",
                                                    ".test");
    testOutputFile.delete();

    executable.getOutputOptions().setOutputFileName(testOutputFile
      .getAbsolutePath());
    executable.execute(testUtility.getConnection());

    final List<String> failures = TestUtility
      .compareOutput(executableName + ".txt",
                     testOutputFile,
                     (OutputFormat) null);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
