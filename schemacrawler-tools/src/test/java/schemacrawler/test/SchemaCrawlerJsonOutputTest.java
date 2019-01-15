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

package schemacrawler.test;


import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class SchemaCrawlerJsonOutputTest
{

  private static final String JSON_EXTRA_OUTPUT = "json_extra_output/";

  @BeforeAll
  public static void cleanOutput()
    throws Exception
  {
    clean(JSON_EXTRA_OUTPUT);
  }

  private void jsonOutput(final TestContext testContext,
                          final Connection connection,
                          final InclusionRule tableInclusionRule,
                          final String tableName)
    throws Exception
  {

    final List<String> failures = new ArrayList<>();
    final InfoLevel infoLevel = InfoLevel.standard;
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final String referenceFile = testContext.testMethodName() + ".json";

    final Path testOutputFile = IOUtility
      .createTempFilePath(referenceFile, TextOutputFormat.json.getFormat());

    final OutputOptions outputOptions = OutputOptionsBuilder
      .newOutputOptions(TextOutputFormat.json, testOutputFile);

    final Config config = loadHsqldbConfig();

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
      .builder().fromConfig(config);

    final Config schemaTextOptions = SchemaTextOptionsBuilder.builder().noInfo()
      .toConfig();

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel())
      .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
      .includeRoutines(new ExcludeAll()).includeTables(tableInclusionRule)
      .tableNamePattern(tableName);
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(schemaTextDetailType
      .name());
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(schemaTextOptions);
    executable.setConnection(connection);
    executable
      .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());
    executable.execute();

    failures.addAll(compareOutput(JSON_EXTRA_OUTPUT + referenceFile,
                                  testOutputFile,
                                  outputOptions.getOutputFormatValue()));
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void noTableJsonOutput(final TestContext testContext,
                                final Connection connection)
    throws Exception
  {
    jsonOutput(testContext, connection, fullName -> false, "");
  }

  @Test
  public void singleTableJsonOutput(final TestContext testContext,
                                    final Connection connection)
    throws Exception
  {
    jsonOutput(testContext,
               connection,
               fullName -> fullName.contains("Counts"),
               "%Counts");
  }

}
