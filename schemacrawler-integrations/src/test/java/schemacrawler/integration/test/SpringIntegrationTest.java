/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareCompressedOutput;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

public class SpringIntegrationTest
  extends BaseDatabaseTest
{

  private final ApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml");

  @Test
  public void testExecutableForXMLSerialization()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();
    final String beanDefinitionName = "executableForXMLSerialization";
    final Object bean = appContext.getBean(beanDefinitionName);
    if (bean instanceof Executable)
    {
      final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = (DatabaseSpecificOverrideOptions) appContext
        .getBean("databaseSpecificOverrideOptions");

      final Executable executable = (Executable) bean;
      executeAndCheckForOutputFile(beanDefinitionName,
                                   executable,
                                   databaseSpecificOverrideOptions,
                                   failures,
                                   true);
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void testExecutables()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();
    for (final String beanDefinitionName: appContext.getBeanDefinitionNames())
    {
      if (beanDefinitionName.equals("executableForXMLSerialization"))
      {
        continue;
      }
      final Object bean = appContext.getBean(beanDefinitionName);
      if (bean instanceof Executable)
      {
        final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = (DatabaseSpecificOverrideOptions) appContext
          .getBean("databaseSpecificOverrideOptions");

        final Executable executable = (Executable) bean;
        executeAndCheckForOutputFile(beanDefinitionName,
                                     executable,
                                     databaseSpecificOverrideOptions,
                                     failures,
                                     false);
      }
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void testSchema()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = (SchemaCrawlerOptions) appContext
      .getBean("schemaCrawlerOptions");

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    assertEquals("Unexpected number of tables in the schema",
                 6,
                 catalog.getTables(schema).size());
  }

  private void executeAndCheckForOutputFile(final String executableName,
                                            final Executable executable,
                                            final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                                            final List<String> failures,
                                            final boolean isCompressedOutput)
    throws Exception
  {
    final Path testOutputFile = IOUtility.createTempFilePath(executableName,
                                                             "data");

    executable.getOutputOptions().setOutputFile(testOutputFile);
    executable.execute(getConnection(), databaseSpecificOverrideOptions);

    if (isCompressedOutput)
    {
      failures.addAll(compareCompressedOutput(executableName + ".txt",
                                              testOutputFile,
                                              TextOutputFormat.text.name()));
    }
    else
    {
      failures
        .addAll(compareOutput(executableName + ".txt",
                              testOutputFile,
                              TextOutputFormat.text.name()));
    }
  }

}
