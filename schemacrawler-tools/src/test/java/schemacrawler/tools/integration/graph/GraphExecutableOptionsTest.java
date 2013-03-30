/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class GraphExecutableOptionsTest
  extends BaseDatabaseTest
{

  private static final String GRAPH_OPTIONS_OUTPUT = "graph_options_output/";

  @Test
  public void executableForGraph_00()
    throws Exception
  {

    final String testMethodName = TestUtility.currentMethodName();
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableForGraph_01()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setAlphabeticalSortForTableColumns(true);
    graphOptions.setShowOrdinalNumbers(true);

    executableGraph(new SchemaCrawlerOptions(), graphOptions);
  }

  @Test
  public void executableForGraph_02()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setHideForeignKeyNames(true);

    executableGraph(new SchemaCrawlerOptions(), graphOptions);
  }

  @Test
  public void executableForGraph_03()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setNoInfo(true);

    executableGraph(new SchemaCrawlerOptions(), graphOptions);
  }

  @Test
  public void executableForGraph_04()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowUnqualifiedNames(true);

    executableGraph(new SchemaCrawlerOptions(), graphOptions);
  }

  @Test
  public void executableForGraph_05()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setTableInclusionRule(new InclusionRule(".*BOOKS"));
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableForGraph_06()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setSchemaTextDetailType(SchemaTextDetailType.list);

    executableGraph(schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableForGraph_07()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setSchemaTextDetailType(SchemaTextDetailType.schema);

    executableGraph(schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableForGraph_08()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setTableInclusionRule(new InclusionRule(".*BOOKS"));

    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowUnqualifiedNames(true);
    graphOptions.setHideForeignKeyNames(true);

    executableGraph(schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableForGraph_09()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setTableInclusionRule(new InclusionRule(".*BOOKS"));
    schemaCrawlerOptions.setGrepOnlyMatching(true);

    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowUnqualifiedNames(true);
    graphOptions.setHideForeignKeyNames(true);

    executableGraph(schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableForGraph_10()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.REGIONS\\..*", ""));

    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableForGraph_11()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\.REGIONS\\..*", ""));
    schemaCrawlerOptions.setGrepOnlyMatching(true);

    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(schemaCrawlerOptions, graphOptions);
  }

  private void executableGraph(final SchemaCrawlerOptions schemaCrawlerOptions,
                               final GraphOptions graphOptions)
    throws Exception
  {
    final GraphExecutable executable = new GraphExecutable();
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(graphOptions.toConfig());

    final String testMethodName = TestUtility.callingMethodName();
    final String referenceFileName = testMethodName;
    executeExecutableAndCheckForOutputFile(executable,
                                           "echo",
                                           referenceFileName);

    final File testDiagramFile = File.createTempFile("schemacrawler."
                                                     + executable.getCommand()
                                                     + ".", ".png");
    testDiagramFile.delete();
    final File referenceDotFile = TestUtility
      .copyResourceToTempFile("/" + GRAPH_OPTIONS_OUTPUT + referenceFileName
                              + ".dot");
    final GraphGenerator graphGenerator = new GraphGenerator(graphOptions.getGraphVizOpts(),
                                                             referenceDotFile,
                                                             "png",
                                                             testDiagramFile);
    graphGenerator.generateDiagram();
    checkDiagramFile(testDiagramFile);
  }

  private void executeExecutableAndCheckForOutputFile(final Executable executable,
                                                      final String outputFormatValue,
                                                      final String referenceFileName)
    throws Exception
  {
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                    + executable.getCommand()
                                                    + ".", ".test");
    testOutputFile.delete();
    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          testOutputFile);

    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    final List<String> failures = TestUtility
      .compareOutput(GRAPH_OPTIONS_OUTPUT + referenceFileName + ".dot",
                     testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
