/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.CoreMatchers.is;
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

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.integration.test.DiagramOutputTest;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.OnlyRunWithGraphviz;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
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
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
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
      final DatabaseConnectionSource dataSource,
      final SchemaCrawlerCommand<DiagramOptions> scCommand,
      final DiagramOutputFormat outputFormat)
      throws Exception {
    final Path tempFilePath = IOUtility.createTempFilePath("test", "");
    final OutputOptionsBuilder outputOptionsBuilder =
        OutputOptionsBuilder.builder()
            .withOutputFormatValue(outputFormat.getFormat())
            .withOutputFile(tempFilePath);

    try (final Connection connection = dataSource.get(); ) {
      scCommand.setOutputOptions(outputOptionsBuilder.toOptions());
      scCommand.setConnection(connection);

      final SchemaRetrievalOptions schemaRetrievalOptions =
          SchemaCrawlerUtility.matchSchemaRetrievalOptions(dataSource);
      scCommand.setIdentifiers(schemaRetrievalOptions.getIdentifiers());

      // Initialize, and check if the command is available
      scCommand.initialize();
      scCommand.checkAvailability();

      scCommand.execute();
    }

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
      final DatabaseConnectionSource dataSource,
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
        outputOf(commandExecution(dataSource, scCommand, diagramOutputFormat)),
        hasSameContentAndTypeAs(
            classpathResource(
                DIAGRAM_OUTPUT + referenceFileName + "." + diagramOutputFormat.getFormat()),
            diagramOutputFormat));
  }

  private static Catalog getCatalog(final DatabaseConnectionSource dataSource) {
    SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .fromOptions(schemaCrawlerOptions.getLimitOptions())
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
    schemaCrawlerOptions = schemaCrawlerOptions.withLimitOptions(limitOptionsBuilder.toOptions());

    SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaCrawlerUtility.matchSchemaRetrievalOptions(dataSource);
    schemaRetrievalOptions =
        SchemaRetrievalOptionsBuilder.builder(schemaRetrievalOptions).toOptions();

    final Catalog catalog =
        SchemaCrawlerUtility.getCatalog(
            dataSource, schemaRetrievalOptions, schemaCrawlerOptions, new Config());
    return catalog;
  }

  @Test
  @OnlyRunWithGraphviz
  @WithSystemProperty(key = "SC_GRAPHVIZ_PROC_DISABLE", value = "true")
  public void checkGraphvizAvailabilityDisabled() throws Exception {

    assertThat(GraphvizUtility.isGraphvizAvailable(), is(false));
  }

  @Test
  @OnlyRunWithGraphviz
  public void checkGraphvizAvailabilityEnabled() throws Exception {

    assertThat(GraphvizUtility.isGraphvizAvailable(), is(true));
  }

  @Test
  @OnlyRunWithGraphviz
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void diagramRenderer_graphviz(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(dataSource);

    commandDiagram(
        new DiagramRenderer(SchemaTextDetailType.details.name(), new GraphExecutorFactory()),
        dataSource,
        catalog,
        diagramOptions,
        DiagramOutputFormat.canon,
        testContext.testMethodName());
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void diagramRenderer_graphviz_java(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(dataSource);

    commandDiagram(
        new DiagramRenderer(SchemaTextDetailType.details.name(), new GraphvizJavaExecutorFactory()),
        dataSource,
        catalog,
        diagramOptions,
        DiagramOutputFormat.svg,
        testContext.testMethodName());
  }

  @Test
  @OnlyRunWithGraphviz
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void embeddedDiagramRenderer_graphviz(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(dataSource);

    commandDiagram(
        new EmbeddedDiagramRenderer(
            SchemaTextDetailType.details.name(), new GraphExecutorFactory()),
        dataSource,
        catalog,
        diagramOptions,
        DiagramOutputFormat.htmlx,
        testContext.testMethodName());
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void embeddedDiagramRenderer_graphviz_java(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(dataSource);

    commandDiagram(
        new EmbeddedDiagramRenderer(
            SchemaTextDetailType.details.name(), new GraphvizJavaExecutorFactory()),
        dataSource,
        catalog,
        diagramOptions,
        DiagramOutputFormat.htmlx,
        "embeddedDiagramRenderer_graphviz");
  }
}
