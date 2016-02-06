/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
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
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class SchemaCrawlerOutputTest
  extends BaseDatabaseTest
{

  private static final String COMPOSITE_OUTPUT = "composite_output/";
  private static final String ORDINAL_OUTPUT = "ordinal_output/";
  private static final String TABLE_ROW_COUNT_OUTPUT = "table_row_count_output/";
  private static final String JSON_OUTPUT = "json_output/";
  private static final String HIDE_CONSTRAINT_NAMES_OUTPUT = "hide_constraint_names_output/";
  private static final String UNQUALIFIED_NAMES_OUTPUT = "unqualified_names_output/";
  private static final String ROUTINES_OUTPUT = "routines_output/";
  private static final String NO_REMARKS_OUTPUT = "no_remarks_output/";

  @Test
  public void compareCompositeOutput()
    throws Exception
  {
    clean(COMPOSITE_OUTPUT);

    final String queryCommand1 = "all_tables";
    final Config queriesConfig = new Config();
    queriesConfig
      .put(queryCommand1,
           "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES ORDER BY TABLE_SCHEM, TABLE_NAME");
    final String queryCommand2 = "dump_tables";
    queriesConfig
      .put(queryCommand2,
           "SELECT ${orderbycolumns} FROM ${table} ORDER BY ${orderbycolumns}");

    final String[] commands = new String[] {
                                             SchemaTextDetailType.details + ","
                                             + Operation.count + ","
                                             + Operation.dump,
                                             SchemaTextDetailType.brief + ","
                                                               + Operation.count,
                                             queryCommand1 + "," + queryCommand2 + ","
                                                                                  + Operation.count
                                                                                  + ","
                                                                                  + SchemaTextDetailType.brief, };

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

        final Config config = Config
          .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");

        final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder()
          .fromConfig(config);

        final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
        schemaCrawlerOptions
          .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
        schemaCrawlerOptions
          .setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
        schemaCrawlerOptions.setSequenceInclusionRule(new IncludeAll());

        final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        executable.setAdditionalConfiguration(queriesConfig);
        executable.execute(getConnection(),
                           databaseSpecificOverrideOptionsBuilder.toOptions());

        failures
          .addAll(compareOutput(COMPOSITE_OUTPUT + referenceFile,
                                testOutputFile,
                                outputFormat.getFormat()));
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
    clean(HIDE_CONSTRAINT_NAMES_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setHidePrimaryKeyNames(true);
    textOptions.setHideForeignKeyNames(true);
    textOptions.setHideIndexNames(true);
    textOptions.setHideConstraintNames(true);

    for (final OutputFormat outputFormat: EnumSet
      .complementOf(EnumSet.of(TextOutputFormat.tsv)))
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = createTempFile(referenceFile,
                                                 outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");

      final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder()
        .fromConfig(config);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions.setSequenceInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
                                                                             + ","
                                                                             + Operation.count
                                                                             + ","
                                                                             + Operation.dump);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable.execute(getConnection(),
                         databaseSpecificOverrideOptionsBuilder.toOptions());

      failures
        .addAll(compareOutput(HIDE_CONSTRAINT_NAMES_OUTPUT + referenceFile,
                              testOutputFile,
                              outputFormat.getFormat()));
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

    final List<String> failures = new ArrayList<>();
    final InfoLevel infoLevel = InfoLevel.maximum;
    for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
      .values())
    {
      final String referenceFile = schemaTextDetailType + "_" + infoLevel
                                   + ".json";

      final Path testOutputFile = createTempFile(referenceFile,
                                                 TextOutputFormat.json
                                                   .getFormat());

      final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.json,
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");

      final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder()
        .fromConfig(config);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.buildSchemaInfoLevel());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions.setSequenceInclusionRule(new IncludeAll());

      final Executable executable = new SchemaCrawlerExecutable(schemaTextDetailType
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.execute(getConnection(),
                         databaseSpecificOverrideOptionsBuilder.toOptions());

      failures.addAll(compareOutput(JSON_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputOptions.getOutputFormatValue()));
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareNoRemarksOutput()
    throws Exception
  {
    clean(NO_REMARKS_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(true);
    textOptions.setHideRemarks(true);

    for (final OutputFormat outputFormat: EnumSet
      .complementOf(EnumSet.of(TextOutputFormat.tsv)))
    {
      final String referenceFile = "schema_detailed."
                                   + outputFormat.getFormat();

      final Path testOutputFile = createTempFile(referenceFile,
                                                 outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInfoLevel(SchemaInfoLevelBuilder.detailed());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*\\.BOOKS"));

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.schema
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(new SchemaTextOptionsBuilder(textOptions)
          .toConfig());
      executable.execute(getConnection());

      failures.addAll(compareOutput(NO_REMARKS_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.getFormat()));
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
    clean(ORDINAL_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setShowOrdinalNumbers(true);

    for (final OutputFormat outputFormat: EnumSet
      .complementOf(EnumSet.of(TextOutputFormat.tsv)))
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = createTempFile(referenceFile,
                                                 outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");

      final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder()
        .fromConfig(config);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions.setSequenceInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
                                                                             + ","
                                                                             + Operation.count
                                                                             + ","
                                                                             + Operation.dump);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable.execute(getConnection(),
                         databaseSpecificOverrideOptionsBuilder.toOptions());

      failures.addAll(compareOutput(ORDINAL_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.getFormat()));
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
    clean(ROUTINES_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setShowUnqualifiedNames(true);

    for (final OutputFormat outputFormat: EnumSet
      .complementOf(EnumSet.of(TextOutputFormat.tsv)))
    {
      final String referenceFile = "routines." + outputFormat.getFormat();

      final Path testOutputFile = createTempFile(referenceFile,
                                                 outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");

      final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder()
        .fromConfig(config);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions.setTableInclusionRule(new ExcludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setSequenceInclusionRule(new ExcludeAll());
      schemaCrawlerOptions.setSynonymInclusionRule(new ExcludeAll());
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable.execute(getConnection(),
                         databaseSpecificOverrideOptionsBuilder.toOptions());

      failures.addAll(compareOutput(ROUTINES_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.getFormat()));
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void compareTableRowCountOutput()
    throws Exception
  {
    clean(TABLE_ROW_COUNT_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setShowRowCounts(true);

    for (final OutputFormat outputFormat: EnumSet
      .complementOf(EnumSet.of(TextOutputFormat.tsv)))
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = createTempFile(referenceFile,
                                                 outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable.execute(getConnection());

      failures.addAll(compareOutput(TABLE_ROW_COUNT_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.getFormat()));
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
    clean(UNQUALIFIED_NAMES_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoInfo(false);
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setShowUnqualifiedNames(true);

    for (final OutputFormat outputFormat: EnumSet
      .complementOf(EnumSet.of(TextOutputFormat.tsv)))
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = createTempFile(referenceFile,
                                                 outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final Config config = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");

      final DatabaseSpecificOverrideOptionsBuilder databaseSpecificOverrideOptionsBuilder = new DatabaseSpecificOverrideOptionsBuilder()
        .fromConfig(config);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions.setSequenceInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
                                                                             + ","
                                                                             + Operation.count
                                                                             + ","
                                                                             + Operation.dump);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable.execute(getConnection(),
                         databaseSpecificOverrideOptionsBuilder.toOptions());

      failures.addAll(compareOutput(UNQUALIFIED_NAMES_OUTPUT + referenceFile,
                                    testOutputFile,
                                    outputFormat.getFormat()));
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
