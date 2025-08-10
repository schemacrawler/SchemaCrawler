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
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
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
      final TextOutputFormat textOutputFormat,
      final DatabaseConnectionSource dataSource,
      final TestContext testContext)
      throws Exception {

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.ROUTINE_REFERENCES,
                "SELECT "
                    + "  'PUBLIC' AS ROUTINE_CATALOG,"
                    + " 'BOOKS' AS ROUTINE_SCHEMA,"
                    + "  'NEW_PUBLISHER' AS ROUTINE_NAME,"
                    + "  'NEW_PUBLISHER_FORCE_VALUE' AS SPECIFIC_NAME,"
                    + "  'PUBLIC' AS REFERENCED_OBJECT_CATALOG,"
                    + "  'BOOKS' AS REFERENCED_OBJECT_SCHEMA,"
                    + "  'AUTHORSLIST' AS REFERENCED_OBJECT_NAME,"
                    + "  NULL AS REFERENCED_OBJECT_SPECIFIC_NAME,"
                    + "  'VIEW' AS REFERENCED_OBJECT_TYPE"
                    + " FROM INFORMATION_SCHEMA.SYSTEM_TABLES"
                    + " WHERE 1=1"
                    + " LIMIT 1")
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

    final Config config = new Config();
    final SchemaTextOptionsBuilder commonTextOptions = SchemaTextOptionsBuilder.builder();
    commonTextOptions.fromConfig(config);
    commonTextOptions.noInfo();
    config.merge(commonTextOptions.toConfig());

    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder().withOutputFormat(textOutputFormat).toOptions();

    final SchemaCrawlerExecutable executable =
        new SchemaCrawlerExecutable(SchemaTextDetailType.details.name());
    executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);
    executable.setOutputOptions(outputOptions);

    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(
            classpathResource(testContext.testMethodFullName() + "." + textOutputFormat.name())));
  }
}
