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


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newBufferedWriter;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import sf.util.IOUtility;

public class SiteGraphVariationsTest
  extends
  BaseSiteVariationsTest
{

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestInfo testInfo)
    throws IOException,
    URISyntaxException
  {
    if (directory != null)
    {
      return;
    }
    directory = resolveTargetFromRootPath(testInfo, "diagram-examples");
  }

  @Test
  public void diagram(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");

    final Map<String, String> config = new HashMap<>();

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_10_no_schema_colors(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.no_schema_colors", "true");

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_11_title(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("title", "Books and Publishers Schema");

    final Map<String, String> config = new HashMap<>();

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_12_graphviz_attributes(final TestInfo testInfo)
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

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_2_portablenames(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_3_important_columns(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("command", "brief");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_4_ordinals(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_5_alphabetical(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");
    args.put("sortcolumns", "true");

    final Map<String, String> config = new HashMap<>();

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_6_grep(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_7_grep_onlymatching(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("only-matching", "true");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_8_no_cardinality(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.graph.show.primarykey.cardinality", "false");
    config.put("schemacrawler.graph.show.foreignkey.cardinality", "false");

    run(args, config, diagramPath(testInfo));
  }

  @Test
  public void diagram_9_row_counts(final TestInfo testInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_row_counts", "true");

    run(args, config, diagramPath(testInfo));
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = SiteGraphVariationsTest.class.getName();
    final Path configFile = IOUtility.createTempFilePath(prefix, "properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(newBufferedWriter(configFile, UTF_8), prefix);
    return configFile;
  }

  private Path diagramPath(final TestInfo testInfo)
  {
    return directory.resolve(currentMethodName(testInfo) + ".png");
  }

  private void run(final Map<String, String> argsMap,
                   final Map<String, String> config, final Path outputFile)
    throws Exception
  {
    deleteIfExists(outputFile);

    argsMap.put("url", getConnectionUrl());
    argsMap.put("user", "sa");
    argsMap.put("password", "");
    argsMap.put("title", "Details of Example Database");
    argsMap.put("tables", ".*");
    argsMap.put("routines", "");
    if (!argsMap.containsKey("command"))
    {
      argsMap.put("command", "schema");
    }
    argsMap.put("outputformat", "png");
    argsMap.put("outputfile", outputFile.toString());

    final Config runConfig = new Config();
    final Config informationSchema = loadHsqldbConfig();
    runConfig.putAll(informationSchema);
    if (config != null)
    {
      runConfig.putAll(config);
    }

    final Path configFile = createConfig(runConfig);
    argsMap.put("g", configFile.toString());

    Main.main(flattenCommandlineArgs(argsMap));
  }

}
