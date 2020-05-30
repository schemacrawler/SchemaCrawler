/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestLoggingExtension;
import schemacrawler.testdb.TestSchemaCreatorMain;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

@ExtendWith(TestLoggingExtension.class)
@ExtendWith(TestContextParameterResolver.class)
public class SQLiteExecutableTest
  extends BaseSqliteTest
{

  @Test
  public void list(final TestContext testContext)
    throws Exception
  {
    run(testContext.testMethodFullName(), InfoLevel.minimum, "list");
  }

  @Test
  public void count(final TestContext testContext)
    throws Exception
  {
    run(testContext.testMethodFullName(), InfoLevel.minimum, "count");
  }

  @Test
  public void dump(final TestContext testContext)
    throws Exception
  {
    run(testContext.testMethodFullName(), InfoLevel.standard, "dump");
  }

  private void run(final String currentMethodFullName,
                   final InfoLevel infoLevel,
                   final String command)
    throws Exception
  {
    final Path sqliteDbFile = IOUtility
      .createTempFilePath("sc", ".db")
      .normalize()
      .toAbsolutePath();

    TestSchemaCreatorMain.call("--url", "jdbc:sqlite:" + sqliteDbFile);

    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", sqliteDbFile.toString());

    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder()
      .withSchemaInfoLevel(infoLevel.toSchemaInfoLevel());
    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder
      .builder()
      .withLoadOptionsBuilder(loadOptionsBuilder)
      .toOptions();

    final SchemaTextOptions textOptions =
      SchemaTextOptionsBuilder.newSchemaTextOptions();

    final SchemaCrawlerExecutable executable =
      new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
                                            .builder(textOptions)
                                            .toConfig());

    assertThat(outputOf(executableExecution(createConnection(sqliteDbFile),
                                            executable)),
               hasSameContentAs(classpathResource(currentMethodFullName)));
  }

}
