/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.file.Files.createDirectories;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.clean;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.diagram.DiagramOptions;
import schemacrawler.tools.integration.diagram.DiagramOptionsBuilder;
import schemacrawler.tools.integration.diagram.DiagramOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class DiagramRendererOptionsTest
{

  private static final String DIAGRAM_OPTIONS_OUTPUT = "diagram_options_output/";

  private static void executableDiagram(final String command,
                                        final Connection connection,
                                        final SchemaCrawlerOptions options,
                                        final DiagramOptions diagramOptions,
                                        final String testMethodName)
    throws Exception
  {
    SchemaCrawlerOptions schemaCrawlerOptions = options;
    if (options
      .getSchemaInclusionRule()
      .equals(new IncludeAll()))
    {
      schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
        .builder()
        .fromOptions(options)
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"))
        .toOptions();
    }

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);

    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder(diagramOptions);
    diagramOptionsBuilder.sortTables(true);
    diagramOptionsBuilder.noInfo(diagramOptions.isNoInfo());
    if (!"maximum".equals(options
                            .getSchemaInfoLevel()
                            .getTag()))
    {
      diagramOptionsBuilder.weakAssociations(true);
    }
    executable.setAdditionalConfiguration(diagramOptionsBuilder.toConfig());

    executable.setConnection(connection);

    // Generate diagram, so that we have something to look at, even if
    // the DOT file comparison fails
    saveDiagram(executable, testMethodName);

    // Check DOT file
    final String referenceFileName = testMethodName;
    assertThat(outputOf(executableExecution(connection, executable, DiagramOutputFormat.scdot)),
               hasSameContentAndTypeAs(classpathResource(DIAGRAM_OPTIONS_OUTPUT + referenceFileName + ".dot"),
                                       DiagramOutputFormat.scdot));
  }

  @BeforeAll
  public static void removeOutputDir()
    throws Exception
  {
    clean(DIAGRAM_OPTIONS_OUTPUT);
  }

  private static void saveDiagram(final SchemaCrawlerExecutable executable, final String testMethodName)
    throws Exception
  {
    final Path testDiagramFile = directory.resolve(testMethodName + ".png");

    final OutputOptions oldOutputOptions = executable.getOutputOptions();
    final OutputOptions outputOptions = OutputOptionsBuilder
      .builder()
      .fromOptions(oldOutputOptions)
      .withOutputFile(testDiagramFile)
      .withOutputFormat(DiagramOutputFormat.png)
      .toOptions();

    executable.setOutputOptions(outputOptions);

    executable.execute();
  }

  @BeforeAll
  public static void setupDirectory(final TestContext testContext)
    throws Exception
  {
    directory =
      testContext.resolveTargetFromRootPath("test-output-diagrams/" + DiagramRendererOptionsTest.class.getSimpleName());
    deleteDirectory(directory.toFile());
    createDirectories(directory);
  }

  private static Path directory;

  @Test
  public void executableForDiagram_00(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions =
      DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final DiagramOptions diagramOptions = DiagramOptionsBuilder
      .builder()
      .toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_01(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder();
    diagramOptionsBuilder.sortTableColumns();
    diagramOptionsBuilder.showOrdinalNumbers();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      optionsWithWeakAssociations(),
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_02(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder();
    diagramOptionsBuilder.noForeignKeyNames();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      optionsWithWeakAssociations(),
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_03(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder();
    diagramOptionsBuilder.noSchemaCrawlerInfo(true);
    diagramOptionsBuilder.showDatabaseInfo(false);
    diagramOptionsBuilder.showJdbcDriverInfo(false);
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      optionsWithWeakAssociations(),
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_04(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder();
    diagramOptionsBuilder.showUnqualifiedNames();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      optionsWithWeakAssociations(),
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_05(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder()
      .includeTables(new RegularExpressionInclusionRule(".*BOOKS"));
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder.toOptions();

    final DiagramOptions diagramOptions = DiagramOptionsBuilder
      .builder()
      .toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_06(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions =
      DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final DiagramOptions diagramOptions = DiagramOptionsBuilder
      .builder()
      .toOptions();

    executableDiagram(SchemaTextDetailType.brief.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_07(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions =
      DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final DiagramOptions diagramOptions = DiagramOptionsBuilder
      .builder()
      .toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_08(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder()
      .includeTables(new RegularExpressionInclusionRule(".*BOOKS"));
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder.toOptions();

    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder();
    diagramOptionsBuilder
      .noForeignKeyNames()
      .showUnqualifiedNames();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_09(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder()
      .includeTables(new RegularExpressionInclusionRule(".*BOOKS"))
      .grepOnlyMatching(true);
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder.toOptions();

    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder();
    diagramOptionsBuilder
      .noForeignKeyNames()
      .showUnqualifiedNames();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_10(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .builder()
      .includeGreppedColumns(new RegularExpressionInclusionRule(".*\\.REGIONS\\..*"))
      .toOptions();

    final DiagramOptions diagramOptions = DiagramOptionsBuilder
      .builder()
      .toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_11(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .builder()
      .includeGreppedColumns(new RegularExpressionInclusionRule(".*\\.REGIONS\\..*"))
      .grepOnlyMatching(true)
      .toOptions();

    final DiagramOptions diagramOptions = DiagramOptionsBuilder
      .builder()
      .toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_12(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder();
    diagramOptionsBuilder.showRowCounts();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final SchemaCrawlerOptions schemaCrawlerOptions =
      DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_13(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final Map<String, String> graphvizAttributes = new HashMap<>();

    final String GRAPH = "graph.";
    graphvizAttributes.put(GRAPH + "splines", "ortho");

    final String NODE = "node.";
    graphvizAttributes.put(NODE + "shape", "none");

    final DiagramOptionsBuilder diagramOptionsBuilder = DiagramOptionsBuilder.builder();
    diagramOptionsBuilder.withGraphvizAttributes(graphvizAttributes);
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final SchemaCrawlerOptions schemaCrawlerOptions =
      DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  @Test
  public void executableForDiagram_lintschema(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeSchemas(new RegularExpressionInclusionRule(".*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder.toOptions();

    final DiagramOptions diagramOptions = DiagramOptionsBuilder
      .builder()
      .toOptions();

    executableDiagram(SchemaTextDetailType.schema.name(),
                      connection,
                      schemaCrawlerOptions,
                      diagramOptions,
                      testContext.testMethodName());
  }

  private SchemaCrawlerOptions optionsWithWeakAssociations()
  {
    final SchemaInfoLevelBuilder infoLevelBuilder = SchemaInfoLevelBuilder
      .builder()
      .withInfoLevel(InfoLevel.standard)
      .setRetrieveWeakAssociations(true);
    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder()
      .withSchemaInfoLevel(infoLevelBuilder);
    return builder.toOptions();
  }

}
