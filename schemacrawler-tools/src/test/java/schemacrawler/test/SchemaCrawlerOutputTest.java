/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.BaseTextOptionsBuilder;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;

public class SchemaCrawlerOutputTest
  extends BaseDatabaseTest
{

  private static final String INFO_LEVEL_OUTPUT = "info_level_output/";
  private static final String COMPOSITE_OUTPUT = "composite_output/";
  private static final String ORDINAL_OUTPUT = "ordinal_output/";
  private static final String JSON_OUTPUT = "json_output/";
  private static final String HIDE_CONSTRAINT_NAMES_OUTPUT = "hide_constraint_names_output/";
  private static final String UNQUALIFIED_NAMES_OUTPUT = "unqualified_names_output/";
  private static final String ROUTINES_OUTPUT = "routines_output/";

  @Test
  public void compareCompositeOutput()
    throws Exception
  {
    FileUtils.deleteDirectory(new File("./target/unit_tests_results_output",
                                       COMPOSITE_OUTPUT));

    final String queryCommand1 = "all_tables";
    final Config queriesConfig = new Config();
    queriesConfig
      .put(queryCommand1,
           "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES ORDER BY TABLE_SCHEM, TABLE_NAME");
    final String queryCommand2 = "dump_tables";
    queriesConfig
      .put(queryCommand2,
           "SELECT ${orderbycolumns} FROM ${table} ORDER BY ${orderbycolumns}");

    final BaseTextOptionsBuilder baseTextOptions = new BaseTextOptionsBuilder();
    queriesConfig.putAll(baseTextOptions.toConfig());

    final String[] commands = new String[] {
        SchemaTextDetailType.details + "," + Operation.count + ","
            + Operation.dump,
        SchemaTextDetailType.list + "," + Operation.count,
        queryCommand1 + "," + queryCommand2 + "," + Operation.count + ","
            + SchemaTextDetailType.list,
    };

    final List<String> failures = new ArrayList<String>();
    for (final OutputFormat outputFormat: EnumSet.complementOf(EnumSet
      .of(OutputFormat.tsv)))
    {
      for (final String command: commands)
      {
        final String referenceFile = command + "." + outputFormat.name();

        final File testOutputFile = File.createTempFile("schemacrawler."
                                                            + referenceFile
                                                            + ".",
                                                        ".test");
        testOutputFile.delete();

        final OutputOptions outputOptions = new OutputOptions(outputFormat.name(),
                                                              testOutputFile);

        final Config config = Config
          .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
        final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
        schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

        final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        executable.setAdditionalConfiguration(queriesConfig);
        executable.execute(getConnection());

        failures.addAll(compareOutput(COMPOSITE_OUTPUT + referenceFile,
                                      testOutputFile,
                                      outputFormat.name()));
      }
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareHideConstraintNamesOutput()
    throws Exception
  {

    FileUtils.deleteDirectory(new File("./target/unit_tests_results_output",
                                       HIDE_CONSTRAINT_NAMES_OUTPUT));

    final List<String> failures = new ArrayList<String>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setHidePrimaryKeyNames(true);
    textOptions.setHideForeignKeyNames(true);
    textOptions.setHideIndexNames(true);
    textOptions.setHideConstraintNames(true);

    for (final OutputFormat outputFormat: EnumSet.complementOf(EnumSet
      .of(OutputFormat.tsv)))
    {
      final String referenceFile = "details_maximum." + outputFormat.name();

      final File testOutputFile = File
        .createTempFile("schemacrawler." + referenceFile + ".", ".test");
      testOutputFile.delete();

      final OutputOptions outputOptions = new OutputOptions(outputFormat.name(),
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
                                                                             + ","
                                                                             + Operation.count
                                                                             + ","
                                                                             + Operation.dump);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.setAdditionalConfiguration(textOptions.toConfig());
      executable.execute(getConnection());

      failures.addAll(compareOutput(HIDE_CONSTRAINT_NAMES_OUTPUT
                                        + referenceFile,
                                    testOutputFile,
                                    outputFormat.name()));
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareInfoLevelOutput()
    throws Exception
  {
    FileUtils.deleteDirectory(new File("./target/unit_tests_results_output",
                                       INFO_LEVEL_OUTPUT));

    final List<String> failures = new ArrayList<String>();
    for (final InfoLevel infoLevel: InfoLevel.values())
    {
      if (infoLevel == InfoLevel.unknown)
      {
        continue;
      }
      for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
        .values())
      {
        final String referenceFile = schemaTextDetailType + "_" + infoLevel
                                     + ".txt";

        final File testOutputFile = File.createTempFile("schemacrawler."
                                                            + referenceFile
                                                            + ".",
                                                        ".test");
        testOutputFile.delete();

        final OutputOptions outputOptions = new OutputOptions(OutputFormat.text.name(),
                                                              testOutputFile);

        final Config config = Config
          .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
        final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
        schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.getSchemaInfoLevel());

        final Executable executable = new SchemaCrawlerExecutable(schemaTextDetailType
          .name());
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        executable.execute(getConnection());

        failures.addAll(compareOutput(INFO_LEVEL_OUTPUT + referenceFile,
                                      testOutputFile,
                                      outputOptions.getOutputFormat().name()));
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
    FileUtils.deleteDirectory(new File("./target/unit_tests_results_output",
                                       JSON_OUTPUT));

    final List<String> failures = new ArrayList<String>();
    final InfoLevel infoLevel = InfoLevel.maximum;
    for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
      .values())
    {
      final String referenceFile = schemaTextDetailType + "_" + infoLevel
                                   + ".json";

      final File testOutputFile = File
        .createTempFile("schemacrawler." + referenceFile + ".", ".test");
      testOutputFile.delete();

      final OutputOptions outputOptions = new OutputOptions(OutputFormat.json.name(),
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
      schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.getSchemaInfoLevel());

      final Executable executable = new SchemaCrawlerExecutable(schemaTextDetailType
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.execute(getConnection());

      failures.addAll(compareOutput(JSON_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputOptions.getOutputFormat().name()));
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareOrdinalOutput()
    throws Exception
  {
    FileUtils.deleteDirectory(new File("./target/unit_tests_results_output",
                                       ORDINAL_OUTPUT));

    final List<String> failures = new ArrayList<String>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setShowOrdinalNumbers(true);

    for (final OutputFormat outputFormat: EnumSet.complementOf(EnumSet
      .of(OutputFormat.tsv)))
    {
      final String referenceFile = "details_maximum." + outputFormat.name();

      final File testOutputFile = File
        .createTempFile("schemacrawler." + referenceFile + ".", ".test");
      testOutputFile.delete();

      final OutputOptions outputOptions = new OutputOptions(outputFormat.name(),
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
                                                                             + ","
                                                                             + Operation.count
                                                                             + ","
                                                                             + Operation.dump);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.setAdditionalConfiguration(textOptions.toConfig());
      executable.execute(getConnection());

      failures.addAll(compareOutput(ORDINAL_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.name()));
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareRoutinesOutput()
    throws Exception
  {
    FileUtils.deleteDirectory(new File("./target/unit_tests_results_output",
                                       ROUTINES_OUTPUT));

    final List<String> failures = new ArrayList<String>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setShowUnqualifiedNames(true);

    for (final OutputFormat outputFormat: EnumSet.complementOf(EnumSet
      .of(OutputFormat.tsv)))
    {
      final String referenceFile = "routines." + outputFormat.name();

      final File testOutputFile = File
        .createTempFile("schemacrawler." + referenceFile + ".", ".test");
      testOutputFile.delete();

      final OutputOptions outputOptions = new OutputOptions(outputFormat.name(),
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
      schemaCrawlerOptions.setTableInclusionRule(InclusionRule.EXCLUDE_ALL);
      schemaCrawlerOptions.setRoutineInclusionRule(InclusionRule.INCLUDE_ALL);
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.setAdditionalConfiguration(textOptions.toConfig());
      executable.execute(getConnection());

      failures.addAll(compareOutput(ROUTINES_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.name()));
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareUnqualifiedNamesOutput()
    throws Exception
  {
    FileUtils.deleteDirectory(new File("./target/unit_tests_results_output",
                                       UNQUALIFIED_NAMES_OUTPUT));

    final List<String> failures = new ArrayList<String>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setShowUnqualifiedNames(true);

    for (final OutputFormat outputFormat: EnumSet.complementOf(EnumSet
      .of(OutputFormat.tsv)))
    {
      final String referenceFile = "details_maximum." + outputFormat.name();

      final File testOutputFile = File
        .createTempFile("schemacrawler." + referenceFile + ".", ".test");
      testOutputFile.delete();

      final OutputOptions outputOptions = new OutputOptions(outputFormat.name(),
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
                                                                             + ","
                                                                             + Operation.count
                                                                             + ","
                                                                             + Operation.dump);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.setAdditionalConfiguration(textOptions.toConfig());
      executable.execute(getConnection());

      failures.addAll(compareOutput(UNQUALIFIED_NAMES_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.name()));
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
