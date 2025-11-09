/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.BeforeAll;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.TestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public abstract class AbstractWeakAssociationsTest {

  private static final String WEAK_ASSOCIATIONS_OUTPUT = "weak_associations_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(WEAK_ASSOCIATIONS_OUTPUT);
  }

  protected void weakAssociations_00(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {

    final Config additionalConfig = ConfigUtility.newConfig();
    additionalConfig.put("weak-associations", Boolean.TRUE);

    assertWeakAssociations(testContext, dataSource, additionalConfig, false, outputFormat);
  }

  protected void weakAssociations_01(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {

    final Config additionalConfig = ConfigUtility.newConfig();
    additionalConfig.put("attributes-file", "/attributes-weakassociations.yaml");

    assertWeakAssociations(testContext, dataSource, additionalConfig, false, outputFormat);
  }

  protected void weakAssociations_02(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {

    final Config additionalConfig = ConfigUtility.newConfig();
    additionalConfig.put("attributes-file", "/attributes-weakassociations-remarks.yaml");

    assertWeakAssociations(testContext, dataSource, additionalConfig, false, outputFormat);
  }

  protected void weakAssociations_03(
      final OutputFormat outputFormat,
      final TestContext testContext,
      final DatabaseConnectionSource dataSource)
      throws Exception {

    final Config additionalConfig = ConfigUtility.newConfig();
    additionalConfig.put("attributes-file", "/attributes-weakassociations-remarks.yaml");

    assertWeakAssociations(testContext, dataSource, additionalConfig, true, outputFormat);
  }

  private void assertWeakAssociations(
      final TestContext testContext,
      final DatabaseConnectionSource dataSource,
      final Config config,
      final boolean noRemarks,
      OutputFormat outputFormat)
      throws Exception {

    final String command = SchemaTextDetailType.schema.name();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel.withLimitOptions(
            limitOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    schemaTextOptionsBuilder.sortTables(true).noInfo().noRemarks(noRemarks);

    final Config additionalConfig = ConfigUtility.newConfig();
    additionalConfig.merge(config);
    additionalConfig.merge(schemaTextOptionsBuilder.toConfig());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setDataSource(dataSource);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    final String referenceFileName =
        WEAK_ASSOCIATIONS_OUTPUT + testContext.testMethodName() + "." + outputFormat.getFormat();
    assertThat(
        outputOf(executableExecution(dataSource, executable, outputFormat)),
        hasSameContentAndTypeAs(classpathResource(referenceFileName), outputFormat));
  }
}
