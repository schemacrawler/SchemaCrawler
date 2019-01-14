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


import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class AdditionalCasesTest
  extends BaseSqliteTest
{

  @Test
  public void advancedUsage(final TestInfo testInfo)
    throws Exception
  {
    run(currentMethodFullName(testInfo), "/advanced_usage.sql", "details");
  }

  @Test
  public void quotedCreateScript(final TestInfo testInfo)
    throws Exception
  {
    run(currentMethodFullName(testInfo), "/identifiers_unquoted.sql", "schema");
    run(currentMethodFullName(testInfo), "/identifiers_quoted.sql", "schema");
  }

  private void run(final String currentMethodFullName,
                   final String databaseSqlResource,
                   final String command)
    throws Exception
  {
    final Path sqliteDbFile = createTestDatabase(databaseSqlResource);

    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", sqliteDbFile.toString());

    final SchemaCrawlerOptions options = schemaCrawlerOptionsWithMaximumSchemaInfoLevel();

    final SchemaTextOptions textOptions = SchemaTextOptionsBuilder
      .newSchemaTextOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
      .builder(textOptions).toConfig());

    executableExecution(createConnection(sqliteDbFile),
                      executable,
                      TextOutputFormat.text,
                      currentMethodFullName);
  }

}
