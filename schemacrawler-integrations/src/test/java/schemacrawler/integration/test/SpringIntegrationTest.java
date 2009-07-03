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
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.Executable;
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
  {
    TestDatabase.disableApplicationLogging();
    testUtility.createMemoryDatabase();
  }

  private final ApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml");

  @Test
  public void testExecutables()
    throws Exception
  {
    final List<String> failures = new ArrayList<String>();
    for (final String beanDefinitionName: appContext.getBeanDefinitionNames())
    {
      final Object bean = appContext.getBean(beanDefinitionName);
      if (bean instanceof Executable<?>)
      {
        executeAndCheckForOutputFile(beanDefinitionName,
                                     (Executable<?>) bean,
                                     failures);
      }
    }
    if (failures.size() > 0)
    {
      System.err.println(failures);
      fail(failures.toString());
    }
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

  private void executeAndCheckForOutputFile(final String executableName,
                                            final Executable<?> executable,
                                            final List<String> failures)
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);
    executable.execute(testUtility.getDataSource());

    final File testOutputFile = new File(outputFilename);
    assertTrue(testOutputFile.exists());
    assertTrue(testOutputFile.length() > 0);
    TestUtility
      .compareOutput(executableName + ".txt", testOutputFile, failures);
  }

}
