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


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.createTempFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class LintOutputTest
  extends BaseDatabaseTest
{

  private static final String TEXT_OUTPUT = "lint_text_output/";
  private static final String COMPOSITE_OUTPUT = "lint_composite_output/";
  private static final String JSON_OUTPUT = "lint_json_output/";

  @Test
  public void compareCompositeOutput()
    throws Exception
  {
    clean(COMPOSITE_OUTPUT);

    final String queryCommand1 = "dump_top5";
    final Config queriesConfig = new Config();
    queriesConfig
      .put(queryCommand1,
           "SELECT TOP 5 ${orderbycolumns} FROM ${table} ORDER BY ${orderbycolumns}");

    final String[] commands = new String[] {
                                             SchemaTextDetailType.brief + ","
                                             + Operation.count + ","
                                             + "lint", queryCommand1 + ","
                                                       + SchemaTextDetailType.brief
                                                       + "," + "lint",
    };

    final List<String> failures = new ArrayList<>();
    for (final OutputFormat outputFormat: EnumSet
      .complementOf(EnumSet.of(TextOutputFormat.tsv)))
    {
      for (final String command: commands)
      {
        final String referenceFile = command + "." + outputFormat.getFormat();

        final Path testOutputFile = createTempFile(referenceFile,
                                                   outputFormat.getFormat());

        final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                              testOutputFile);

        final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
        schemaCrawlerOptions
          .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*FOR_LINT"));
        schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

        final Executable executable = new SchemaCrawlerExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        executable.setAdditionalConfiguration(queriesConfig);
        executable.execute(getConnection());

        failures
          .addAll(compareOutput(COMPOSITE_OUTPUT + referenceFile,
                                testOutputFile, outputFormat.getFormat()));
      }
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareJsonOutput()
    throws Exception
  {
    clean(JSON_OUTPUT);

    final InfoLevel infoLevel = InfoLevel.standard;
    try (
      final TestWriter out = new TestWriter(TextOutputFormat.json.getFormat());)
    {
      final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.json,
                                                            out);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*FOR_LINT"));
      schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.buildSchemaInfoLevel());

      final Executable executable = new SchemaCrawlerExecutable("lint");
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.execute(getConnection());

      out.assertEquals(JSON_OUTPUT + "lints.json");
    }
  }

  @Test
  public void compareTextOutput()
    throws Exception
  {
    clean(TEXT_OUTPUT);

    final InfoLevel infoLevel = InfoLevel.standard;
    try (
      final TestWriter out = new TestWriter(TextOutputFormat.text.getFormat());)
    {
      final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.text,
                                                            out);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*FOR_LINT"));
      schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.buildSchemaInfoLevel());

      final Executable executable = new SchemaCrawlerExecutable("lint");
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.execute(getConnection());

      out.assertEquals(TEXT_OUTPUT + "lint.txt");
    }
  }

}
