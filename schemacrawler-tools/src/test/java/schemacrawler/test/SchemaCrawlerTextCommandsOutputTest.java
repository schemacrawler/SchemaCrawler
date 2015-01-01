/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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

package schemacrawler.test;


import static java.nio.file.Files.exists;
import static org.junit.Assert.assertTrue;
import static schemacrawler.test.utility.TestUtility.createTempFile;

import java.nio.file.Path;

import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class SchemaCrawlerTextCommandsOutputTest
  extends BaseDatabaseTest
{

  private static final String COMMAND_OUTPUT = "command_output/";

  @Test
  public void countOutput()
    throws Exception
  {
    textOutputTest(Operation.count.name(), new Config());
  }

  @Test
  public void dumpOutput()
    throws Exception
  {
    textOutputTest(Operation.dump.name(), new Config());
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
    final Path dummyOutputFile = createTempFile("dummy", "data");
    try (final TestWriter writer = new TestWriter(TextOutputFormat.text.getFormat());)
    {
      final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.text,
                                                            dummyOutputFile);
      outputOptions.setWriter(writer);

      final BaseTextOptionsBuilder baseTextOptions = new BaseTextOptionsBuilder();
      baseTextOptions.hideInfo();
      baseTextOptions.hideHeader();
      baseTextOptions.hideFooter();

      final String command = SchemaTextDetailType.brief.name();
      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      executable.setOutputOptions(outputOptions);
      executable.setAdditionalConfiguration(baseTextOptions.toConfig());
      executable.execute(getConnection());

      assertTrue(!exists(dummyOutputFile));

      writer.assertEquals(COMMAND_OUTPUT + command + ".txt");
    }
  }

  private void textOutputTest(final String command, final Config config)
    throws Exception
  {
    try (final TestWriter writer = new TestWriter(TextOutputFormat.text.getFormat());)
    {
      final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.text,
                                                            writer);

      final BaseTextOptionsBuilder baseTextOptions = new BaseTextOptionsBuilder(config);
      baseTextOptions.hideInfo();
      baseTextOptions.hideHeader();
      baseTextOptions.hideFooter();
      config.putAll(baseTextOptions.toConfig());

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      executable.setAdditionalConfiguration(config);
      executable.setOutputOptions(outputOptions);
      executable.execute(getConnection());

      writer.assertEquals(COMMAND_OUTPUT + command + ".txt");
    }
  }
}
