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

package schemacrawler.integration.test;

import static java.nio.file.Files.createDirectories;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.plugin.EnumDataTypeInfo.EMPTY_ENUM_DATA_TYPE_INFO;
import static schemacrawler.plugin.EnumDataTypeInfo.EnumDataTypeTypes.enumerated_column;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.failTestSetup;
import static schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder.builder;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
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
      final Connection connection,
      final Catalog catalog,
      final DiagramOptions diagramOptions,
      final String testMethodName)
      throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder(diagramOptions);
    diagramOptionsBuilder.sortTables(true);
    diagramOptionsBuilder.noInfo(diagramOptions.isNoInfo());

    final Config additionalConfig = new Config();
    additionalConfig.merge(diagramOptionsBuilder.toConfig());
    additionalConfig.put("schemacrawler.format.hide_weakassociation_names", "true");

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel);
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setConnection(connection);
    executable.setCatalog(catalog);

    // Generate diagram, so that we have something to look at, even if
    // the DOT file comparison fails
    saveDiagram(executable, testMethodName);

    // Check DOT file
    final String referenceFileName = testMethodName;
    assertThat(
        outputOf(executableExecution(connection, executable, DiagramOutputFormat.scdot)),
        hasSameContentAndTypeAs(
            classpathResource(DIAGRAM_OUTPUT + referenceFileName + ".dot"),
            DiagramOutputFormat.scdot));
  }

  private static Catalog getCatalog(
      final Connection connection, final EnumDataTypeHelper enumHelper) {
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
          SchemaRetrievalOptionsBuilder.builder(schemaRetrievalOptions)
              .withEnumDataTypeHelper(enumHelper)
              .toOptions();

      final Catalog catalog =
          SchemaCrawlerUtility.getCatalog(
              connection, schemaRetrievalOptions, schemaCrawlerOptions, new Config());
      return catalog;
    } catch (final SchemaCrawlerException e) {
      return failTestSetup("Could not get catalog", e);
    }
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
  @DisplayName("Diagram with maximum output, including columns enum values")
  public void executableForDiagram_enum(final TestContext testContext, final Connection connection)
      throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog =
        getCatalog(
            connection,
            (column, columnDataType, conn) -> {
              if (column.getName().equals("FIRSTNAME")) {
                return new EnumDataTypeInfo(
                    enumerated_column, Arrays.asList("Tom", "Dick", "Harry"));
              } else {
                return EMPTY_ENUM_DATA_TYPE_INFO;
              }
            });

    executableDiagram(
        SchemaTextDetailType.details.name(),
        connection,
        catalog,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with maximum output, including indexes with remarks")
  public void executableForDiagram_indexRemarks(
      final TestContext testContext, final Connection connection) throws Exception {

    final DiagramOptionsBuilder diagramOptionsBuilder = builder();
    final DiagramOptions diagramOptions = diagramOptionsBuilder.toOptions();

    final Catalog catalog = getCatalog(connection, EnumDataTypeHelper.NO_OP_ENUM_DATA_TYPE_HELPER);
    catalog
        .lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "AUTHORS")
        .get()
        .lookupIndex("IDX_B_AUTHORS")
        .get()
        .setRemarks("Index for quick lookups by author name");

    executableDiagram(
        SchemaTextDetailType.details.name(),
        connection,
        catalog,
        diagramOptions,
        testContext.testMethodName());
  }
}
