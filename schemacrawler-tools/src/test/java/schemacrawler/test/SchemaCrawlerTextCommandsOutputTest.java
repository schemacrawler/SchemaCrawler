/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static schemacrawler.test.utility.TestUtility.clean;

import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.base.CommonTextOptionsBuilder;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class SchemaCrawlerTextCommandsOutputTest
  extends BaseDatabaseTest
{

  private static final String COMMAND_OUTPUT = "command_output/";

  @BeforeClass
  public static void before()
    throws Exception
  {
    clean(COMMAND_OUTPUT);
  }

  @Test
  public void countOutput()
    throws Exception
  {
    testOperationOutput(Operation.count);
  }

  @Test
  public void dumpOutput()
    throws Exception
  {
    testOperationOutput(Operation.dump);
  }

  @Test
  public void queryOutput()
    throws Exception
  {
    final String queryCommand = "all_tables";
    final Config config = new Config();
    config
      .put(queryCommand,
           "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES ORDER BY TABLE_SCHEM, TABLE_NAME");

    textOutputTest(queryCommand, config);
  }

  @Test
  public void queryOverOutput()
    throws Exception
  {
    final String queryCommand = "dump_tables";
    final Config config = new Config();
    config
      .put(queryCommand,
           "SELECT ${orderbycolumns} FROM ${table} ORDER BY ${orderbycolumns}");

    textOutputTest(queryCommand, config);
  }

  @Test
  public void quickdumpOutput()
    throws Exception
  {
    testOperationOutput(Operation.quickdump);
  }

  @Test
  public void schemaOutput()
    throws Exception
  {
    textOutputTest(SchemaTextDetailType.brief.name(), new Config());
  }

  @Test
  public void sortedColumnsOutput()
    throws Exception
  {
    final String queryCommand = "dump_tables_sorted_columns";
    final Config config = new Config();
    config.put("schemacrawler.format.sort_alphabetically.table_columns",
               Boolean.TRUE.toString());
    config.put(queryCommand,
               "SELECT ${columns} FROM ${table} ORDER BY ${orderbycolumns}");

    textOutputTest(queryCommand, config);
  }

  @Test
  public void streamedOutput()
    throws Exception
  {
    textOutputTest(SchemaTextDetailType.brief.name(), new Config());
  }

  private void testOperationOutput(final Operation operation)
    throws Exception
  {
    textOutputTest(operation.name(), new Config());
  }

  private void textOutputTest(final String command, final Config config)
    throws Exception
  {
    try (final TestWriter writer = new TestWriter(TextOutputFormat.text
      .getFormat());)
    {
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));

      final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.text,
                                                            writer);

      final CommonTextOptionsBuilder commonTextOptions = new CommonTextOptionsBuilder();
      commonTextOptions.fromConfig(config);
      commonTextOptions.noInfo(true);
      commonTextOptions.noHeader(true);
      commonTextOptions.noFooter(true);
      commonTextOptions.sortTables(true);
      config.putAll(commonTextOptions.toConfig());

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(config);
      executable.setOutputOptions(outputOptions);
      executable.execute(getConnection());

      writer.assertEquals(COMMAND_OUTPUT + command + ".txt");
    }
  }

}
