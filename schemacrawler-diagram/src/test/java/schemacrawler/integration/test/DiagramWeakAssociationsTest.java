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
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder.builder;

import java.nio.file.Path;
import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class DiagramWeakAssociationsTest {

  private static final String WEAK_ASSOCIATIONS_OUTPUT = "weak_associations_output/";
  private static Path directory;

  @BeforeAll
  public static void removeOutputDir() throws Exception {
    clean(WEAK_ASSOCIATIONS_OUTPUT);
  }

  @BeforeAll
  public static void setupDirectory(final TestContext testContext) throws Exception {
    directory =
        testContext.resolveTargetFromRootPath(
            "test-output-diagrams/" + DiagramWeakAssociationsTest.class.getSimpleName());
    deleteDirectory(directory.toFile());
    createDirectories(directory);
  }

  private static void executableDiagram(
      final String command,
      final Connection connection,
      final SchemaCrawlerOptions options,
      final Config config,
      final DiagramOptions diagramOptions,
      final String testMethodName)
      throws Exception {

    SchemaCrawlerOptions schemaCrawlerOptions = options;
    if (options.getLimitOptions().isIncludeAll(ruleForSchemaInclusion)) {
      final LimitOptionsBuilder limitOptionsBuilder =
          LimitOptionsBuilder.builder()
              .fromOptions(options.getLimitOptions())
              .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
      schemaCrawlerOptions = options.withLimitOptions(limitOptionsBuilder.toOptions());
    }

    final DiagramOptionsBuilder diagramOptionsBuilder = builder(diagramOptions);
    diagramOptionsBuilder.sortTables(true);
    diagramOptionsBuilder.noInfo(diagramOptions.isNoInfo());
    diagramOptionsBuilder.weakAssociations(true);

    final Config additionalConfig = new Config();
    additionalConfig.merge(config);
    additionalConfig.merge(diagramOptionsBuilder.toConfig());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setConnection(connection);

    // Generate diagram, so that we have something to look at, even if
    // the DOT file comparison fails
    saveDiagram(executable, testMethodName);

    // Check DOT file
    final String referenceFileName = testMethodName;
    assertThat(
        outputOf(executableExecution(connection, executable, DiagramOutputFormat.scdot)),
        hasSameContentAndTypeAs(
            classpathResource(WEAK_ASSOCIATIONS_OUTPUT + referenceFileName + ".dot"),
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
  @DisplayName("Diagram with weak associations")
  public void weakAssociationsDiagram_00(final TestContext testContext, final Connection connection)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final DiagramOptions diagramOptions = DiagramOptionsBuilder.builder().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("weak-associations", Boolean.TRUE);

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        connection,
        schemaCrawlerOptions,
        additionalConfig,
        diagramOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Diagram with weak associations loaded from catalog attributes file")
  public void weakAssociationsDiagram_01(final TestContext testContext, final Connection connection)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final DiagramOptions diagramOptions = DiagramOptionsBuilder.builder().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("attributes-file", "/attributes-weakassociations-diagram.yaml");

    executableDiagram(
        SchemaTextDetailType.schema.name(),
        connection,
        schemaCrawlerOptions,
        additionalConfig,
        diagramOptions,
        testContext.testMethodName());
  }
}
