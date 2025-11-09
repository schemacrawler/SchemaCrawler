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
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ResolveTestContext
@WithTestDatabase
public class RoutineReferencesTest {

  @ParameterizedTest
  @EnumSource(
      value = TextOutputFormat.class,
      names = {"text", "html"})
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void schemaTextOutput(
      final TextOutputFormat outputFormat,
      final DatabaseConnectionSource dataSource,
      final TestContext testContext)
      throws Exception {

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.ROUTINE_REFERENCES,
                """
                SELECT
                  'PUBLIC' AS ROUTINE_CATALOG,
                  'BOOKS' AS ROUTINE_SCHEMA,
                  'NEW_PUBLISHER' AS ROUTINE_NAME,
                  'NEW_PUBLISHER_FORCE_VALUE' AS SPECIFIC_NAME,
                  'PUBLIC' AS REFERENCED_OBJECT_CATALOG,
                  'BOOKS' AS REFERENCED_OBJECT_SCHEMA,
                  'AUTHORSLIST' AS REFERENCED_OBJECT_NAME,
                  NULL AS REFERENCED_OBJECT_SPECIFIC_NAME,
                  'VIEW' AS REFERENCED_OBJECT_TYPE
                FROM
                  INFORMATION_SCHEMA.SYSTEM_TABLES
                WHERE
                  1=1
                LIMIT
                  1
                """)
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(".*\\.BOOKS"))
            .includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Config config = ConfigUtility.newConfig();
    final SchemaTextOptionsBuilder commonTextOptions = SchemaTextOptionsBuilder.builder();
    commonTextOptions.fromConfig(config);
    commonTextOptions.noInfo();
    config.merge(commonTextOptions.toConfig());

    final SchemaCrawlerExecutable executable =
        new SchemaCrawlerExecutable(SchemaTextDetailType.details.name());
    executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);

    assertThat(
        outputOf(executableExecution(dataSource, executable, outputFormat)),
        hasSameContentAs(
            classpathResource(testContext.testMethodFullName() + "." + outputFormat.name())));
  }
}
