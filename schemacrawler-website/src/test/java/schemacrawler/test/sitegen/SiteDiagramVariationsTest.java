/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.sitegen;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static us.fatehi.test.utility.TestUtility.deleteIfPossible;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.PortableType;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@WithTestDatabase
@ResolveTestContext
@EnabledIfSystemProperty(named = "distrib", matches = "^((?!(false|no)).)*$")
public class SiteDiagramVariationsTest {

  private static void run(
      final DatabaseConnectionInfo connectionInfo,
      final String command,
      final Map<String, String> argsMap,
      final Map<String, String> config,
      final Path outputFile)
      throws Exception {
    deleteIfPossible(outputFile);
    assertThat(exists(outputFile), is(false));

    final Map<String, Object> runConfig = new HashMap<>();
    final Map<String, String> informationSchema = loadHsqldbConfig();
    runConfig.putAll(informationSchema);
    if (config != null) {
      runConfig.putAll(config);
    }

    final Path pngFile =
        commandlineExecution(connectionInfo, command, argsMap, runConfig, DiagramOutputFormat.png);
    move(pngFile, outputFile, REPLACE_EXISTING);
  }

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
      throws IOException, URISyntaxException {
    if (directory != null) {
      return;
    }
    directory = testContext.resolveTargetFromRootPath("_website/diagram-examples");
  }

  @Test
  public void diagram(final TestContext testContext, final DatabaseConnectionInfo connectionInfo)
      throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.maximum.name());

    run(connectionInfo, "schema", argsMap, null, diagramPath(testContext));
  }

  @Test
  public void diagram_10_no_schema_colors(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--portable", PortableType.names.name());

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.no_schema_colors", Boolean.TRUE.toString());

    run(connectionInfo, "schema", argsMap, config, diagramPath(testContext));
  }

  @Test
  public void diagram_11_title(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--title", "Books and Publishers Schema");

    run(connectionInfo, "schema", argsMap, null, diagramPath(testContext));
  }

  @Test
  public void diagram_12_graphviz_attributes(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--portable", PortableType.names.name());

    final Map<String, String> config = new HashMap<>();
    final String GRAPH = "schemacrawler.graph.graphviz.graph.";
    config.put(GRAPH + "rankdir", "RL");
    config.put(GRAPH + "fontname", "Helvetica");

    final String NODE = "schemacrawler.graph.graphviz.node.";
    config.put(NODE + "fontname", "Helvetica");
    config.put(NODE + "shape", "none");

    final String EDGE = "schemacrawler.graph.graphviz.edge.";
    config.put(EDGE + "fontname", "Helvetica");

    // Test
    config.put("schemacrawler.graph.graphviz.graph.splines", "ortho");

    run(connectionInfo, "schema", argsMap, config, diagramPath(testContext));
  }

  @Test
  public void diagram_13_indexes(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());

    run(connectionInfo, "details", argsMap, null, diagramPath(testContext));
  }

  @Test
  public void diagram_14_weak_associations(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--weak-associations", Boolean.TRUE.toString());

    run(connectionInfo, "schema", argsMap, null, diagramPath(testContext));
  }

  @Test
  public void diagram_2_portablenames(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.maximum.name());
    argsMap.put("--portable", PortableType.names.name());

    run(connectionInfo, "schema", argsMap, null, diagramPath(testContext));
  }

  @Test
  public void diagram_3_important_columns(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("c", "brief");
    argsMap.put("--portable", PortableType.names.name());

    run(connectionInfo, "schema", argsMap, null, diagramPath(testContext));
  }

  @Test
  public void diagram_4_ordinals(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--portable", PortableType.names.name());

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", Boolean.TRUE.toString());

    run(connectionInfo, "schema", argsMap, config, diagramPath(testContext));
  }

  @Test
  public void diagram_5_alphabetical(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--portable", PortableType.names.name());
    argsMap.put("--sort-columns", Boolean.TRUE.toString());

    run(connectionInfo, "schema", argsMap, null, diagramPath(testContext));
  }

  @Test
  public void diagram_6_grep(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.maximum.name());
    argsMap.put("--portable", PortableType.names.name());
    argsMap.put("--grep-columns", ".*\\.BOOKS\\..*\\.ID");
    argsMap.put("--table-types", "TABLE");

    run(connectionInfo, "schema", argsMap, null, diagramPath(testContext));
  }

  @Test
  public void diagram_8_no_cardinality(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--portable", PortableType.names.name());

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.graph.show.primarykey.cardinality", Boolean.FALSE.toString());
    config.put("schemacrawler.graph.show.foreignkey.cardinality", Boolean.FALSE.toString());

    run(connectionInfo, "schema", argsMap, config, diagramPath(testContext));
  }

  @Test
  public void diagram_9_row_counts(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.maximum.name());
    argsMap.put("--load-row-counts", Boolean.TRUE.toString());
    argsMap.put("--portable", PortableType.names.name());

    run(connectionInfo, "schema", argsMap, null, diagramPath(testContext));
  }

  private Path diagramPath(final TestContext testContext) {
    return directory.resolve(testContext.testMethodName() + ".png");
  }
}
