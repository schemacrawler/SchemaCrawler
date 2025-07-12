/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.clean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class SchemaCrawlerTextCommandsOutputTest {

  private static final String COMMAND_OUTPUT = "command_output/";

  @BeforeAll
  public static void cleanAll() throws Exception {
    clean(COMMAND_OUTPUT);
  }

  @ParameterizedTest
  @EnumSource(OperationType.class)
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void operationOutput(
      final OperationType operation, final DatabaseConnectionSource dataSource) throws Exception {
    textOutputTest(operation.name(), dataSource, new Config());
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void queryOutput(final DatabaseConnectionSource dataSource) throws Exception {
    final String queryCommand = "all_tables";
    final Config config = new Config();
    config.put(
        queryCommand,
        "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES ORDER BY TABLE_SCHEM, TABLE_NAME");

    textOutputTest(queryCommand, dataSource, config);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void queryOverOutput(final DatabaseConnectionSource dataSource) throws Exception {
    final String queryCommand = "dump_tables";
    final Config config = new Config();
    config.put(queryCommand, "SELECT ${orderbycolumns} FROM ${table} ORDER BY ${orderbycolumns}");

    textOutputTest(queryCommand, dataSource, config);
  }

  @ParameterizedTest
  @EnumSource(SchemaTextDetailType.class)
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void schemaTextOutput(
      final SchemaTextDetailType schemaTextDetailType, final DatabaseConnectionSource dataSource)
      throws Exception {
    textOutputTest(schemaTextDetailType.name(), dataSource, new Config());
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sortedColumnsOutput(final DatabaseConnectionSource dataSource) throws Exception {
    final String queryCommand = "dump_tables_sorted_columns";
    final Config config = new Config();
    config.put("schemacrawler.format.sort_alphabetically.table_columns", Boolean.TRUE.toString());
    config.put(queryCommand, "SELECT ${columns} FROM ${table} ORDER BY ${orderbycolumns}");

    textOutputTest(queryCommand, dataSource, config);
  }

  private void textOutputTest(
      final String command, final DatabaseConnectionSource dataSource, final Config config)
      throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
            .includeAllRoutines();
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder commonTextOptions = SchemaTextOptionsBuilder.builder();
    commonTextOptions.fromConfig(config);
    commonTextOptions.noInfo();
    commonTextOptions.sortTables(true);
    config.merge(commonTextOptions.toConfig());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);

    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(COMMAND_OUTPUT + command + ".txt")));
  }
}
