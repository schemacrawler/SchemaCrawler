/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.integration.graph;


import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static schemacrawler.test.utility.TestUtility.validateDiagram;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class GraphExecutableOptionsTest
  extends BaseExecutableTest
{

  private static final String GRAPH_OPTIONS_OUTPUT = "graph_options_output/";

  private static Path directory;

  @BeforeClass
  public static void setupDirectory()
    throws IOException, URISyntaxException
  {
    final Path codePath = Paths.get(GraphExecutableOptionsTest.class
      .getProtectionDomain().getCodeSource().getLocation().toURI()).normalize()
      .toAbsolutePath();
    directory = codePath
      .resolve("../../../schemacrawler-docs/graphs/"
               + GraphExecutableOptionsTest.class.getSimpleName())
      .normalize().toAbsolutePath();
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

    executableGraph(schemaCrawlerOptions,
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

    executableGraph(new SchemaCrawlerOptions(),
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_02()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setHideForeignKeyNames(true);

    executableGraph(new SchemaCrawlerOptions(),
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_03()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setNoInfo(true);

    executableGraph(new SchemaCrawlerOptions(),
                    graphOptions,
                    testName.currentMethodName());
  }

  @Test
  public void executableForGraph_04()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowUnqualifiedNames(true);

    executableGraph(new SchemaCrawlerOptions(),
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

    executableGraph(schemaCrawlerOptions,
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
    graphOptions.setSchemaTextDetailType(SchemaTextDetailType.brief);

    executableGraph(schemaCrawlerOptions,
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
    graphOptions.setSchemaTextDetailType(SchemaTextDetailType.schema);

    executableGraph(schemaCrawlerOptions,
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

    executableGraph(schemaCrawlerOptions,
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

    executableGraph(schemaCrawlerOptions,
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

    executableGraph(schemaCrawlerOptions,
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

    executableGraph(schemaCrawlerOptions,
                    graphOptions,
                    testName.currentMethodName());
  }

  private void executableGraph(final SchemaCrawlerOptions schemaCrawlerOptions,
                               final GraphOptions graphOptions,
                               final String testMethodName)
                                 throws Exception
  {
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));

    final GraphExecutable executable = new GraphExecutable();
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);

    final GraphOptionsBuilder graphOptionsBuilder = new GraphOptionsBuilder(graphOptions);
    graphOptionsBuilder.sortTables();
    executable.setAdditionalConfiguration(graphOptionsBuilder.toConfig());

    // Check DOT file
    final String referenceFileName = testMethodName;
    executeExecutable(executable,
                      GraphOutputFormat.scdot.getFormat(),
                      GRAPH_OPTIONS_OUTPUT + referenceFileName + ".dot");

    // Check diagram
    final Path testDiagramFile = executeGraphExecutable(executable);
    copy(testDiagramFile,
         directory.resolve(testMethodName + ".png"),
         REPLACE_EXISTING);
  }

  private Path executeGraphExecutable(final GraphExecutable executable)
    throws Exception
  {
    final String outputFormatValue = GraphOutputFormat.png.getFormat();

    final Path testOutputFile = TestUtility
      .createTempFile(executable.getCommand(), outputFormatValue);

    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          testOutputFile);

    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    validateDiagram(testOutputFile);

    return testOutputFile;
  }

}
