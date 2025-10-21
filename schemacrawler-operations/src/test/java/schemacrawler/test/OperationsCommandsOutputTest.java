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
import static us.fatehi.test.utility.TestUtility.clean;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class OperationsCommandsOutputTest {

  private static final String OPERATION_COMMAND_OUTPUT = "operation_command_output/";

  @BeforeAll
  public static void cleanAll() throws Exception {
    clean(OPERATION_COMMAND_OUTPUT);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sortedColumnsOutput(final DatabaseConnectionSource dataSource) throws Exception {
    final String queryCommand = "dump_tables_sorted_columns";
    final Config config = new Config();
    config.put("schemacrawler.format.sort_alphabetically.table_columns", Boolean.TRUE.toString());
    config.put(queryCommand, "SELECT ${columns} FROM ${table} ORDER BY ${basiccolumns}");

    textOutputTest(queryCommand, dataSource, config);
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
    config.put(queryCommand, "SELECT ${basiccolumns} FROM ${table} ORDER BY ${basiccolumns}");

    textOutputTest(queryCommand, dataSource, config);
  }

  @ParameterizedTest
  @EnumSource(
      value = OperationType.class,
      mode = Mode.EXCLUDE,
      names = {"tablesample"})
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void operationOutput(
      final OperationType operation, final DatabaseConnectionSource dataSource) throws Exception {
    textOutputTest(operation.name(), dataSource, new Config());
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
        hasSameContentAs(classpathResource(OPERATION_COMMAND_OUTPUT + command + ".txt")));
  }
}
