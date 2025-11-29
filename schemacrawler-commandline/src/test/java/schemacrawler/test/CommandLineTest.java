/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@WithTestDatabase
@ResolveTestContext
public class CommandLineTest {

  private static final String COMMAND_LINE_OUTPUT = "command_line_output/";

  private static void run(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final Map<String, String> argsMap,
      final Map<String, String> config,
      final String command)
      throws Exception {
    run(testContext, connectionInfo, argsMap, config, command, TextOutputFormat.text);
  }

  private static void run(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final Map<String, String> argsMap,
      final Map<String, String> config,
      final String command,
      final TextOutputFormat outputFormat)
      throws Exception {
    argsMap.put("--no-info", Boolean.TRUE.toString());
    argsMap.put("--schemas", ".*\\.(?!FOR_LINT).*");
    argsMap.put("--info-level", "maximum");

    final Map<String, Object> runConfig = new HashMap<>();
    final Map<String, String> informationSchema = loadHsqldbConfig();
    runConfig.putAll(informationSchema);
    if (config != null) {
      runConfig.putAll(config);
    }

    final String extension;
    switch (outputFormat) {
      case text:
        extension = ".txt";
        break;
      default:
        extension = "." + outputFormat.getFormat();
        break;
    }

    assertThat(
        outputOf(commandlineExecution(connectionInfo, command, argsMap, runConfig, outputFormat)),
        hasSameContentAs(
            classpathResource(COMMAND_LINE_OUTPUT + testContext.testMethodName() + extension)));
  }

  private static void run(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final Map<String, String> argsMap,
      final String command)
      throws Exception {
    run(testContext, connectionInfo, argsMap, null, command, TextOutputFormat.text);
  }

  @Test
  public void commandLineColorOverrides(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--tables", ".*");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.color_map.9900CC", ".*\\..*PUBLISHER.*");
    config.put("schemacrawler.format.color_map.FFFF00", ".*\\.BOOKS");

    run(testContext, connectionInfo, argsMap, config, "brief", TextOutputFormat.html);
  }

  @Test
  public void commandLineColumnExcludesWithConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.column.pattern.include", ".*");
    config.put("schemacrawler.column.pattern.exclude", ".*\\.ID");

    run(testContext, connectionInfo, argsMap, config, "schema");
  }

  @Test
  public void commandLineOverridesWithConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--tables", ".*");
    argsMap.put("--routines", ".*");
    argsMap.put("--sequences", ".*");
    argsMap.put("--synonyms", ".*");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.table.pattern.include", ".*");
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");
    config.put("schemacrawler.routine.pattern.include", ".*");
    config.put("schemacrawler.routine.pattern.exclude", ".*A.*");
    config.put("schemacrawler.sequence.pattern.include", ".*");
    config.put("schemacrawler.sequence.pattern.exclude", "");
    config.put("schemacrawler.synonym.pattern.include", ".*");
    config.put("schemacrawler.synonym.pattern.exclude", "");

    run(testContext, connectionInfo, argsMap, config, "brief");
  }

  @Test
  public void commandLineOverridesWithGrepConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--grep-columns", ".*BOOKS.ID");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.grep.column.pattern.include", ".*AUTHORS.ID");
    config.put("schemacrawler.grep.column.pattern.exclude", "");

    run(testContext, connectionInfo, argsMap, config, "brief");
  }

  @Test
  public void commandLineOverridesWithGrepConfigTables(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--grep-tables", ".*\\.BOOKS");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.grep.table.pattern.include", ".*AUTHORS");
    config.put("schemacrawler.grep.table.pattern.exclude", "");

    run(testContext, connectionInfo, argsMap, config, "list");
  }

  @Test
  public void commandLineRoutinesWithColumnsSorting(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--tables", "");
    argsMap.put("--routines", ".*");
    argsMap.put("--sort-columns", Boolean.TRUE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, "brief");
  }

  @Test
  public void commandLineRoutinesWithoutColumnsSorting(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--tables", "");
    argsMap.put("--routines", ".*");
    argsMap.put("--sort-columns", Boolean.FALSE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, "brief");
  }

  @Test
  public void commandLineRoutinesWithoutSorting(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--tables", "");
    argsMap.put("--routines", ".*");
    argsMap.put("--sort-routines", Boolean.FALSE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, "brief");
  }

  @Test
  public void commandLineRoutinesWithSorting(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--tables", "");
    argsMap.put("--routines", ".*");
    argsMap.put("--sort-routines", Boolean.TRUE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, "brief");
  }

  @Test
  public void commandLineTablesWithColumnsSorting(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--routines", "");
    argsMap.put("--sort-columns", Boolean.TRUE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, null, "brief");
  }

  @Test
  public void commandLineTablesWithoutColumnsSorting(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--routines", "");
    argsMap.put("--sort-columns", Boolean.FALSE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, "brief");
  }

  @Test
  public void commandLineTablesWithoutSorting(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--routines", "");
    argsMap.put("--sort-tables", Boolean.FALSE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, "brief");
  }

  @Test
  public void commandLineTablesWithSorting(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--routines", "");
    argsMap.put("--sort-tables", Boolean.TRUE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, null, "brief");
  }

  @Test
  public void commandLineWithConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_unqualified_names", Boolean.TRUE.toString());
    config.put("schemacrawler.table.pattern.include", ".*");
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");
    config.put("schemacrawler.routine.pattern.include", ".*");
    config.put("schemacrawler.routine.pattern.exclude", ".*A.*");
    config.put("schemacrawler.sequence.pattern.include", ".*");
    config.put("schemacrawler.sequence.pattern.exclude", "");
    config.put("schemacrawler.synonym.pattern.include", ".*");
    config.put("schemacrawler.synonym.pattern.exclude", "");

    run(testContext, connectionInfo, argsMap, config, "brief");
  }

  @Test
  public void commandLineWithDefaults(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    // Testing all tables, routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, argsMap, "brief");
  }

  @Test
  public void commandLineWithGrepConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.grep.column.pattern.include", ".*AUTHORS.ID");
    config.put("schemacrawler.grep.column.pattern.exclude", "");

    run(testContext, connectionInfo, argsMap, config, "brief");
  }

  @Test
  public void commandLineWithNonDefaults(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--tables", "");
    argsMap.put("--routines", ".*");
    argsMap.put("--sequences", ".*");
    argsMap.put("--synonyms", ".*");

    run(testContext, connectionInfo, argsMap, "brief");
  }

  @Test
  public void commandLineWithQueryInConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final String command = "query1";

    final Map<String, String> argsMap = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put(command, "SELECT * FROM BOOKS.Books");

    run(testContext, connectionInfo, argsMap, config, command);
  }

  @Test
  public void commandLineWithQueryOverInConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final String command = "query2";

    final Map<String, String> argsMap = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put(command, "SELECT ${columns} FROM ${table} ORDER BY ${columns}");

    run(testContext, connectionInfo, argsMap, config, command);
  }

  @Test
  public void commandLineWithQuoteOptionsConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.identifier_quoting_strategy", "quote_all");

    run(testContext, connectionInfo, argsMap, config, "brief");
  }

  @Test
  public void commandLineWithSortConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.sort_alphabetically.tables", Boolean.TRUE.toString());
    config.put("schemacrawler.format.sort_alphabetically.table_columns", Boolean.TRUE.toString());
    config.put(
        "schemacrawler.format.sort_alphabetically.table_foreignkeys", Boolean.TRUE.toString());
    config.put("schemacrawler.format.sort_alphabetically.table_indexes", Boolean.TRUE.toString());
    config.put("schemacrawler.format.sort_alphabetically.routines", Boolean.TRUE.toString());
    config.put("schemacrawler.format.sort_alphabetically.routine_columns", Boolean.TRUE.toString());

    run(testContext, connectionInfo, argsMap, config, "brief");
  }

  @Test
  public void commandLineWithTextShowOptionsConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.hide_remarks", Boolean.TRUE.toString());
    config.put("schemacrawler.format.show_unqualified_names", Boolean.TRUE.toString());
    config.put("schemacrawler.format.show_standard_column_type_names", Boolean.TRUE.toString());
    config.put("schemacrawler.format.show_ordinal_numbers", Boolean.TRUE.toString());

    run(testContext, connectionInfo, argsMap, config, "brief");
  }
}
