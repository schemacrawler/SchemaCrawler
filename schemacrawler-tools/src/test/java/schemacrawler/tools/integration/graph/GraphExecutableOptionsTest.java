/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
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
  public void executableGraphDot00()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(0, schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableGraphDot01()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setAlphabeticalSortForTableColumns(true);
    graphOptions.setShowOrdinalNumbers(true);

    executableGraph(1, graphOptions);
  }

  @Test
  public void executableGraphDot02()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setHideForeignKeyNames(true);

    executableGraph(2, graphOptions);
  }

  @Test
  public void executableGraphDot03()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setNoInfo(true);

    executableGraph(3, graphOptions);
  }

  @Test
  public void executableGraphDot04()
    throws Exception
  {
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setShowUnqualifiedNames(true);

    executableGraph(4, graphOptions);
  }

  @Test
  public void executableGraphDot05()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setTableInclusionRule(new InclusionRule(".*BOOKS"));
    final GraphOptions graphOptions = new GraphOptions();

    executableGraph(5, schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableGraphDot06()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setSchemaTextDetailType(SchemaTextDetailType.list);

    executableGraph(6, schemaCrawlerOptions, graphOptions);
  }

  @Test
  public void executableGraphDot07()
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    final GraphOptions graphOptions = new GraphOptions();
    graphOptions.setSchemaTextDetailType(SchemaTextDetailType.schema);

    executableGraph(7, schemaCrawlerOptions, graphOptions);
  }

  private void executableGraph(final int testNumber,
                               final GraphOptions graphOptions)
    throws Exception
  {
    executableGraph(testNumber, new SchemaCrawlerOptions(), graphOptions);
  }

  private void executableGraph(final int testNumber,
                               final SchemaCrawlerOptions schemaCrawlerOptions,
                               final GraphOptions graphOptions)
    throws Exception
  {
    final GraphExecutable executable = new GraphExecutable();
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(graphOptions.toConfig());

    final String referenceFileName = String.format("executableForGraph_%02d",
                                                   testNumber);
    executeExecutableAndCheckForOutputFile(executable,
                                           "canon",
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
