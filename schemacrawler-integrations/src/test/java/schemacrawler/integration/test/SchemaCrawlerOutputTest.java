/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import schemacrawler.utility.IdentifierQuotingStrategy;
import sf.util.IOUtility;

public class SchemaCrawlerOutputTest
  extends BaseDatabaseTest
{

  private static final String COMPOSITE_OUTPUT = "composite_output/";
  private static final String ORDINAL_OUTPUT = "ordinal_output/";
  private static final String TABLE_ROW_COUNT_OUTPUT = "table_row_count_output/";
  private static final String SHOW_WEAK_ASSOCIATIONS_OUTPUT = "show_weak_associations_output/";
  private static final String JSON_OUTPUT = "json_output/";
  private static final String HIDE_CONSTRAINT_NAMES_OUTPUT = "hide_constraint_names_output/";
  private static final String UNQUALIFIED_NAMES_OUTPUT = "unqualified_names_output/";
  private static final String ROUTINES_OUTPUT = "routines_output/";
  private static final String NO_REMARKS_OUTPUT = "no_remarks_output/";
  private static final String NO_SCHEMA_COLORS_OUTPUT = "no_schema_colors_output/";
  private static final String IDENTIFIER_QUOTING_OUTPUT = "identifier_quoting_output/";

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

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    final List<String> failures = new ArrayList<>();
    for (final OutputFormat outputFormat: getOutputFormats())
    {
      for (final String command: commands)
      {
        final String referenceFile = command + "." + outputFormat.getFormat();

        final Path testOutputFile = IOUtility
          .createTempFilePath(referenceFile, outputFormat.getFormat());

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
        schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
        schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

        queriesConfig
          .putAll(new SchemaTextOptionsBuilder(textOptions).toConfig());

        final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        executable.setAdditionalConfiguration(queriesConfig);
        executable.execute(getConnection(),
                           databaseSpecificOverrideOptionsBuilder.toOptions());

        failures.addAll(compareOutput(COMPOSITE_OUTPUT + referenceFile,
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
    textOptions.setNoHeader(false);
    textOptions.setNoFooter(false);
    textOptions.setHidePrimaryKeyNames(true);
    textOptions.setHideForeignKeyNames(true);
    textOptions.setHideIndexNames(true);
    textOptions.setHideConstraintNames(true);
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

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
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

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
  public void compareIdentifierQuotingOutput()
    throws Exception
  {
    clean(IDENTIFIER_QUOTING_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setHideRemarks(true);
    textOptions.setNoSchemaCrawlerInfo(true);
    textOptions.setShowDatabaseInfo(false);
    textOptions.setShowJdbcDriverInfo(false);

    for (final IdentifierQuotingStrategy identifierQuotingStrategy: IdentifierQuotingStrategy
      .values())
    {
      final OutputFormat outputFormat = TextOutputFormat.text;
      textOptions.setIdentifierQuotingStrategy(identifierQuotingStrategy);

      final String referenceFile = "schema_" + identifierQuotingStrategy.name()
                                   + "." + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*\\.BOOKS"));
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.schema
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(new SchemaTextOptionsBuilder(textOptions)
          .toConfig());
      executable.execute(getConnection());

      failures.addAll(compareOutput(IDENTIFIER_QUOTING_OUTPUT + referenceFile,
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

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    final List<String> failures = new ArrayList<>();
    final InfoLevel infoLevel = InfoLevel.maximum;
    for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
      .values())
    {
      final String referenceFile = schemaTextDetailType + "_" + infoLevel
                                   + ".json";

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, TextOutputFormat.json.getFormat());

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
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);

      final Executable executable = new SchemaCrawlerExecutable(schemaTextDetailType
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
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
    textOptions.setHideRemarks(true);
    textOptions.setNoSchemaCrawlerInfo(true);
    textOptions.setShowDatabaseInfo(false);
    textOptions.setShowJdbcDriverInfo(false);

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "schema_detailed."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInfoLevel(SchemaInfoLevelBuilder.detailed());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*\\.BOOKS"));
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

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
  public void compareNoSchemaColorsOutput()
    throws Exception
  {
    clean(NO_SCHEMA_COLORS_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setHideRemarks(true);
    textOptions.setNoSchemaColors(true);
    textOptions.setNoSchemaCrawlerInfo(true);
    textOptions.setShowDatabaseInfo(false);
    textOptions.setShowJdbcDriverInfo(false);

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "schema_detailed."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*\\.BOOKS"));
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.schema
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(new SchemaTextOptionsBuilder(textOptions)
          .toConfig());
      executable.execute(getConnection());

      failures.addAll(compareOutput(NO_SCHEMA_COLORS_OUTPUT + referenceFile,
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
    textOptions.setShowOrdinalNumbers(true);
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

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
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

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
    textOptions.setShowUnqualifiedNames(true);
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "routines." + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

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
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setSequenceInclusionRule(new ExcludeAll());
      schemaCrawlerOptions.setSynonymInclusionRule(new ExcludeAll());
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

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
  public void compareShowWeakAssociationsOutput()
    throws Exception
  {
    clean(SHOW_WEAK_ASSOCIATIONS_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setShowWeakAssociations(true);
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "schema_standard."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.schema
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable.execute(getConnection());

      failures
        .addAll(compareOutput(SHOW_WEAK_ASSOCIATIONS_OUTPUT + referenceFile,
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
    textOptions.setShowRowCounts(true);
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            testOutputFile);

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

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
    textOptions.setShowUnqualifiedNames(true);
    textOptions.setNoSchemaCrawlerInfo(false);
    textOptions.setShowDatabaseInfo(true);
    textOptions.setShowJdbcDriverInfo(true);

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

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
      schemaCrawlerOptions.setRoutineInclusionRule(new IncludeAll());
      schemaCrawlerOptions.setRoutineColumnInclusionRule(new IncludeAll());

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = new SchemaTextOptionsBuilder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

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

  private Set<OutputFormat> getOutputFormats()
  {
    final Set<OutputFormat> outputFormats = new HashSet<>();
    outputFormats
      .addAll(EnumSet.complementOf(EnumSet.of(TextOutputFormat.tsv)));
    outputFormats.add(GraphOutputFormat.scdot);

    return outputFormats;
  }

}
