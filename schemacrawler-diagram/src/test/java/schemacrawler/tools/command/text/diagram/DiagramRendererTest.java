/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.text.diagram;

import static java.nio.file.Files.createDirectories;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder.builder;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.scdot;

import java.nio.file.Path;
import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.integration.test.DiagramOutputTest;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestDisabledWithoutGraphvizExtension;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.embeddeddiagram.EmbeddedDiagramRenderer;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class DiagramRendererTest {

  private final class GraphvizJavaExecutorFactory extends GraphExecutorFactory {

    @Override
    public void canGenerate(final DiagramOutputFormat diagramOutputFormat) {
      // No-op
    }

    @Override
    public GraphExecutor getGraphExecutor(
        final Path dotFile,
        final DiagramOutputFormat diagramOutputFormat,
        final Path outputFile,
        final DiagramOptions commandOptions) {
      final GraphExecutor graphExecutor;
      if (diagramOutputFormat != scdot) {
        graphExecutor = new GraphvizJavaExecutor(dotFile, outputFile, diagramOutputFormat);
      } else {
        graphExecutor = new GraphNoOpExecutor(diagramOutputFormat);
      }
      return graphExecutor;
    }
  }

  private static final String DIAGRAM_OUTPUT = "diagram_renderer_output/";
  private static Path directory;

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(DIAGRAM_OUTPUT);
  }

  public static Path commandExecution(
      final Connection connection,
      final SchemaCrawlerCommand<DiagramOptions> scCommand,
      final DiagramOutputFormat outputFormat)
      throws Exception {
    final Path tempFilePath = IOUtility.createTempFilePath("test", "");
    final OutputOptionsBuilder outputOptionsBuilder =
        OutputOptionsBuilder.builder()
            .withOutputFormatValue(outputFormat.getFormat())
            .withOutputFile(tempFilePath);

    scCommand.setOutputOptions(outputOptionsBuilder.toOptions());
    scCommand.setConnection(connection);

    final SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaCrawlerUtility.matchSchemaRetrievalOptions(connection);
    scCommand.setIdentifiers(schemaRetrievalOptions.getIdentifiers());

    // Initialize, and check if the command is available
    scCommand.initialize();
    scCommand.checkAvailability();

    scCommand.execute();

    return tempFilePath;
  }

  @BeforeAll
  public static void setupDirectory(final TestContext testContext) throws Exception {
    directory =
        testContext.resolveTargetFromRootPath(
            "test-output-diagrams/" + DiagramOutputTest.class.getSimpleName());
    deleteDirectory(directory.toFile());
    createDirectories(directory);
  }

  private static void commandDiagram(
      final SchemaCrawlerCommand<DiagramOptions> scCommand,
      final Connection connection,
      final Catalog catalog,
      final DiagramOptions diagramOptions,
      final DiagramOutputFormat diagramOutputFormat,
      final String testMethodName)
      throws Exception {

    scCommand.setCommandOptions(diagramOptions);
    scCommand.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

    scCommand.setCatalog(catalog);

    // Check output file
    final String referenceFileName = testMethodName;
    assertThat(
        outputOf(commandExecution(connection, scCommand, diagramOutputFormat)),
        hasSameContentAndTypeAs(
            classpathResource(
                DIAGRAM_OUTPUT + referenceFileName + "." + diagramOutputFormat.getFormat()),
            diagramOutputFormat));
  }

  private static Catalog getCatalog(final Connection connection) {
    try {
      SchemaCrawlerOptions schemaCrawlerOptions =
          DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
      final LimitOptionsBuilder limitOptionsBuilder =
          LimitOptionsBuilder.builder()
              .fromOptions(schemaCrawlerOptions.getLimitOptions())
              .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions = schemaCrawlerOptions.withLimitOptions(limitOptionsBuilder.toOptions());

      SchemaRetrievalOptions schemaRetrievalOptions =
          SchemaCrawlerUtility.matchSchemaRetrievalOptions(connection);
      schemaRetrievalOptions =
          SchemaRetrievalOptionsBuilder.builder(schemaRetrievalOptions).toOptions();

      final Catalog catalog =
          SchemaCrawlerUtility.getCatalog(
              connection, schemaRetrievalOptions, schemaCrawlerOptions, new Config());
      return catalog;
    } catch (final SchemaCrawlerException e) {
      throw new RuntimeException("Could not get catalog", e);
    }
  }

  @Test
  @ExtendWith(TestDisabledWithoutGraphvizExtension.class)
  public void diagramRenderer_graphviz(final TestContext testContext, final Connection connection)
      throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(connection);

    commandDiagram(
        new DiagramRenderer(SchemaTextDetailType.details.name(), new GraphExecutorFactory()),
        connection,
        catalog,
        diagramOptions,
        DiagramOutputFormat.canon,
        testContext.testMethodName());
  }

  @Test
  public void diagramRenderer_graphviz_java(
      final TestContext testContext, final Connection connection) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(connection);

    commandDiagram(
        new DiagramRenderer(SchemaTextDetailType.details.name(), new GraphvizJavaExecutorFactory()),
        connection,
        catalog,
        diagramOptions,
        DiagramOutputFormat.svg,
        testContext.testMethodName());
  }

  @Test
  @ExtendWith(TestDisabledWithoutGraphvizExtension.class)
  public void embeddedDiagramRenderer_graphviz(
      final TestContext testContext, final Connection connection) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(connection);

    commandDiagram(
        new EmbeddedDiagramRenderer(
            SchemaTextDetailType.details.name(), new GraphExecutorFactory()),
        connection,
        catalog,
        diagramOptions,
        DiagramOutputFormat.htmlx,
        testContext.testMethodName());
  }

  @Test
  public void embeddedDiagramRenderer_graphviz_java(
      final TestContext testContext, final Connection connection) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(connection);

    commandDiagram(
        new EmbeddedDiagramRenderer(
            SchemaTextDetailType.details.name(), new GraphvizJavaExecutorFactory()),
        connection,
        catalog,
        diagramOptions,
        DiagramOutputFormat.htmlx,
        "embeddedDiagramRenderer_graphviz");
  }
}
