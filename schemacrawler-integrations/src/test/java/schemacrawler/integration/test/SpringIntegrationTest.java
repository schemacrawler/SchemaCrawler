/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.createTempFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.TextOutputFormat;

public class SpringIntegrationTest
  extends BaseDatabaseTest
{

  private final ApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml");

  @Test
  public void testExecutables()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();
    for (final String beanDefinitionName: appContext.getBeanDefinitionNames())
    {
      final Object bean = appContext.getBean(beanDefinitionName);
      if (bean instanceof Executable)
      {
        final Executable executable = (Executable) bean;
        executeAndCheckForOutputFile(beanDefinitionName, executable, failures);
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
    assertEquals("Unexpected number of tables in the schema", 6, catalog
      .getTables(schema).size());
  }

  private void executeAndCheckForOutputFile(final String executableName,
                                            final Executable executable,
                                            final List<String> failures)
    throws Exception
  {
    final Path testOutputFile = createTempFile(executableName, "data");

    executable.getOutputOptions().setOutputFile(testOutputFile);
    executable.execute(getConnection());

    failures.addAll(compareOutput(executableName + ".txt",
                                  testOutputFile,
                                  TextOutputFormat.text.name()));
  }

}
