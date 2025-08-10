/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ResolveTestContext
@WithTestDatabase
public class RoutineReferencesTest {

  @ParameterizedTest
  @EnumSource(value = TextOutputFormat.class, names = {"text", "html"})
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void schemaTextOutput(final TextOutputFormat textOutputFormat,
      final DatabaseConnectionSource dataSource, final TestContext testContext) throws Exception {

    final Config config = new Config();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder()
        .includeSchemas(new RegularExpressionInclusionRule(".*\\.BOOKS"))
        .includeTables(new ExcludeAll()).includeAllRoutines();
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
        .newSchemaCrawlerOptions().withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder commonTextOptions = SchemaTextOptionsBuilder.builder();
    commonTextOptions.fromConfig(config);
    commonTextOptions.noInfo();
    config.merge(commonTextOptions.toConfig());

    final SchemaCrawlerExecutable executable =
        new SchemaCrawlerExecutable(SchemaTextDetailType.details.name());
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);

    assertThat(outputOf(executableExecution(dataSource, executable)), hasSameContentAs(
        classpathResource(testContext.testMethodFullName() + "." + textOutputFormat.name())));
  }
}
