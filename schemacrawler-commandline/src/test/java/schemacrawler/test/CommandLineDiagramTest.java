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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

@WithTestDatabase
@ResolveTestContext
public class CommandLineDiagramTest {

  private static final String COMMAND_LINE_DIAGRAM_OUTPUT = "command_line_diagram_output/";

  private static void run(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final Map<String, String> argsMap,
      final Map<String, String> config,
      final String command)
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

    assertThat(
        outputOf(
            commandlineExecution(
                connectionInfo, command, argsMap, runConfig, DiagramOutputFormat.scdot)),
        hasSameContentAs(
            classpathResource(
                COMMAND_LINE_DIAGRAM_OUTPUT + testContext.testMethodName() + ".scdot")));
  }

  @Test
  public void commandLineWithCardinalityOptionsConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.graph.show.primarykey.cardinality", Boolean.FALSE.toString());
    config.put("schemacrawler.graph.show.foreignkey.cardinality", Boolean.FALSE.toString());

    run(testContext, connectionInfo, args, config, "brief");
  }

  @Test
  public void commandLineWithDefaults(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();

    final Map<String, String> config = new HashMap<>();

    run(testContext, connectionInfo, args, config, "brief");
  }

  @Test
  public void commandLineWithGraphvizAttributesConfig(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.graph.graphviz.graph.ranksep", String.valueOf(3));

    run(testContext, connectionInfo, args, config, "brief");
  }
}
