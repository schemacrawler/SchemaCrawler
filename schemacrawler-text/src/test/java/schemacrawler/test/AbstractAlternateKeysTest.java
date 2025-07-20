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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder.builder;
import org.junit.jupiter.api.BeforeAll;
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
public abstract class AbstractAlternateKeysTest {

  private static final String ALTERNATE_KEYS_OUTPUT = "alternate_keys_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(ALTERNATE_KEYS_OUTPUT);
  }

  protected void assertAlternateKeys(
      final TestContext testContext,
      final DatabaseConnectionSource dataSource,
      final OutputFormat outputFormat)
      throws Exception {

    final String command = SchemaTextDetailType.schema.name();
    final SchemaTextOptions schemaTextOptions = SchemaTextOptionsBuilder.builder().toOptions();

    final Config config = new Config();
    config.put("attributes-file", "/attributes-alternatekeys.yaml");

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.SYSTEM_LOBS|.*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel.withLimitOptions(
            limitOptionsBuilder.toOptions());

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

    final String referenceFileName =
        ALTERNATE_KEYS_OUTPUT + testContext.testMethodName() + "." + outputFormat.getFormat();
    assertThat(
        outputOf(executableExecution(dataSource, executable, outputFormat)),
        hasSameContentAndTypeAs(classpathResource(referenceFileName), outputFormat));
  }
}
