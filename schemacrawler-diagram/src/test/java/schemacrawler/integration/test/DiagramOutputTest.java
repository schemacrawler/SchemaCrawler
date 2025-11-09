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
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.plugin.EnumDataTypeInfo.EMPTY_ENUM_DATA_TYPE_INFO;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.enumerated_column;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder.builder;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.test.utility.TestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class DiagramOutputTest {

  private static final String DIAGRAM_OUTPUT = "diagram_output/";
  private static Path directory;

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(DIAGRAM_OUTPUT);
  }

  @BeforeAll
  public static void setupDirectory(final TestContext testContext) throws Exception {
    directory =
        testContext.resolveTargetFromRootPath(
            "test-output-diagrams/" + DiagramOutputTest.class.getSimpleName());
    deleteDirectory(directory.toFile());
    createDirectories(directory);
  }

  private static void executableDiagram(
      final String command,
      final DatabaseConnectionSource dataSource,
      final Catalog catalog,
      final DiagramOptions diagramOptions,
      final String testMethodName)
      throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder(diagramOptions);
    diagramOptionsBuilder.sortTables(true);
    diagramOptionsBuilder.noInfo(diagramOptions.isNoInfo());

    final Config additionalConfig = ConfigUtility.newConfig();
    additionalConfig.merge(diagramOptionsBuilder.toConfig());
    additionalConfig.put("schemacrawler.format.hide_weakassociation_names", true);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setDataSource(dataSource);
    executable.setCatalog(catalog);

    // Generate diagram, so that we have something to look at, even if
    // the DOT file comparison fails
    saveDiagram(executable, testMethodName);

    // Check DOT file
    final String referenceFileName = testMethodName;
    assertThat(
        outputOf(executableExecution(dataSource, executable, DiagramOutputFormat.scdot)),
        hasSameContentAndTypeAs(
            classpathResource(DIAGRAM_OUTPUT + referenceFileName + ".dot"),
            DiagramOutputFormat.scdot));
  }

  private static Catalog getCatalog(
      final DatabaseConnectionSource dataSource, final EnumDataTypeHelper enumHelper) {
    SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .fromOptions(schemaCrawlerOptions.limitOptions())
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
    schemaCrawlerOptions = schemaCrawlerOptions.withLimitOptions(limitOptionsBuilder.toOptions());

    SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaCrawlerUtility.matchSchemaRetrievalOptions(dataSource);
    schemaRetrievalOptions =
        SchemaRetrievalOptionsBuilder.builder(schemaRetrievalOptions)
            .withEnumDataTypeHelper(enumHelper)
            .toOptions();

    final Catalog catalog =
        SchemaCrawlerUtility.getCatalog(
            dataSource, schemaRetrievalOptions, schemaCrawlerOptions, ConfigUtility.newConfig());
    return catalog;
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
  @DisplayName("Diagram output to a bad directory")
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void executableForDiagram_badOutputFile(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final Catalog catalog = getCatalog(dataSource, EnumDataTypeHelper.NO_OP_ENUM_DATA_TYPE_HELPER);

    final SchemaCrawlerExecutable executable =
        new SchemaCrawlerExecutable(SchemaTextDetailType.details.name());
    executable.setDataSource(dataSource);
    executable.setCatalog(catalog);
    final OutputOptionsBuilder outputOptionsBuilder =
        OutputOptionsBuilder.builder(executable.getOutputOptions())
            .withOutputFormatValue(DiagramOutputFormat.png.getFormat())
            .withOutputFile(Path.of("bad-path", "filename"));

    executable.setOutputOptions(outputOptionsBuilder.toOptions());
    executable.setDataSource(dataSource);
    final ExecutionRuntimeException runtimeException =
        assertThrows(ExecutionRuntimeException.class, () -> executable.execute());
    final Throwable exception = runtimeException.getCause();
    assertThat(exception, instanceOf(IORuntimeException.class));
    assertThat(exception.getMessage(), startsWith("Cannot write output file"));
  }

  @Test
  @DisplayName("Diagram with maximum output, including columns enum values")
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void executableForDiagram_enum(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog =
        getCatalog(
            dataSource,
            (column, columnDataType, conn) -> {
              if ("FIRSTNAME".equals(column.getName())) {
                return new EnumDataTypeInfo(enumerated_column, List.of("Tom", "Dick", "Harry"));
              }
              return EMPTY_ENUM_DATA_TYPE_INFO;
            });

    executableDiagram(
        SchemaTextDetailType.details.name(),
        dataSource,
        catalog,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with maximum output, including indexes with remarks")
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void executableForDiagram_indexRemarks(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(dataSource, EnumDataTypeHelper.NO_OP_ENUM_DATA_TYPE_HELPER);
    catalog
        .lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "AUTHORS")
        .get()
        .lookupIndex("IDX_B_AUTHORS")
        .get()
        .setRemarks("Index for quick lookups by author name");

    executableDiagram(
        SchemaTextDetailType.details.name(),
        dataSource,
        catalog,
        diagramOptions,
        testContext.testMethodName());
  }
}
