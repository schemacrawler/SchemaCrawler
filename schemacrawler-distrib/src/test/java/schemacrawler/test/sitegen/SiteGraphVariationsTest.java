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
package schemacrawler.test.sitegen;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.*;
import schemacrawler.tools.integration.graph.GraphOutputFormat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.move;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;

@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestAssertNoSystemOutOutput.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class SiteGraphVariationsTest
{

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
      throws IOException, URISyntaxException
  {
    if (directory != null)
    {
      return;
    }
    directory = testContext.resolveTargetFromRootPath("diagram-examples");
  }

  @Test
  public void diagram(final TestContext testContext,
                      final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_10_no_schema_colors(final TestContext testContext,
                                          final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.no_schema_colors", "true");

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_11_title(final TestContext testContext,
                               final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("title", "Books and Publishers Schema");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_12_graphviz_attributes(final TestContext testContext,
                                             final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

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

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_2_portablenames(final TestContext testContext,
                                      final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_3_important_columns(final TestContext testContext,
                                          final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("command", "brief");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_4_ordinals(final TestContext testContext,
                                 final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_5_alphabetical(final TestContext testContext,
                                     final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");
    args.put("sortcolumns", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_6_grep(final TestContext testContext,
                             final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_7_grep_onlymatching(final TestContext testContext,
                                          final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("only-matching", "true");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_8_no_cardinality(final TestContext testContext,
                                       final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.graph.show.primarykey.cardinality", "false");
    config.put("schemacrawler.graph.show.foreignkey.cardinality", "false");

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  @Test
  public void diagram_9_row_counts(final TestContext testContext,
                                   final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_row_counts", "true");

    run(connectionInfo, args, config, diagramPath(testContext));
  }

  private Path diagramPath(final TestContext testContext)
  {
    return directory.resolve(testContext.testMethodName() + ".png");
  }

  private void run(final DatabaseConnectionInfo connectionInfo,
                   final Map<String, String> argsMap,
                   final Map<String, String> config,
                   final Path outputFile)
      throws Exception
  {
    deleteIfExists(outputFile);

    argsMap.put("title", "Details of Example Database");

    final Config runConfig = new Config();
    final Config informationSchema = loadHsqldbConfig();
    runConfig.putAll(informationSchema);
    if (config != null)
    {
      runConfig.putAll(config);
    }

    final Path pngFile = commandlineExecution(connectionInfo,
                                              "schema",
                                              argsMap,
                                              runConfig,
                                              GraphOutputFormat.png);
    move(pngFile, outputFile);
  }

}
