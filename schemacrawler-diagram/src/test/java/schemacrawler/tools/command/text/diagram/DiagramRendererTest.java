/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static java.nio.file.Files.createDirectories;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder.builder;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Path;
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
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.embeddeddiagram.EmbeddedDiagramRenderer;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.test.utility.TestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class DiagramRendererTest {

  private static final String DIAGRAM_OUTPUT = "diagram_renderer_output/";
  private static Path directory;

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(DIAGRAM_OUTPUT);
  }

  public static Path commandExecution(
      final DatabaseConnectionSource connectionSource,
      final SchemaCrawlerCommand<DiagramOptions> scCommand,
      final DiagramOutputFormat outputFormat)
      throws Exception {
    final Path tempFilePath = IOUtility.createTempFilePath("test", "");
    final OutputOptionsBuilder outputOptionsBuilder =
        OutputOptionsBuilder.builder()
            .withOutputFormatValue(outputFormat.getFormat())
            .withOutputFile(tempFilePath);

    scCommand.setOutputOptions(outputOptionsBuilder.toOptions());
    if (scCommand.usesConnection()) {
      scCommand.setConnectionSource(connectionSource);
    }

    final SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaCrawlerUtility.matchSchemaRetrievalOptions(connectionSource);
    scCommand.setIdentifiers(schemaRetrievalOptions.getIdentifiers());
    scCommand.setInformationSchemaViews(schemaRetrievalOptions.getInformationSchemaViews());

    // Initialize, and check if the command is available
    scCommand.initialize();

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
      final DatabaseConnectionSource connectionSource,
      final Catalog catalog,
      final DiagramOptions diagramOptions,
      final DiagramOutputFormat diagramOutputFormat,
      final String testMethodName)
      throws Exception {

    scCommand.configure(diagramOptions);
    scCommand.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

    scCommand.setCatalog(catalog);

    // Check output file
    final String referenceFileName = testMethodName;
    assertThat(
        outputOf(commandExecution(connectionSource, scCommand, diagramOutputFormat)),
        hasSameContentAndTypeAs(
            classpathResource(
                DIAGRAM_OUTPUT + referenceFileName + "." + diagramOutputFormat.getFormat()),
            diagramOutputFormat));
  }

  private static Catalog getCatalog(final DatabaseConnectionSource connectionSource) {
    SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .fromOptions(schemaCrawlerOptions.limitOptions())
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
    schemaCrawlerOptions = schemaCrawlerOptions.withLimitOptions(limitOptionsBuilder.toOptions());

    SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaCrawlerUtility.matchSchemaRetrievalOptions(connectionSource);
    schemaRetrievalOptions =
        SchemaRetrievalOptionsBuilder.builder(schemaRetrievalOptions).toOptions();

    final Catalog catalog =
        SchemaCrawlerUtility.getCatalog(
            connectionSource,
            schemaRetrievalOptions,
            schemaCrawlerOptions,
            ConfigUtility.newConfig());
    return catalog;
  }

  @Test
  @WithSystemProperty(key = "SC_GRAPHVIZ_PROC_DISABLE", value = "true")
  public void checkGraphvizAvailabilityDisabled() throws Exception {

    assertThat(GraphvizUtility.isGraphvizAvailable(), is(false));
  }

  @Test
  public void checkGraphvizAvailabilityEnabled() throws Exception {

    assertThat(GraphvizUtility.isGraphvizAvailable(), is(true));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void diagramRenderer_graphviz(
      final TestContext testContext, final DatabaseConnectionSource connectionSource)
      throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(connectionSource);

    commandDiagram(
        new DiagramRenderer(
            SchemaTextDetailType.details.toPropertyName(), new GraphExecutorFactory()),
        connectionSource,
        catalog,
        diagramOptions,
        DiagramOutputFormat.canon,
        testContext.testMethodName());
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void embeddedDiagramRenderer_graphviz(
      final TestContext testContext, final DatabaseConnectionSource connectionSource)
      throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(connectionSource);

    commandDiagram(
        new EmbeddedDiagramRenderer(
            SchemaTextDetailType.details.toPropertyName(), new GraphExecutorFactory()),
        connectionSource,
        catalog,
        diagramOptions,
        DiagramOutputFormat.htmlx,
        testContext.testMethodName());
  }
}
