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

package schemacrawler.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.clean;

import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.text.base.CommonTextOptionsBuilder;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class SchemaCrawlerTextCommandsOutputTest
{

  private static final String COMMAND_OUTPUT = "command_output/";

  @BeforeAll
  public static void before()
    throws Exception
  {
    clean(COMMAND_OUTPUT);
  }

  @Test
  public void queryOutput(final Connection connection)
    throws Exception
  {
    final String queryCommand = "all_tables";
    final Config config = new Config();
    config.put(queryCommand,
               "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES ORDER BY TABLE_SCHEM, TABLE_NAME");

    textOutputTest(queryCommand, connection, config);
  }

  @Test
  public void queryOverOutput(final Connection connection)
    throws Exception
  {
    final String queryCommand = "dump_tables";
    final Config config = new Config();
    config.put(queryCommand,
               "SELECT ${orderbycolumns} FROM ${table} ORDER BY ${orderbycolumns}");

    textOutputTest(queryCommand, connection, config);
  }

  @Test
  public void operationOutput(final Connection connection)
    throws Exception
  {
    for (final Operation operation : Operation.values())
    {
      textOutputTest(operation.name(), connection, new Config());
    }
  }

  @Test
  public void schemaTextOutput(final Connection connection)
    throws Exception
  {
    for (final SchemaTextDetailType schemaTextDetailType : SchemaTextDetailType.values())
    {
      textOutputTest(schemaTextDetailType.name(), connection, new Config());
    }
  }

  @Test
  public void sortedColumnsOutput(final Connection connection)
    throws Exception
  {
    final String queryCommand = "dump_tables_sorted_columns";
    final Config config = new Config();
    config.put("schemacrawler.format.sort_alphabetically.table_columns",
               Boolean.TRUE.toString());
    config.put(queryCommand,
               "SELECT ${columns} FROM ${table} ORDER BY ${orderbycolumns}");

    textOutputTest(queryCommand, connection, config);
  }

  private void textOutputTest(final String command,
                              final Connection connection,
                              final Config config)
    throws Exception
  {
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder
      .builder()
      .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
      .includeAllRoutines();
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      SchemaCrawlerOptionsBuilder
        .builder()
        .withLimitOptionsBuilder(limitOptionsBuilder);
    final SchemaCrawlerOptions schemaCrawlerOptions =
      schemaCrawlerOptionsBuilder.toOptions();

    final CommonTextOptionsBuilder commonTextOptions =
      CommonTextOptionsBuilder.builder();
    commonTextOptions.fromConfig(config);
    commonTextOptions.noInfo();
    commonTextOptions.noHeader();
    commonTextOptions.noFooter();
    commonTextOptions.sortTables(true);
    config.putAll(commonTextOptions.toConfig());

    final SchemaCrawlerExecutable executable =
      new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);

    assertThat(outputOf(executableExecution(connection, executable)),
               hasSameContentAs(classpathResource(COMMAND_OUTPUT
                                                  + command
                                                  + ".txt")));
  }

}
