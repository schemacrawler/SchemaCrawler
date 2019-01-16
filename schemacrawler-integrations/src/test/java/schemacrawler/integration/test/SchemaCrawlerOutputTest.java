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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.ExecutableTestUtility.outputFileOf;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import schemacrawler.utility.IdentifierQuotingStrategy;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class SchemaCrawlerOutputTest
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
  public void compareCompositeOutput(final Connection connection)
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

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noSchemaCrawlerInfo(false).showDatabaseInfo()
      .showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    assertAll(getOutputFormats().stream()
      .flatMap(outputFormat -> Arrays.stream(commands).map(command -> () -> {

        final String referenceFile = command + "." + outputFormat.getFormat();

        final Config config = loadHsqldbConfig();

        final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
          .builder().fromConfig(config);

        final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
          .builder()
          .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
          .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
          .includeAllSequences().includeAllRoutines();
        final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
          .toOptions();

        queriesConfig
          .putAll(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

        final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setAdditionalConfiguration(queriesConfig);
        executable
          .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());

        assertThat(outputFileOf(executableExecution(connection,
                                                    executable,
                                                    outputFormat)),
                   hasSameContentAndTypeAs(classpathResource(COMPOSITE_OUTPUT
                                                             + referenceFile),
                                           outputFormat));
      })));
  }

  @Test
  public void compareHideConstraintNamesOutput(final Connection connection)
    throws Exception
  {
    clean(HIDE_CONSTRAINT_NAMES_OUTPUT);

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noHeader(false).noFooter(false)
      .noSchemaCrawlerInfo(false).showDatabaseInfo(true)
      .showJdbcDriverInfo(true).noPrimaryKeyNames().noForeignKeyNames()
      .noIndexNames().noConstraintNames();
    textOptionsBuilder.noConstraintNames();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    assertAll(getOutputFormats().stream().map(outputFormat -> () -> {

      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
        .builder().fromConfig(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
        .includeAllSequences().includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
        .builder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

      final String command = String.format("%s,%s,%s",
                                           SchemaTextDetailType.details,
                                           Operation.count,
                                           Operation.dump);
      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable
        .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());

      assertThat(outputFileOf(executableExecution(connection,
                                                  executable,
                                                  outputFormat)),
                 hasSameContentAndTypeAs(classpathResource(HIDE_CONSTRAINT_NAMES_OUTPUT
                                                           + referenceFile),
                                         outputFormat));
    }));
  }

  @Test
  public void compareIdentifierQuotingOutput(final Connection connection)
    throws Exception
  {
    clean(IDENTIFIER_QUOTING_OUTPUT);

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noRemarks().noSchemaCrawlerInfo().showDatabaseInfo(false)
      .showJdbcDriverInfo(false);

    assertAll(Arrays.stream(IdentifierQuotingStrategy.values())
      .map(identifierQuotingStrategy -> () -> {

        final OutputFormat outputFormat = TextOutputFormat.text;
        textOptionsBuilder
          .withIdentifierQuotingStrategy(identifierQuotingStrategy);
        final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

        final String referenceFile = "schema_"
                                     + identifierQuotingStrategy.name() + "."
                                     + outputFormat.getFormat();

        final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
          .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.standard())
          .includeSchemas(new RegularExpressionInclusionRule(".*\\.BOOKS"))
          .includeAllRoutines();
        final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
          .toOptions();

        final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.schema
          .name());
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
          .builder(textOptions).toConfig());

        assertThat(outputFileOf(executableExecution(connection,
                                                    executable,
                                                    outputFormat)),
                   hasSameContentAndTypeAs(classpathResource(IDENTIFIER_QUOTING_OUTPUT
                                                             + referenceFile),
                                           outputFormat));
      }));
  }

  @Test
  public void compareJsonOutput(final Connection connection)
    throws Exception
  {
    clean(JSON_OUTPUT);

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noSchemaCrawlerInfo(false).showDatabaseInfo()
      .showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final InfoLevel infoLevel = InfoLevel.maximum;

    assertAll(Arrays.stream(SchemaTextDetailType.values())
      .map(schemaTextDetailType -> () -> {

        final String referenceFile = schemaTextDetailType + "_" + infoLevel
                                     + ".json";
        final OutputFormat outputFormat = TextOutputFormat.json;

        final Config config = loadHsqldbConfig();

        final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
          .builder().fromConfig(config);

        final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
          .builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel())
          .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
          .includeAllSequences().includeAllRoutines();
        final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
          .toOptions();

        final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
          .builder(textOptions);

        final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(schemaTextDetailType
          .name());
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable
          .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
        executable
          .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());

        assertThat(outputFileOf(executableExecution(connection,
                                                    executable,
                                                    outputFormat)),
                   hasSameContentAndTypeAs(classpathResource(JSON_OUTPUT
                                                             + referenceFile),
                                           outputFormat));
      }));
  }

  @Test
  public void compareNoRemarksOutput(final Connection connection)
    throws Exception
  {
    clean(NO_REMARKS_OUTPUT);

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noRemarks().noSchemaCrawlerInfo().showDatabaseInfo(false)
      .showJdbcDriverInfo(false);
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    assertAll(getOutputFormats().stream().map(outputFormat -> () -> {

      final String referenceFile = "schema_detailed."
                                   + outputFormat.getFormat();

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.detailed())
        .includeSchemas(new RegularExpressionInclusionRule(".*\\.BOOKS"))
        .includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.schema
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
        .builder(textOptions).toConfig());

      assertThat(outputFileOf(executableExecution(connection,
                                                  executable,
                                                  outputFormat)),
                 hasSameContentAndTypeAs(classpathResource(NO_REMARKS_OUTPUT
                                                           + referenceFile),
                                         outputFormat));
    }));
  }

  @Test
  public void compareNoSchemaColorsOutput(final Connection connection)
    throws Exception
  {
    clean(NO_SCHEMA_COLORS_OUTPUT);

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noRemarks().noSchemaCrawlerInfo().showDatabaseInfo(false)
      .showJdbcDriverInfo(false).noSchemaColors();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    assertAll(getOutputFormats().stream().map(outputFormat -> () -> {

      final String referenceFile = "schema_detailed."
                                   + outputFormat.getFormat();

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.standard())
        .includeSchemas(new RegularExpressionInclusionRule(".*\\.BOOKS"))
        .includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.schema
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
        .builder(textOptions).toConfig());

      assertThat(outputFileOf(executableExecution(connection,
                                                  executable,
                                                  outputFormat)),
                 hasSameContentAndTypeAs(classpathResource(NO_SCHEMA_COLORS_OUTPUT
                                                           + referenceFile),
                                         outputFormat));
    }));
  }

  @Test
  public void compareOrdinalOutput(final Connection connection)
    throws Exception
  {
    clean(ORDINAL_OUTPUT);

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noSchemaCrawlerInfo(false).showDatabaseInfo()
      .showJdbcDriverInfo();
    textOptionsBuilder.showOrdinalNumbers();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    assertAll(getOutputFormats().stream().map(outputFormat -> () -> {

      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
        .builder().fromConfig(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
        .includeAllSequences().includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
        .builder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

      final String command = String.format("%s,%s,%s",
                                           SchemaTextDetailType.details,
                                           Operation.count,
                                           Operation.dump);
      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable
        .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());

      assertThat(outputFileOf(executableExecution(connection,
                                                  executable,
                                                  outputFormat)),
                 hasSameContentAndTypeAs(classpathResource(ORDINAL_OUTPUT
                                                           + referenceFile),
                                         outputFormat));
    }));
  }

  @Test
  public void compareRoutinesOutput(final Connection connection)
    throws Exception
  {
    clean(ROUTINES_OUTPUT);

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noSchemaCrawlerInfo(false).showDatabaseInfo()
      .showJdbcDriverInfo().showUnqualifiedNames();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    assertAll(getOutputFormats().stream().map(outputFormat -> () -> {

      final String referenceFile = "routines." + outputFormat.getFormat();

      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
        .builder().fromConfig(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder()
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
        .includeTables(new ExcludeAll()).includeAllRoutines()
        .includeSequences(new ExcludeAll()).includeSynonyms(new ExcludeAll())
        .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
        .builder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable
        .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());

      assertThat(outputFileOf(executableExecution(connection,
                                                  executable,
                                                  outputFormat)),
                 hasSameContentAndTypeAs(classpathResource(ROUTINES_OUTPUT
                                                           + referenceFile),
                                         outputFormat));
    }));
  }

  @Test
  public void compareShowWeakAssociationsOutput(final Connection connection)
    throws Exception
  {
    clean(SHOW_WEAK_ASSOCIATIONS_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noSchemaCrawlerInfo(false).showDatabaseInfo()
      .showJdbcDriverInfo();
    textOptionsBuilder.weakAssociations();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "schema_standard."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

      final OutputOptions outputOptions = OutputOptionsBuilder
        .newOutputOptions(outputFormat, testOutputFile);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.standard())
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
        .includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
        .builder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.schema
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable.setConnection(connection);
      executable.execute();

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
  public void compareTableRowCountOutput(final Connection connection)
    throws Exception
  {
    clean(TABLE_ROW_COUNT_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noSchemaCrawlerInfo(false).showDatabaseInfo()
      .showJdbcDriverInfo();
    textOptionsBuilder.showRowCounts();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

      final OutputOptions outputOptions = OutputOptionsBuilder
        .newOutputOptions(outputFormat, testOutputFile);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
        .includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
        .builder(textOptions);
      schemaTextOptionsBuilder.sortTables(true);

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(SchemaTextDetailType.details
        .name());
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable
        .setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
      executable.setConnection(connection);
      executable.execute();

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
  public void compareUnqualifiedNamesOutput(final Connection connection)
    throws Exception
  {
    clean(UNQUALIFIED_NAMES_OUTPUT);

    final List<String> failures = new ArrayList<>();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    textOptionsBuilder.noSchemaCrawlerInfo(false).showDatabaseInfo()
      .showJdbcDriverInfo().showUnqualifiedNames();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    for (final OutputFormat outputFormat: getOutputFormats())
    {
      final String referenceFile = "details_maximum."
                                   + outputFormat.getFormat();

      final Path testOutputFile = IOUtility
        .createTempFilePath(referenceFile, outputFormat.getFormat());

      final OutputOptions outputOptions = OutputOptionsBuilder
        .newOutputOptions(outputFormat, testOutputFile);

      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
        .builder().fromConfig(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
        .includeAllSequences().includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
        .builder(textOptions);
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
      executable.setConnection(connection);
      executable
        .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());
      executable.execute();

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
