/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;

import java.sql.Connection;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class SpringIntegrationTest
  extends BaseDatabaseTest
{

  private final ApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml");

  private void executeAndCheckForOutputFile(final String executableName,
                                            final Connection connection,
                                            final SchemaCrawlerExecutable executable,
                                            final SchemaRetrievalOptions schemaRetrievalOptions)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final OutputOptions outputOptions = (OutputOptions) FieldUtils
        .readField(executable, "outputOptions", true);
      final OutputOptionsBuilder outputOptionsBuilder = OutputOptionsBuilder
        .builder(outputOptions).withOutputWriter(out);

      executable.setOutputOptions(outputOptionsBuilder.toOptions());

      executable.setConnection(connection);
      executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
      executable.execute();

    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(executableName + ".txt")));
  }

  @Test
  public void testExecutables(final Connection connection)
    throws Exception
  {
    final String beanDefinitionName = "executableForSchema";
    final SchemaCrawlerExecutable executable = appContext
      .getBean(beanDefinitionName, SchemaCrawlerExecutable.class);
    final SchemaRetrievalOptions schemaRetrievalOptions = appContext
      .getBean("schemaRetrievalOptions", SchemaRetrievalOptions.class);

    executeAndCheckForOutputFile(beanDefinitionName,
                                 connection,
                                 executable,
                                 schemaRetrievalOptions);
  }

  @Test
  public void testSchema(final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = appContext
      .getBean("schemaCrawlerOptions", SchemaCrawlerOptions.class);

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    assertThat("Unexpected number of tables in the schema",
               catalog.getTables(schema),
               hasSize(10));
  }

}
