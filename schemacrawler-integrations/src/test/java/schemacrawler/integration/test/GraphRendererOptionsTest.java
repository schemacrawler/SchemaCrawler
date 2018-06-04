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


import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.validateDiagram;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.graph.GraphOptions;
import schemacrawler.tools.integration.graph.GraphOptionsBuilder;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.utility.SchemaCrawlerUtility;
import sf.util.IOUtility;

public class GraphRendererOptionsTest
  extends BaseExecutableTest
{

  private static final String GRAPH_OPTIONS_OUTPUT = "graph_options_output/";

  private static Path directory;

  @BeforeClass
  public static void removeOutputDir()
    throws Exception
  {
    clean(GRAPH_OPTIONS_OUTPUT);
  }

  @BeforeClass
  public static void setupDirectory()
    throws Exception
  {
    final Path codePath = Paths.get(GraphRendererOptionsTest.class
      .getProtectionDomain().getCodeSource().getLocation().toURI()).normalize()
      .toAbsolutePath();
    directory = codePath
      .resolve("../../../schemacrawler-docs/graphs/"
               + GraphRendererOptionsTest.class.getSimpleName())
      .normalize().toAbsolutePath();
    FileUtils.deleteDirectory(directory.toFile());
    createDirectories(directory);
  }

  @Rule
  public TestName testName = new TestName();

  @Test
  public void executableForGraph_00()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_01()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setAlphabeticalSortForTableColumns(true);
    graphOptions.setShowOrdinalNumbers(true);

    executableGraph(SchemaTextDetailType.schema.name(),
                    new SchemaCrawlerOptions(),
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_02()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setHideForeignKeyNames(true);

    executableGraph(SchemaTextDetailType.schema.name(),
                    new SchemaCrawlerOptions(),
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_03()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setNoSchemaCrawlerInfo(true);
    graphOptions.setShowDatabaseInfo(false);
    graphOptions.setShowJdbcDriverInfo(false);

    executableGraph(SchemaTextDetailType.schema.name(),
                    new SchemaCrawlerOptions(),
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_04()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowUnqualifiedNames(true);

    executableGraph(SchemaTextDetailType.schema.name(),
                    new SchemaCrawlerOptions(),
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_05()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setTableInclusionRule(new RegularExpressionInclusionRule(".*BOOKS"));
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_06()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(SchemaTextDetailType.brief.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_07()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_08()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setTableInclusionRule(new RegularExpressionInclusionRule(".*BOOKS"));

    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowUnqualifiedNames(true);
    graphOptions.setHideForeignKeyNames(true);

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_09()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setTableInclusionRule(new RegularExpressionInclusionRule(".*BOOKS"));
    schemaCrawlerOptions.setGrepOnlyMatching(true);

    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowUnqualifiedNames(true);
    graphOptions.setHideForeignKeyNames(true);

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_10()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new RegularExpressionInclusionRule(".*\\.REGIONS\\..*"));

    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_11()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new RegularExpressionInclusionRule(".*\\.REGIONS\\..*"));
    schemaCrawlerOptions.setGrepOnlyMatching(true);

    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_12()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowRowCounts(true);

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_13()
    throws Exception
  {
    final Map<String, String> graphvizAttributes = new HashMap<>();

    final String GRAPH = "graph.";
    graphvizAttributes.put(GRAPH + "splines", "ortho");

    final String NODE = "node.";
    graphvizAttributes.put(NODE + "shape", "none");

    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setGraphvizAttributes(graphvizAttributes);

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_lintschema()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionInclusionRule(".*\\.FOR_LINT"));
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(SchemaTextDetailType.schema.name(),
                    schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  private void executableGraph(final String command,
                               final SchemaCrawlerOptions schemaCrawlerOptions,
                               final GraphOptions graphOptions,
                               final String testMethodName)
    throws Exception
  {
    if (schemaCrawlerOptions.getSchemaInclusionRule().equals(new IncludeAll()))
    {
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
    }

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);

    final GraphOptionsBuilder graphOptionsBuilder = new GraphOptionsBuilder(graphOptions);
    graphOptionsBuilder.sortTables(true);
    if (!graphOptions.isNoInfo())
    {
      graphOptionsBuilder.showInfo();
    }
    if (!"maximum".equals(schemaCrawlerOptions.getSchemaInfoLevel().getTag()))
    {
      graphOptionsBuilder.weakAssociations(true);
    }
    executable.setAdditionalConfiguration(graphOptionsBuilder.toConfig());

    // Generate diagram, so that we have something to look at, even if
    // the DOT file comparison fails
    final Path testDiagramFile = executeGraphExecutable(executable);
    copy(testDiagramFile,
         directory.resolve(testMethodName + ".png"),
         REPLACE_EXISTING);

    // Check DOT file
    final String referenceFileName = testMethodName;
    executeExecutable(executable,
                      GraphOutputFormat.scdot.getFormat(),
                      GRAPH_OPTIONS_OUTPUT + referenceFileName + ".dot");
  }

  private Path executeGraphExecutable(final SchemaCrawlerExecutable executable)
    throws Exception
  {
    final String outputFormatValue = GraphOutputFormat.png.getFormat();

    final Path testOutputFile = IOUtility
      .createTempFilePath(executable.getCommand(), outputFormatValue);

    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          testOutputFile);

    executable.setOutputOptions(outputOptions);
    final Connection connection = getConnection();
    final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = SchemaCrawlerUtility
      .matchDatabaseSpecificOverrideOptions(connection);
    executable.execute(connection, databaseSpecificOverrideOptions);

    validateDiagram(testOutputFile);

    return testOutputFile;
  }

}
