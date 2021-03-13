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

package schemacrawler.test;

import static java.nio.file.Files.createDirectories;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder.builder;

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
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class TextWeakAssociationsTest {

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
            "test-output-diagrams/" + TextWeakAssociationsTest.class.getSimpleName());
    deleteDirectory(directory.toFile());
    createDirectories(directory);
  }

  private static void executable(
      final String command,
      final Connection connection,
      final SchemaCrawlerOptions options,
      final Config config,
      final SchemaTextOptions schemaTextOptions,
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

    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = builder(schemaTextOptions);
    schemaTextOptionsBuilder.sortTables(true);
    schemaTextOptionsBuilder.noInfo(schemaTextOptions.isNoInfo());
    schemaTextOptionsBuilder.weakAssociations(true);

    final Config additionalConfig = new Config();
    additionalConfig.merge(config);
    additionalConfig.merge(schemaTextOptionsBuilder.toConfig());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setConnection(connection);

    // Check DOT file
    final String referenceFileName = testMethodName;
    assertThat(
        outputOf(executableExecution(connection, executable, TextOutputFormat.html)),
        hasSameContentAndTypeAs(
            classpathResource(WEAK_ASSOCIATIONS_OUTPUT + referenceFileName + ".html"),
            TextOutputFormat.html));
  }

  @Test
  @DisplayName("HTML with weak associations")
  public void weakAssociationsDiagram_00(final TestContext testContext, final Connection connection)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final SchemaTextOptions schemaTextOptions = SchemaTextOptionsBuilder.builder().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("weak-associations", Boolean.TRUE);

    executable(
        SchemaTextDetailType.schema.name(),
        connection,
        schemaCrawlerOptions,
        additionalConfig,
        schemaTextOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("HTML with weak associations loaded from catalog attributes file")
  public void weakAssociationsDiagram_01(final TestContext testContext, final Connection connection)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final SchemaTextOptions schemaTextOptions = SchemaTextOptionsBuilder.builder().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("attributes-file", "/attributes-weakassociations-diagram.yaml");

    executable(
        SchemaTextDetailType.schema.name(),
        connection,
        schemaCrawlerOptions,
        additionalConfig,
        schemaTextOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("HTML with weak associations with remarks")
  public void weakAssociationsDiagram_02(final TestContext testContext, final Connection connection)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final SchemaTextOptions schemaTextOptions = SchemaTextOptionsBuilder.builder().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("attributes-file", "/attributes-weakassociations-remarks.yaml");

    executable(
        SchemaTextDetailType.schema.name(),
        connection,
        schemaCrawlerOptions,
        additionalConfig,
        schemaTextOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("HTML with weak associations without remarks")
  public void weakAssociationsDiagram_03(final TestContext testContext, final Connection connection)
      throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final SchemaTextOptions schemaTextOptions =
        SchemaTextOptionsBuilder.builder().noRemarks().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("attributes-file", "/attributes-weakassociations-remarks.yaml");

    executable(
        SchemaTextDetailType.schema.name(),
        connection,
        schemaCrawlerOptions,
        additionalConfig,
        schemaTextOptions,
        testContext.testMethodName());
  }
}