/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static java.nio.file.Files.createDirectories;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder.builder;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class DiagramRendererOptionsTest {

  private static final String DIAGRAM_OPTIONS_OUTPUT = "diagram_options_output/";
  private static Path directory;

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(DIAGRAM_OPTIONS_OUTPUT);
  }

  @BeforeAll
  public static void setupDirectory(final TestContext testContext) throws Exception {
    directory =
        testContext.resolveTargetFromRootPath(
            "test-output-diagrams/" + DiagramRendererOptionsTest.class.getSimpleName());
    deleteDirectory(directory.toFile());
    createDirectories(directory);
  }

  private static void executableDiagram(
      final String command,
      final DatabaseConnectionSource dataSource,
      final SchemaCrawlerOptions options,
      final Config config,
      final DiagramOptions diagramOptions,
      final String testMethodName)
      throws Exception {

    SchemaCrawlerOptions schemaCrawlerOptions = options;
    if (options.limitOptions().isIncludeAll(ruleForSchemaInclusion)) {
      final LimitOptionsBuilder limitOptionsBuilder =
          LimitOptionsBuilder.builder()
              .fromOptions(options.limitOptions())
              .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions = options.withLimitOptions(limitOptionsBuilder.toOptions());
    }

    final DiagramOptionsBuilder diagramOptionsBuilder = builder(diagramOptions);
    diagramOptionsBuilder.sortTables(true);
    diagramOptionsBuilder.noInfo(diagramOptions.isNoInfo());

    final Config additionalConfig = new Config();
    additionalConfig.merge(config);
    additionalConfig.merge(diagramOptionsBuilder.toConfig());
    additionalConfig.put("schemacrawler.format.hide_weakassociation_names", "true");

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setDataSource(dataSource);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    // Generate diagram, so that we have something to look at, even if
    // the DOT file comparison fails
    saveDiagram(executable, testMethodName);

    // Check DOT file
    final String referenceFileName = testMethodName;
    assertThat(
        outputOf(executableExecution(dataSource, executable, DiagramOutputFormat.scdot)),
        hasSameContentAndTypeAs(
            classpathResource(DIAGRAM_OPTIONS_OUTPUT + referenceFileName + ".dot"),
            DiagramOutputFormat.scdot));
  }

  private static void saveDiagram(
      final SchemaCrawlerExecutable executable, final String testMethodName) throws Exception {
    final Path testDiagramFile = directory.resolve(testMethodName + ".png");

    final OutputOptions oldOutputOptions = executable.getOutputOptions();
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder()
            .fromOptions(oldOutputOptions)
            .withOutputFile(testDiagramFile)
            .withOutputFormat(DiagramOutputFormat.png)
            .toOptions();

    executable.setOutputOptions(outputOptions);

    executable.execute();
  }

  @Test
  @DisplayName("Diagram with no options")
  public void executableForDiagram_00(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final DiagramOptions diagramOptions = DiagramOptionsBuilder.builder().toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with ordinal numbers and weak associations")
  public void executableForDiagram_01(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final DiagramOptionsBuilder diagramOptionsBuilder =
        builder()
            .sortTableColumns()
            .showOrdinalNumbers()
            .showForeignKeyCardinality()
            .showPrimaryKeyCardinality()
            .showFilteredTables();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions(),
        configWithWeakAssociations(),
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram no foreign key names")
  public void executableForDiagram_02(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final DiagramOptionsBuilder diagramOptionsBuilder =
        builder()
            .noForeignKeyNames()
            .showForeignKeyCardinality(true)
            .showPrimaryKeyCardinality(true);
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions(),
        configWithWeakAssociations(),
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with no-info")
  public void executableForDiagram_03(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    diagramOptionsBuilder
        .noSchemaCrawlerInfo(true)
        .showDatabaseInfo(false)
        .showJdbcDriverInfo(false);
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions(),
        configWithWeakAssociations(),
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with unqualified names")
  public void executableForDiagram_04(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    diagramOptionsBuilder.showUnqualifiedNames();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions(),
        configWithWeakAssociations(),
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with limit options")
  public void executableForDiagram_05(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeTables(new RegularExpressionInclusionRule(".*BOOKS"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final DiagramOptions diagramOptions = builder().toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with default options, with brief command")
  public void executableForDiagram_06(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final DiagramOptions diagramOptions = builder().toOptions();

    executableDiagram(
        SchemaTextDetailType.brief.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with default options, with schema command")
  public void executableForDiagram_07(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final DiagramOptions diagramOptions = builder().toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with limit options, and unqualified names")
  public void executableForDiagram_08(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeTables(new RegularExpressionInclusionRule(".*BOOKS"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());
    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    diagramOptionsBuilder.noForeignKeyNames().showUnqualifiedNames();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with limit options, and grep tables")
  public void executableForDiagram_09(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeTables(new RegularExpressionInclusionRule(".*BOOKS"));
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder()
            .includeGreppedTables(new RegularExpressionInclusionRule(".*\\.BOOKS"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withGrepOptions(grepOptionsBuilder.toOptions());

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    diagramOptionsBuilder.noForeignKeyNames().showUnqualifiedNames();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with a grep for column patterns, with primary key filtered")
  public void executableForDiagram_10a(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder()
            .includeGreppedColumns(new RegularExpressionInclusionRule(".*\\.SALES\\..*"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withGrepOptions(grepOptionsBuilder.toOptions());

    final DiagramOptions diagramOptions = builder().toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with a grep for column patterns, with foreign key filtered")
  public void executableForDiagram_10b(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder()
            .includeGreppedColumns(new RegularExpressionInclusionRule(".*\\.REGIONS\\..*"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withGrepOptions(grepOptionsBuilder.toOptions());

    final DiagramOptions diagramOptions = builder().toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with a grep for column patterns")
  public void executableForDiagram_11(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder()
            .includeGreppedColumns(new RegularExpressionInclusionRule(".*\\.REGIONS\\..*"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withGrepOptions(grepOptionsBuilder.toOptions());

    final DiagramOptions diagramOptions = builder().toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with table row counts")
  public void executableForDiagram_12(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Config additionalConfig = new Config();
    additionalConfig.put("load-row-counts", true);

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        additionalConfig,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram after setting Graphviz options for the graph")
  public void executableForDiagram_13(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final Map<String, String> graphvizAttributes = new HashMap<>();

    final String GRAPH = "graph.";
    graphvizAttributes.put(GRAPH + "fontname", "Courier");

    final DiagramOptionsBuilder diagramOptionsBuilder =
        builder().withGraphvizAttributes(graphvizAttributes);
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with maximum output, including indexes")
  public void executableForDiagram_14(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    executableDiagram(
        SchemaTextDetailType.details.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram of the FOR_LINT schema")
  public void executableForDiagram_lintschema(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(".*\\.FOR_LINT"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final DiagramOptions diagramOptions = builder().toOptions();

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        null,
        diagramOptions,
        testContext.testMethodName());
  }

  private Config configWithWeakAssociations() {
    final Config config = new Config();
    config.put("weak-associations", Boolean.TRUE);
    return config;
  }
}
