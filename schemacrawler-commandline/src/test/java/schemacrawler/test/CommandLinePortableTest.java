/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@WithTestDatabase
@ResolveTestContext
public class CommandLinePortableTest {

  private static final String COMMAND_LINE_PORTABLE_OUTPUT = "command_line_portable_output/";

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
            classpathResource(
                COMMAND_LINE_PORTABLE_OUTPUT + testContext.testMethodName() + extension)));
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
  public void commandLinePortableBroad(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--portable", PortableType.broad.name());
    argsMap.put("--tables", ".*");
    argsMap.put("--routines", ".*");
    argsMap.put("--sequences", ".*");
    argsMap.put("--synonyms", ".*");

    run(testContext, connectionInfo, argsMap, "schema");
  }

  @Test
  public void commandLinePortableNames(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--portable", PortableType.names.name());
    argsMap.put("--tables", ".*");
    argsMap.put("--routines", ".*");
    argsMap.put("--sequences", ".*");
    argsMap.put("--synonyms", ".*");

    run(testContext, connectionInfo, argsMap, "schema");
  }

  @Test
  public void commandLinePortableOverridesConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--no-remarks", Boolean.FALSE.toString());
    argsMap.put("--portable", PortableType.none.name());

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.hide_remarks", Boolean.TRUE.toString());
    config.put("schemacrawler.format.hide_primarykey_names", Boolean.TRUE.toString());
    config.put("schemacrawler.format.hide_foreignkey_names", Boolean.TRUE.toString());
    config.put("schemacrawler.format.show_unqualified_names", Boolean.TRUE.toString());
    config.put("schemacrawler.format.show_standard_column_type_names", Boolean.TRUE.toString());
    config.put("schemacrawler.format.show_ordinal_numbers", Boolean.TRUE.toString());

    run(testContext, connectionInfo, argsMap, config, "schema");
  }

  @Test
  public void commandLineWithSomePortableNames1(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    // argsMap.put("--portable", PortableType.none.name());

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.hide_primarykey_names", Boolean.TRUE.toString());

    run(testContext, connectionInfo, argsMap, config, "brief");
  }

  @Test
  public void commandLineWithSomePortableNames2(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    // argsMap.put("--portable", PortableType.none.name());

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.hide_foreignkey_names", Boolean.TRUE.toString());

    run(testContext, connectionInfo, argsMap, config, "brief");
  }
}
