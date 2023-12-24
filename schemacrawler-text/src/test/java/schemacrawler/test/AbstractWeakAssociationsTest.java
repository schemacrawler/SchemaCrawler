/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder.builder;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public abstract class AbstractWeakAssociationsTest {

  private static final String WEAK_ASSOCIATIONS_OUTPUT = "weak_associations_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(WEAK_ASSOCIATIONS_OUTPUT);
  }

  @Test
  @DisplayName("Inferred weak associations")
  public void weakAssociations_00(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final SchemaTextOptions schemaTextOptions = SchemaTextOptionsBuilder.builder().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("weak-associations", Boolean.TRUE);

    multipleExecutions(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        additionalConfig,
        schemaTextOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Weak associations loaded from catalog attributes file")
  public void weakAssociations_01(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final SchemaTextOptions schemaTextOptions = SchemaTextOptionsBuilder.builder().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("attributes-file", "/attributes-weakassociations.yaml");

    multipleExecutions(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        additionalConfig,
        schemaTextOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Weak associations with remarks")
  public void weakAssociations_02(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final SchemaTextOptions schemaTextOptions = SchemaTextOptionsBuilder.builder().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("attributes-file", "/attributes-weakassociations-remarks.yaml");

    multipleExecutions(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        additionalConfig,
        schemaTextOptions,
        testContext.testMethodName());
  }

  @Test
  @DisplayName("Weak associations without remarks")
  public void weakAssociations_03(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    final SchemaTextOptions schemaTextOptions =
        SchemaTextOptionsBuilder.builder().noRemarks().toOptions();

    final Config additionalConfig = new Config();
    additionalConfig.put("attributes-file", "/attributes-weakassociations-remarks.yaml");

    multipleExecutions(
        SchemaTextDetailType.schema.name(),
        dataSource,
        schemaCrawlerOptions,
        additionalConfig,
        schemaTextOptions,
        testContext.testMethodName());
  }

  protected abstract Stream<OutputFormat> outputFormats();

  private void multipleExecutions(
      final String command,
      final DatabaseConnectionSource dataSource,
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

    final Config additionalConfig = new Config();
    additionalConfig.merge(config);
    additionalConfig.merge(schemaTextOptionsBuilder.toConfig());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setDataSource(dataSource);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    final String referenceFileName = testMethodName;
    assertAll(
        outputFormats()
            .map(
                outputFormat ->
                    () -> {
                      assertThat(
                          outputOf(executableExecution(dataSource, executable, outputFormat)),
                          hasSameContentAndTypeAs(
                              classpathResource(
                                  WEAK_ASSOCIATIONS_OUTPUT
                                      + referenceFileName
                                      + "."
                                      + outputFormat.getFormat()),
                              outputFormat));
                    }));
  }
}
