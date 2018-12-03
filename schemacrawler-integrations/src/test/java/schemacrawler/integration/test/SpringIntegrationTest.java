/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

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
      final SchemaCrawlerExecutable executable = appContext
        .getBean(beanDefinitionName, SchemaCrawlerExecutable.class);
      final SchemaRetrievalOptions schemaRetrievalOptions = (SchemaRetrievalOptions) appContext
        .getBean("schemaRetrievalOptions");

      executeAndCheckForOutputFile(beanDefinitionName,
                                   executable,
                                   schemaRetrievalOptions,
                                   failures,
                                   false);
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
                 10,
                 catalog.getTables(schema).size());
  }

  private void executeAndCheckForOutputFile(final String executableName,
                                            final SchemaCrawlerExecutable executable,
                                            final SchemaRetrievalOptions schemaRetrievalOptions,
                                            final List<String> failures,
                                            final boolean isCompressedOutput)
    throws Exception
  {
    final Path testOutputFile = IOUtility.createTempFilePath(executableName,
                                                             "data");

    final SchemaCrawlerOptions schemaCrawlerOptions = (SchemaCrawlerOptions) FieldUtils
      .readField(executable, "schemaCrawlerOptions", true);
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder(schemaCrawlerOptions).includeAllRoutines();
    executable.setSchemaCrawlerOptions(schemaCrawlerOptionsBuilder.toOptions());

    // Force output to test output file
    final OutputOptions outputOptions = (OutputOptions) FieldUtils
      .readField(executable, "outputOptions", true);
    executable.setOutputOptions(forceOutputToTestOutputFile(outputOptions,
                                                            testOutputFile));

    executable.setConnection(getConnection());
    executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
    executable.execute();

    if (isCompressedOutput)
    {
      failures.addAll(compareCompressedOutput(executableName + ".txt",
                                              testOutputFile,
                                              TextOutputFormat.text.name()));
    }
    else
    {
      failures.addAll(compareOutput(executableName + ".txt",
                                    testOutputFile,
                                    TextOutputFormat.text.name()));
    }
  }

  private OutputOptions forceOutputToTestOutputFile(final OutputOptions outputOptions,
                                                    final Path testOutputFile)
  {
    return OutputOptionsBuilder.builder(outputOptions)
      .withOutputFile(testOutputFile).toOptions();
  }

}
